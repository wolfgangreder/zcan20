/*
 * Copyright 2017 Wolfgang Reder.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.reder.zcan20.impl;

import com.reder.zcan20.CommandGroup;
import com.reder.zcan20.CommandMode;
import com.reder.zcan20.InterfaceOptionType;
import com.reder.zcan20.LinkState;
import com.reder.zcan20.PacketListener;
import com.reder.zcan20.PowerMode;
import com.reder.zcan20.PowerOutput;
import com.reder.zcan20.PowerState;
import com.reder.zcan20.ProviderID;
import com.reder.zcan20.ZCAN;
import com.reder.zcan20.ZCANFactory;
import com.reder.zcan20.packet.Packet;
import com.reder.zcan20.packet.PacketAdapter;
import com.reder.zcan20.packet.PacketBuilder;
import com.reder.zcan20.packet.Ping;
import com.reder.zcan20.packet.PowerInfo;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.validation.constraints.NotNull;
import org.openide.util.Lookup;

public final class ZCANImpl implements ZCAN
{

  private final class ListenerFuture<T extends PacketAdapter> implements PacketListener
  {

    private final CompletableFuture<? super T> future;
    private final int command;
    private final CommandGroup group;
    private final Class<? extends T> extensionClass;
    private final Predicate<? super Packet> packetMatcher;

    public ListenerFuture(@NotNull CompletableFuture<? super T> future,
                          CommandGroup group,
                          int command,
                          @NotNull Class<? extends T> extensionClass,
                          Predicate<? super Packet> packetMatcher)
    {
      this.future = Objects.requireNonNull(future);
      this.extensionClass = Objects.requireNonNull(extensionClass);
      this.packetMatcher = packetMatcher;
      this.command = command;
      this.group = Objects.requireNonNull(group);
    }

    @Override
    public void onPacket(ZCAN connection,
                         Packet packet)
    {
      if (packet.getCommand() == command && (packetMatcher == null || packetMatcher.test(packet))) {
        T result;
        if (extensionClass.isAssignableFrom(packet.getClass())) {
          result = extensionClass.cast(packet);
        } else {
          result = packet.getAdapter(extensionClass);
        }
        stop();
        LOGGER.log(Level.FINEST,
                   "Future complete for Packet {0}",
                   packet.toString());
        if (result != null) {
          future.complete(result);
        }
      }
    }

    void start()
    {
      addPacketListener(group,
                        this);
    }

    void stop()
    {
      removePacketListener(group,
                           this);
    }

  }

  private final class PacketNotifier implements Runnable
  {

    private final Packet packet;

    public PacketNotifier(Packet packet)
    {
      this.packet = packet;
    }

    @Override
    public void run()
    {
      try {
        port.sendPacket(packet);
      } catch (IOException ex) {
        LOGGER.log(Level.SEVERE,
                   null,
                   ex);
      }
    }

  }
  private static final Logger LOGGER = Logger.getLogger("com.reder.zcan");
  private final ZPort port;
  private final AtomicInteger threadCounter = new AtomicInteger();
  private final AtomicInteger listenerThreadCounter = new AtomicInteger();
  private final ScheduledExecutorService listenerNotifer;
  private final ExecutorService packetThread;
  private final AtomicBoolean abortFlag = new AtomicBoolean();
  private Future<?> terminateResult;
  private final Object lock = new Object();
  private final Set<PacketListener> packetListener = new CopyOnWriteArraySet<>();
  private final ConcurrentMap<CommandGroup, Set<PacketListener>> filteredPacketListener = new ConcurrentHashMap<>();
  private final AtomicInteger _masterNID = new AtomicInteger(-1);
  private final AtomicInteger session = new AtomicInteger();
  private volatile long lastPacketTimestamp = -1;
  private final short myNID;
  private ScheduledFuture<?> ownerPing;

  public ZCANImpl(@NotNull ZPort port,
                  Map<String, String> properties)
  {
    this.port = Objects.requireNonNull(port,
                                       "port is null");
    this.packetThread = Executors.newFixedThreadPool(1,
                                                     this::createPacketThread);
    this.listenerNotifer = Executors.newSingleThreadScheduledExecutor(this::createListenerThread);
    String strNid = ZCANFactory.DEFAULT_NID;
    if (properties != null) {
      strNid = properties.getOrDefault(ZCANFactory.PROP_NID,
                                       ZCANFactory.DEFAULT_NID);
    }
    myNID = Short.parseShort(strNid,
                             16);
  }

  @Override
  public Lookup getLookup()
  {
    return Lookup.EMPTY;
  }

  @Override
  public short getNID()
  {
    return myNID;
  }

  private Thread createPacketThread(Runnable r)
  {
    Thread result = new Thread(r,
                               port.getName() + "-" + threadCounter.incrementAndGet());
    result.setDaemon(true);
    return result;
  }

  private Thread createListenerThread(Runnable r)
  {
    Thread result = new Thread(r,
                               port.getName() + "-listener-" + listenerThreadCounter.incrementAndGet());
    result.setDaemon(true);
    return result;
  }

  private <T extends PacketAdapter> Future<T> doSendPacket(@NotNull Packet p,
                                                           CommandGroup group,
                                                           int command,
                                                           @NotNull Class<? extends T> resultData,
                                                           Predicate<? super Packet> packetMatcher) throws IOException
  {
    CompletableFuture<T> future = new CompletableFuture<>();
    ListenerFuture<T> lf = new ListenerFuture<>(future,
                                                group,
                                                command,
                                                resultData,
                                                packetMatcher);
    lf.start();
    port.sendPacket(p);
    return future;
  }

  private boolean isOpen()
  {
    return terminateResult != null;
  }

  public void open() throws IOException
  {
    synchronized (lock) {
      if (!isOpen()) {
        try {
          abortFlag.set(false);
          port.start();
          terminateResult = packetThread.submit(this::packetLoop);
          Future<Ping> future = doSendPacket(ZCANFactory.createPacketBuilder(myNID).
                  commandGroup(CommandGroup.NETWORK).
                  command(CommandGroup.NETWORK_PORT_OPEN).
                  commandMode(CommandMode.COMMAND).build(),
                                             CommandGroup.NETWORK,
                                             CommandGroup.NETWORK_PING,
                                             Ping.class,
                                             (p) -> p.getCommand() == CommandGroup.NETWORK_PING && p.getCommandMode()
                                                                                                           == CommandMode.EVENT);
          Ping ping = future.get(5,
                                 TimeUnit.SECONDS);
          _masterNID.set(ping.getMasterNID());
          session.set(ping.getSession());
          LOGGER.log(Level.INFO,
                     "Connected to 0x{0} Session 0x{1}",
                     new Object[]{Integer.toHexString(_masterNID.get()),
                                  Integer.toHexString(session.get())});
        } catch (InterruptedException ex) {
          Thread.currentThread().interrupt();
        } catch (ExecutionException | TimeoutException ex) {
          LOGGER.log(Level.SEVERE,
                     null,
                     ex);
        }
      }
    }
  }

  private void notfyPacketListener(Packet packet)
  {
    for (PacketListener l : packetListener) {
      l.onPacket(this,
                 packet);
    }
    Set<PacketListener> filtered = filteredPacketListener.get(packet.getCommandGroup());
    if (filtered != null) {
      for (PacketListener l : filtered) {
        l.onPacket(this,
                   packet);
      }
    }
  }

  private void packetLoop()
  {
    LOGGER.log(Level.INFO,
               "Starting packetloop");
    while (!abortFlag.get()) {
      try {
        Packet packet = port.readPacket();
        if (packet != null) {
          if (packet.getAdapter(Ping.class) != null) {
            ByteBuffer data = packet.getData();
            short nid = data.getShort();
            short type = data.getShort();
            short nsession = data.getShort();
            ProviderID id = ProviderID.valueOf(type);
            LOGGER.log(Level.FINE,
                       "Ping received nid=0x{0}, type={1}, session=0x{2}",
                       new Object[]{Integer.toHexString(nid), id.toString(), Integer.toHexString(nsession)});
            _masterNID.set(nid);
            session.set(nsession);
            lastPacketTimestamp = System.currentTimeMillis();
          }
          listenerNotifer.execute(() -> {
            notfyPacketListener(packet);
          });
        }
      } catch (IOException ex) {
        LOGGER.log(Level.SEVERE,
                   null,
                   ex);
      }
    }
    LOGGER.log(Level.INFO,
               "End Packet loop");
  }

  @Override
  public void close() throws IOException
  {
    synchronized (lock) {
      if (isOpen()) {
        if (ownerPing != null) {
          ownerPing.cancel(true);
        }
        ownerPing = null;
        abortFlag.set(true);
        try {
          terminateResult.get(10,
                              TimeUnit.SECONDS);
        } catch (InterruptedException ex) {
          Thread.currentThread().interrupt();
        } catch (ExecutionException | TimeoutException ex) {
        }
        PacketBuilder builder = ZCANFactory.createPacketBuilder(myNID);
        builder.commandGroup(CommandGroup.NETWORK);
        builder.commandMode(CommandMode.COMMAND);
        builder.command(CommandGroup.NETWORK_PORT_CLOSE);
        builder.senderNID(getNID());
        port.sendPacket(builder.build());
        terminateResult = null;
        port.close();
      }
    }
  }

  @Override
  public void addLinkStateListener(BiConsumer<ZCAN, LinkState> listener)
  {
  }

  @Override
  public void removeLinkStateListener(BiConsumer<ZCAN, LinkState> listener)
  {
  }

  @Override
  public void addPacketListener(PacketListener packetListener)
  {
    if (packetListener != null) {
      this.packetListener.add(packetListener);
    }
  }

  @Override
  public void removePacketListener(PacketListener packetListener)
  {
    if (packetListener != null) {
      this.packetListener.remove(packetListener);
    }
  }

  @Override
  public void addPacketListener(CommandGroup group,
                                PacketListener packetListener)
  {
    if (packetListener != null && group != null) {
      Set<PacketListener> filtered = filteredPacketListener.computeIfAbsent(group,
                                                                            (g) -> new CopyOnWriteArraySet<>());
      filtered.add(packetListener);
    }
  }

  @Override
  public void removePacketListener(CommandGroup group,
                                   PacketListener packetListener)
  {
    if (group != null && packetListener != null) {
      Set<PacketListener> filtered = filteredPacketListener.get(group);
      if (filtered != null) {
        filtered.remove(packetListener);
      }
    }
  }

  @Override
  public long getLastPingTimestamp()
  {
    return lastPacketTimestamp;
  }

  @Override
  public void setInterfaceOption(InterfaceOptionType type,
                                 ProviderID provider) throws IOException
  {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public void getInterfaceOption(InterfaceOptionType type) throws IOException
  {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public void getPowerStateInfo(@NotNull final PowerOutput output,
                                long timeOut,
                                @NotNull TimeUnit unit) throws IOException
  {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//    if (!isOpen()) {
//      throw new IOException("Port closed");
//    }
//    DefaultPacket.Builder builder = new DefaultPacket.Builder();
//    builder.commandGroup(CommandGroup.SYSTEM);
//    builder.commandMode(CommandMode.REQUEST);
//    builder.command(CommandGroup.SYSTEM_POWER);
//    builder.senderNID(getNID());
//    builder.data((byte) output.getMagic());
//    try {
//      Future<PowerStateInfo> future = doSendPacket(builder.build(),
//                                                   CommandGroup.SYSTEM,
//                                                   CommandGroup.SYSTEM_POWER,
//                                                   PowerStateInfo.class,
//                                                   (p) -> {
//                                                     System.err.println("Matching " + p.toString());
//                                                     PowerStateInfo info = p.getAdapter(PowerStateInfo.class);
//                                                     if (info != null) {
//                                                       return info.getOutput() == output;
//                                                     }
//                                                     return false;
//                                                   });
//      if (timeOut <= 0) {
//        return future.get();
//      } else {
//        return future.get(timeOut,
//                          unit);
//      }
//    } catch (InterruptedException ex) {
//      Thread.currentThread().interrupt();
//    } catch (ExecutionException ex) {
//      if (ex.getCause() instanceof IOException) {
//        throw (IOException) ex.getCause();
//      } else {
//        throw new IOException(ex);
//      }
//    }
//    return null;
  }

  @Override
  public void setPowerStateInfo(PowerOutput output,
                                PowerState state) throws IOException
  {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//    if (!isOpen()) {
//      throw new IOException("Port closed");
//    }
//    PacketBuilder builder = ZCANFactory.createPacketBuilder(myNID);
//    builder.commandGroup(CommandGroup.SYSTEM);
//    builder.commandMode(CommandMode.COMMAND);
//    builder.command(CommandGroup.SYSTEM_POWER);
//    builder.senderNID(getNID());
//    builder.data((byte) output.getMagic(),
//                 (byte) state.getMagic());
  }

//  @Override
  public Future<PowerInfo> getOutputState(PowerOutput output) throws IOException, InterruptedException
  {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//    PacketBuilder builder = ZCANFactory.createPacketBuilder(myNID);
//    builder.commandGroup(CommandGroup.CONFIG);
//    builder.commandMode(CommandMode.REQUEST);
//    builder.command(CommandGroup.CONFIG_POWER_INFO);
//    builder.senderNID(getNID());
//    byte b = 0;
//    switch (output) {
//      case BOOSTER:
//        b = (byte) 2;
//        break;
//      case OUT_1:
//        b = (byte) 0;
//        break;
//      case OUT_2:
//        b = (byte) 1;
//        break;
//    }
//    builder.data(b);
//    return doSendPacket(builder.build(),
//                        CommandGroup.CONFIG,
//                        CommandGroup.CONFIG_POWER_INFO,
//                        PowerInfo.class,
//                        (p) -> p.getCommand() == CommandGroup.CONFIG_POWER_INFO && p.getCommandMode() == CommandMode.ACK);
  }

  //@Override
  public List<Future<PowerInfo>> getOutputState(Collection<? extends PowerOutput> outputs) throws IOException,
                                                                                                  InterruptedException
  {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//    if (!isOpen()) {
//      throw new IOException("Port closed");
//    }
//    List<Future<PowerInfo>> result = new ArrayList<>(outputs.size());
//    for (PowerOutput out : outputs) {
//      if (out != null) {
//        result.add(getOutputState(out));
//      }
//    }
//    return result;
  }

  //@Override
  public List<Future<PowerInfo>> setOutputState(Map<PowerOutput, PowerMode> modes) throws IOException
  {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public void loadDump() throws IOException
  {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public void readCV(int address,
                     int cv) throws IOException
  {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//    PacketBuilder builder = ZCANFactory.createPacketBuilder(myNID);
//    builder.commandGroup(CommandGroup.TRACK_CONFIG_PUBLIC);
//    builder.command(CommandGroup.TSE_PROG_READ);
//    builder.commandMode(CommandMode.REQUEST);
////    builder.address((short) getNID());//_masterNID.get());
//    ByteBuffer buffer = ByteBuffer.allocate(6);
//    buffer.order(ByteOrder.LITTLE_ENDIAN);
//    buffer.putShort((short) address);
//    buffer.putInt(cv);
//    builder.data(buffer.array());
//    port.sendPacket(builder.build());
  }

  @Override
  public void writeCV(int address,
                      int cv,
                      int value) throws IOException
  {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public void getMode(int address) throws IOException
  {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//    try {
//      DefaultPacket.Builder builder = new DefaultPacket.Builder();
//      builder.commandGroup(CommandGroup.LOCO);
//      builder.command(CommandGroup.LOCO_ACTIVE);
//      builder.commandMode(CommandMode.REQUEST);
//      ByteBuffer buffer = ByteBuffer.allocate(2);
//      buffer.order(ByteOrder.LITTLE_ENDIAN);
//      buffer.putShort((short) address);
//      builder.data(buffer.array());
//      Future<Packet> f = doSendPacket(builder.build(),
//                                      CommandGroup.LOCO,
//                                      CommandGroup.LOCO_ACTIVE,
//                                      Packet.class,
//                                      (Packet p) -> {
//                                        ByteBuffer b = p.getData();
//                                        return b.remaining() >= 2 && b.getShort(2) == address;
//                                      });
//      boolean used = false;
//      try {
//        Packet usedPacket = f.get(500,
//                                  TimeUnit.MILLISECONDS);
//        used = usedPacket.getData().get(2) != 0;
//      } catch (TimeoutException ex) {
//      }
//      builder = new DefaultPacket.Builder();
//      builder.commandGroup(CommandGroup.LOCO);
//      builder.command(CommandGroup.LOCO_MODE);
//      builder.commandMode(CommandMode.REQUEST);
//      builder.data(buffer.array());
//      f = doSendPacket(builder.build(),
//                       CommandGroup.LOCO,
//                       CommandGroup.LOCO_MODE,
//                       Packet.class,
//                       (Packet p) -> {
//                         ByteBuffer b = p.getData();
//                         return b.remaining() >= 5 && b.getShort(2) == address;
//                       });
//      Packet modePacket = f.get(500,
//                                TimeUnit.MILLISECONDS);
//      return new LocoModeImpl(modePacket,
//                              used);
//    } catch (InterruptedException ex) {
//      Thread.currentThread().interrupt();
//      return null;
//    } catch (ExecutionException ex) {
//      if (ex.getCause() instanceof IOException) {
//        throw (IOException) ex.getCause();
//      } else {
//        throw new IOException(ex);
//      }
//    } catch (TimeoutException ex) {
//      return null;
//    }
  }

  @Override
  public void takeOwnership(int address) throws IOException
  {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//    try {
//      synchronized (lock) {
//        if (ownerPing != null) {
//          ownerPing.cancel(true);
//        }
//        ownerPing = null;
//      }
//      DefaultPacket.Builder builder = new DefaultPacket.Builder();
//      Future<Packet> f = doSendPacket(builder.buildTakeOwnership(getNID(),
//                                                                 (short) address),
//                                      CommandGroup.LOCO,
//                                      CommandGroup.LOCO_ACTIVE,
//                                      Packet.class,
//                                      (Packet p) -> {
//                                        ByteBuffer b = p.getData();
//                                        return b.remaining() >= 2 && b.getShort(2) == address;
//                                      });
//      boolean used = false;
//      try {
//        Packet usedPacket = f.get(500,
//                                  TimeUnit.MILLISECONDS);
//        used = usedPacket.getData().get(2) != 0;
//      } catch (TimeoutException ex) {
//      }
//      //if (!used) {
//      ownerPing = listenerNotifer.scheduleWithFixedDelay(new PacketNotifier(new DefaultPacket.Builder().
//              buildOwnerShipEvent(getNID(),
//                                  (short) address)),
//                                                         0,
//                                                         500,
//                                                         TimeUnit.MILLISECONDS);
//      builder = new DefaultPacket.Builder();
//      builder.commandGroup(CommandGroup.LOCO);
//      builder.command(CommandGroup.LOCO_MODE);
//      builder.commandMode(CommandMode.COMMAND);
//      LocoModeImpl lm = new LocoModeImpl.Builder(builder).build(address,
//                                                                used);
//      port.sendPacket(lm.getPacket());
//      return lm;
////      f = doSendPacket(lm.getPacket(),
////                       CommandGroup.LOCO,
////                       CommandGroup.LOCO_MODE,
////                       Packet.class,
////                       (Packet p) -> {
////                         ByteBuffer b = p.getData();
////                         return b.remaining() >= 5 && b.getShort(2) == address;
////                       });
////      Packet modePacket = f.get(500,
////                                TimeUnit.MILLISECONDS);
////      return new LocoModeImpl(modePacket,
////                              used);
////      } else {
////        throw new IOException("Cannot take ownership");
////      }
////    } catch (TimeoutException ex) {
////      return null;
//    } catch (ExecutionException ex) {
//      if (ex.getCause() instanceof IOException) {
//        throw (IOException) ex.getCause();
//      } else {
//        throw new IOException(ex);
//      }
//    } catch (InterruptedException ex) {
//      Thread.currentThread().interrupt();
//      return null;
//    }
  }

}
