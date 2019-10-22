/*
 * Copyright 2017-2019 Wolfgang Reder.
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
package at.or.reder.zcan20.impl;

import at.or.reder.zcan20.CanId;
import at.or.reder.zcan20.CommandGroup;
import at.or.reder.zcan20.CommandMode;
import at.or.reder.zcan20.LinkState;
import at.or.reder.zcan20.LinkStateListener;
import at.or.reder.zcan20.PacketListener;
import at.or.reder.zcan20.PowerMode;
import at.or.reder.zcan20.PowerPort;
import at.or.reder.zcan20.ZCAN;
import at.or.reder.zcan20.ZCANFactory;
import at.or.reder.zcan20.packet.CVInfoAdapter;
import at.or.reder.zcan20.packet.Packet;
import at.or.reder.zcan20.packet.PacketAdapter;
import at.or.reder.zcan20.packet.PacketBuilder;
import at.or.reder.zcan20.packet.Ping;
import at.or.reder.zcan20.util.CanIdMatcher;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.validation.constraints.NotNull;
import org.openide.util.Exceptions;
import org.openide.util.RequestProcessor;

public final class ZCANImpl implements ZCAN
{

  private final class FilterListener implements PacketListener
  {

    private final PacketListener listener;
    private final Predicate<? super Packet> matcher;

    public FilterListener(PacketListener listener,
                          Predicate<? super Packet> matcher)
    {
      this.listener = Objects.requireNonNull(listener,
                                             "listener is null");
      this.matcher = Objects.requireNonNull(matcher,
                                            "matcher is null");
    }

    @Override
    public void onPacket(ZCAN connection,
                         Packet packet)
    {
      if (matcher.test(packet)) {
        listener.onPacket(connection,
                          packet);
      }
    }

    @Override
    public int hashCode()
    {
      int hash = 7;
      hash = 67 * hash + Objects.hashCode(this.listener);
      hash = 67 * hash + Objects.hashCode(this.matcher);
      return hash;
    }

    @Override
    public boolean equals(Object obj)
    {
      if (this == obj) {
        return true;
      }
      if (obj == null) {
        return false;
      }
      if (getClass() != obj.getClass()) {
        return false;
      }
      final FilterListener other = (FilterListener) obj;
      if (!Objects.equals(this.listener,
                          other.listener)) {
        return false;
      }
      return Objects.equals(this.matcher,
                            other.matcher);
    }

  }

  private final class FutureListener<T extends PacketAdapter> implements PacketListener
  {

    private final CompletableFuture<? super T> future;
    private final Predicate<? super Packet> matcher;
    private final Class<? extends T> extensionClass;

    public FutureListener(@NotNull CompletableFuture<? super T> future,
                          Predicate<? super Packet> matcher,
                          @NotNull Class<? extends T> extensionClass)
    {
      this.future = Objects.requireNonNull(future);
      this.extensionClass = Objects.requireNonNull(extensionClass);
      this.matcher = matcher;
    }

    @Override
    public void onPacket(ZCAN connection,
                         Packet packet)
    {
      if (matcher.test(packet)) {
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
      addPacketListener(this);
    }

    void stop()
    {
      removePacketListener(this);
    }

  }

  private static final Logger LOGGER = Logger.getLogger("at.or.reder.zcan20");
  private final ZPort port;
  private final AtomicInteger threadCounter = new AtomicInteger();
  private final AtomicInteger listenerThreadCounter = new AtomicInteger();
  private final ExecutorService listenerNotifer;
  private final ExecutorService packetThread;
  private final AtomicBoolean abortFlag = new AtomicBoolean();
  private Future<?> terminateResult;
  private final Object lock = new Object();
  private final Set<PacketListener> packetListener = new CopyOnWriteArraySet<>();
  private final ConcurrentMap<CommandGroup, Set<PacketListener>> filteredPacketListener = new ConcurrentHashMap<>();
  private final AtomicReference<Short> masterNID = new AtomicReference<>((short) -1);
  private final AtomicInteger masterUID = new AtomicInteger();
  private final AtomicInteger session = new AtomicInteger();
  private volatile long lastPacketTimestamp = -1;
  private final short myNID;
  private volatile RequestProcessor.Task ownerPing;
  private volatile int ownerPingIntervall = 5000;
  private volatile int ownerPingJitter = 100;
  private final Random jitterRandom = new Random();
  private volatile LinkState linkState = LinkState.CLOSED;
  private final RequestProcessor requestProcessor;
  private final RequestProcessor.Task disconnectTimer;
  private volatile int linkTimeout = 10000;
  private final Set<LinkStateListener> linkStateListener = new CopyOnWriteArraySet<>();

  public ZCANImpl(@NotNull ZPort port,
                  Map<String, String> properties)
  {
    this.port = Objects.requireNonNull(port,
                                       "port is null");
    this.packetThread = Executors.newFixedThreadPool(1,
                                                     this::createPacketThread);
    this.listenerNotifer = Executors.newSingleThreadExecutor(this::createListenerThread);
    String strNid = ZCANFactory.DEFAULT_NID;
    if (properties != null) {
      strNid = properties.getOrDefault(ZCANFactory.PROP_NID,
                                       ZCANFactory.DEFAULT_NID);
    }
    myNID = (short) Integer.parseInt(strNid,
                                     16);
    requestProcessor = new RequestProcessor(port.getName(),
                                            Runtime.getRuntime().availableProcessors(),
                                            true,
                                            true);
    disconnectTimer = requestProcessor.create(this::onLinkTimeout);
  }

  @Override
  public PacketBuilder createPacketBuilder()
  {
    return ZCANFactory.createPacketBuilder(myNID);
  }

  @Override
  public short getNID()
  {
    return myNID;
  }

  private Thread createPacketThread(Runnable r)
  {
    Thread result = new Thread(r,
                               port.getName() + "-" + threadCounter.
                               incrementAndGet());
    result.setDaemon(true);
    return result;
  }

  private Thread createListenerThread(Runnable r)
  {
    Thread result = new Thread(r,
                               port.getName() + "-listener-"
                                       + listenerThreadCounter.incrementAndGet());
    result.setDaemon(true);
    return result;
  }

  private void doSendPacket(@NotNull Packet p) throws IOException
  {
    if (!isOpen()) {
      throw new IOException("Port is not open");
    }
    port.sendPacket(p);
    // Wir habe etwas gesendet, also kann der ping warten...
    schedulePing();
  }

  private <T extends PacketAdapter> Future<T> doSendPacket(@NotNull Packet p,
                                                           CanIdMatcher matcher,
                                                           @NotNull Class<? extends T> resultData) throws IOException
  {
    if (!isOpen()) {
      throw new IOException("Port is not open");
    }
    CompletableFuture<T> future = new CompletableFuture<>();
    FutureListener<T> lf = new FutureListener<>(future,
                                                matcher,
                                                resultData);
    lf.start();
    port.sendPacket(p);
    // Wir habe etwas gesendet, also kann der ping warten...
    schedulePing();
    return future;
  }

  private <T extends PacketAdapter> T sendReceive(Packet packet,
                                                  Predicate<? super Packet> matcher,
                                                  Class<? extends T> adapterClass,
                                                  long timeout) throws IOException
  {
    CompletableFuture<T> future = new CompletableFuture<>();
    FutureListener<T> lf = new FutureListener<>(future,
                                                matcher,
                                                adapterClass);
    lf.start();
    port.sendPacket(packet);
    // Wir habe etwas gesendet, also kann der ping warten...

    schedulePing();
    try {
      return future.get(timeout,
                        TimeUnit.MILLISECONDS);
    } catch (InterruptedException ex) {
      Thread.currentThread().
              interrupt();
      return null;
    } catch (ExecutionException ex) {
      if (ex.getCause() instanceof IOException) {
        throw (IOException) ex.getCause();
      } else {
        throw new IOException(ex);
      }
    } catch (TimeoutException ex) {
      return null;
    }
  }

  private boolean isOpen()
  {
    return terminateResult != null;
  }

  @Override
  public boolean sendPing()
  {
    try {
      doSendPacket(createPacketBuilder().
              buildPingPacket(myNID),
                   new CanIdMatcher(CanId.valueOf(
                           CommandGroup.NETWORK,
                           CommandGroup.NETWORK_PING,
                           CommandMode.EVENT,
                           (short) 0),
                                    CanIdMatcher.MASK_NO_ADDRESS),
                   Ping.class);
    } catch (IOException ex) {
      Exceptions.printStackTrace(ex);
      return false;
    }
    return true;
  }

  private void postPing() throws IOException
  {
    doSendPacket(createPacketBuilder().
            buildPingPacket(myNID));
  }

  @Override
  public boolean isAutopingEnabled()
  {
    return ownerPing != null;
  }

  private int getScheduleTime()
  {
    return ownerPingIntervall + jitterRandom.nextInt((int) ownerPingJitter) - (ownerPingJitter / 2);
  }

  private void schedulePing()
  {
    if (ownerPing != null) {
      int nextSchedule = getScheduleTime();
      LOGGER.log(Level.FINER,
                 "Schedule ping in {0,number,0} ms",
                 new Object[]{nextSchedule});
      ownerPing.schedule(nextSchedule);
    }
  }

  private void onAutomaticPing()
  {
    try {
      postPing();
    } catch (IOException ex) {
      LOGGER.log(Level.WARNING,
                 "onAutomaticPing:" + ex.getMessage(),
                 ex);
    } finally {
      schedulePing();
    }
  }

  @Override
  public void setAutopingEnabled(boolean e)
  {
    if (e != isAutopingEnabled()) {
      if (e && isOpen()) {
        if (ownerPing == null) {
          ownerPing = requestProcessor.post(this::onAutomaticPing,
                                            getScheduleTime());
        }
      } else if (ownerPing != null) {
        ownerPing.cancel();
        ownerPing = null;
      }
    }
  }

  @Override
  public int getAutopingIntervall()
  {
    return (int) (ownerPingIntervall / 1000);
  }

  @Override
  public void setAutopingIntervall(int autoPingIntervall)
  {
    this.ownerPingIntervall = autoPingIntervall * 1000;
  }

  public void open(long timeout,
                   TimeUnit unit) throws IOException
  {
    synchronized (lock) {
      if (!isOpen()) {
        try {
          abortFlag.set(false);
          port.start();
          terminateResult = packetThread.submit(this::packetLoop);
          Future<Ping> future = doSendPacket(createPacketBuilder().buildLoginPacket(),
                                             new CanIdMatcher(CanId.valueOf(
                                                     CommandGroup.NETWORK,
                                                     CommandGroup.NETWORK_PING,
                                                     CommandMode.EVENT,
                                                     (short) 0),
                                                              CanIdMatcher.MASK_NO_ADDRESS),
                                             Ping.class);
          Ping ping = future.get(timeout,
                                 unit);
          masterNID.set(ping.getPacket().
                  getSenderNID());
          session.set(ping.getSession());
          LOGGER.log(Level.INFO,
                     "Connected to 0x{0} Session 0x{1}",
                     new Object[]{Integer.toHexString(masterNID.get() & 0xffff),
                                  Integer.toHexString(session.get() & 0xffff)});
          setLinkState(LinkState.OPEN);
          disconnectTimer.schedule(linkTimeout);
          setAutopingEnabled(true);
        } catch (InterruptedException ex) {
          Thread.currentThread().interrupt();
          close();
        } catch (ExecutionException | TimeoutException ex) {
          close();
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
      try {
        l.onPacket(this,
                   packet);
      } catch (Throwable th) {
        LOGGER.log(Level.SEVERE,
                   "Error while dispatch packet:" + th.getMessage(),
                   th);
      }
    }
    Set<PacketListener> filtered = filteredPacketListener.get(packet.getCommandGroup());
    if (filtered != null) {
      for (PacketListener l : filtered) {
        try {
          l.onPacket(this,
                     packet);
        } catch (Throwable th) {
          LOGGER.log(Level.SEVERE,
                     "Error while dispatch packet:" + th.getMessage(),
                     th);
        }
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
          disconnectTimer.schedule(linkTimeout);
          if (packet.getAdapter(Ping.class) != null) {
            handlePing(packet.getAdapter(Ping.class));
          } else {
            LOGGER.log(Level.FINER,
                       packet.toString());
          }
          listenerNotifer.execute(() -> {
            notfyPacketListener(packet);
          });
        }
      } catch (IOException ex) {
        LOGGER.log(Level.SEVERE,
                   "IOException in packetLoop",
                   ex);
      } catch (Throwable ex) {
        LOGGER.log(Level.SEVERE,
                   "Exception in packetLoop",
                   ex);
      }
    }
    LOGGER.log(Level.INFO,
               "End Packet loop");
  }

  private void handlePing(Ping packet)
  {
    int nid = packet.getMasterNID();
    short nsession = packet.getSession();
    if (!masterNID.compareAndSet((short) 0xc0a6,
                                 packet.getPacket().getSenderNID())) {
      masterNID.compareAndSet((short) 0,
                              packet.getPacket().getSenderNID());
    }
    masterUID.set(packet.getMasterNID());
    session.set(nsession);
    lastPacketTimestamp = System.currentTimeMillis();
    LOGGER.log(Level.FINE,
               "Ping received uid=0x{0}, nid=0x{3}, type=0x{1}, session=0x{2}",
               new Object[]{Integer.toHexString(packet.getPacket().getSenderNID() & 0xffff),
                            Integer.toHexString(packet.getType() & 0xffff),
                            Integer.toHexString(nsession),
                            Integer.toHexString(nid)});
  }

  @Override
  public void close() throws IOException
  {
    synchronized (lock) {
      if (isOpen()) {
        try (port) {
          if (ownerPing != null) {
            ownerPing.cancel();
          }
          ownerPing = null;
          abortFlag.set(true);
          try {
            terminateResult.get(10,
                                TimeUnit.SECONDS);
          } catch (InterruptedException ex) {
            Thread.currentThread().
                    interrupt();
          } catch (ExecutionException | TimeoutException ex) {
          }
          PacketBuilder builder = ZCANFactory.createPacketBuilder(myNID);
          builder.commandGroup(CommandGroup.NETWORK);
          builder.commandMode(CommandMode.COMMAND);
          builder.command(CommandGroup.NETWORK_PORT_CLOSE);
          builder.senderNID(getNID());
          port.sendPacket(builder.build());
          terminateResult = null;
          setLinkState(LinkState.CLOSED);
          disconnectTimer.cancel();
        }
      }
    }
  }

  @Override
  public LinkState getLinkState()
  {
    return linkState;
  }

  @Override
  public void addLinkStateListener(LinkStateListener listener)
  {
    if (listener != null) {
      linkStateListener.add(listener);
    }
  }

  @Override
  public void removeLinkStateListener(LinkStateListener listener)
  {
    linkStateListener.remove(listener);
  }

  private void onLinkTimeout()
  {
    setLinkState(LinkState.BROKEN);
  }

  private void setLinkState(LinkState state)
  {
    if (state != linkState) {
      this.linkState = state;
      for (LinkStateListener l : linkStateListener) {
        l.onLinkStateChanged(this,
                             state);
      }
    }
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
  public void addPacketListener(Predicate<? super Packet> matcher,
                                PacketListener packetListener)
  {
    if (matcher != null && packetListener != null) {
      addPacketListener(new FilterListener(packetListener,
                                           matcher));
    }
  }

  @Override
  public void removePacketListener(Predicate<? super Packet> matcher,
                                   PacketListener packetListener)
  {
    if (matcher != null && packetListener != null) {
      removePacketListener(new FilterListener(packetListener,
                                              matcher));
    }
  }

  @Override
  public long getLastPingTimestamp()
  {
    return lastPacketTimestamp;
  }

  @Override
  public void getPowerStateInfo(@NotNull final Collection<PowerPort> output) throws IOException
  {
    if (Collections.frequency(Objects.requireNonNull(output,
                                                     "output is null"),
                              null) > 0) {
      throw new NullPointerException("output contains null");
    }
    if (output.stream().filter((o) -> !o.isValidInSet()).findAny().isPresent()) {
      throw new IllegalArgumentException(
              "output is not valid for set/request methods");
    }
    PacketBuilder builder = createPacketBuilder();
    Packet packet = builder.buildSystemPowerInfoPacket(masterNID.get(),
                                                       output);
    doSendPacket(packet);
  }

  @Override
  public void setPowerModeInfo(PowerPort output,
                               PowerMode state) throws IOException
  {
    Objects.requireNonNull(output,
                           "output is null");
    if (!output.isValidInSet()) {
      throw new IllegalArgumentException(
              "output is not valid for set/request methods");
    }
    Objects.requireNonNull(state,
                           "state is null");
    PacketBuilder builder = createPacketBuilder();
    Packet packet = builder.buildSystemPowerInfoPacket(masterNID.get(),
                                                       Collections.singleton(output),
                                                       state);
    doSendPacket(packet);
  }

  @Override
  public void readCV(short address,
                     int cv) throws IOException
  {
    PacketBuilder builder = createPacketBuilder();
    Packet packet = builder.buildReadCVPacket(masterNID.get(),
                                              address,
                                              cv);
    doSendPacket(packet);
  }

  @Override
  public CVInfoAdapter readCV(short address,
                              int cv,
                              long timeout,
                              Predicate<? super Packet> packetMatcher) throws IOException
  {
    Packet packet = createPacketBuilder().
            buildReadCVPacket(masterNID.get(),
                              address,
                              cv);
    return sendReceive(packet,
                       new CanIdMatcher(CanId.valueOf(
                               CommandGroup.TRACK_CONFIG_PRIVATE,
                               CommandGroup.TSE_PROG_READ,
                               CommandMode.ACK,
                               masterNID.get()),
                                        CanIdMatcher.MASK_NO_ADDRESS
                                                & (~CanIdMatcher.MASK_COMMAND)).and(
                               packetMatcher),
                       CVInfoAdapter.class,
                       timeout);
  }

  @Override
  public void writeCV(short address,
                      int cv,
                      short value) throws IOException
  {
    Packet packet = createPacketBuilder().
            buildWriteCVPacket(masterNID.get(),
                               address,
                               cv,
                               value);
    doSendPacket(packet);
  }

  @Override
  public CVInfoAdapter writeCV(short address,
                               int cv,
                               short value,
                               long timeout) throws IOException
  {
    Packet packet = createPacketBuilder().
            buildWriteCVPacket(masterNID.get(),
                               address,
                               cv,
                               value);
    return sendReceive(packet,
                       new CanIdMatcher(CanId.valueOf(
                               CommandGroup.TRACK_CONFIG_PRIVATE,
                               CommandGroup.TSE_PROG_WRITE,
                               CommandMode.ACK,
                               masterNID.get()),
                                        CanIdMatcher.MASK_NO_ADDRESS
                                                & (~CanIdMatcher.MASK_COMMAND)),
                       CVInfoAdapter.class,
                       timeout);
  }

}
