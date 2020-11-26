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

import at.or.reder.dcc.LinkState;
import at.or.reder.dcc.LinkStateListener;
import at.or.reder.dcc.NotConnectedException;
import at.or.reder.dcc.util.CanIdMatcher;
import at.or.reder.zcan20.CanId;
import at.or.reder.zcan20.CommandGroup;
import at.or.reder.zcan20.CommandMode;
import at.or.reder.zcan20.MX10PropertiesSet;
import at.or.reder.zcan20.PacketListener;
import at.or.reder.zcan20.TrackConfig;
import at.or.reder.zcan20.ZCAN;
import at.or.reder.zcan20.ZCANFactory;
import at.or.reder.zcan20.packet.Packet;
import at.or.reder.zcan20.packet.PacketAdapter;
import at.or.reder.zcan20.packet.PacketBuilder;
import at.or.reder.zcan20.packet.Ping;
import at.or.reder.zcan20.packet.ZCANDecoderPacketMatcher;
import java.io.IOException;
import java.nio.ByteBuffer;
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
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;
import java.util.logging.Level;
import javax.validation.constraints.NotNull;
import org.openide.util.Lookup;
import org.openide.util.RequestProcessor;
import org.openide.util.lookup.Lookups;

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
  private final ZPort port;
  private final AtomicInteger threadCounter = new AtomicInteger();
  private final AtomicInteger listenerThreadCounter = new AtomicInteger();
  private final ExecutorService listenerNotifer;
  private final ExecutorService packetThread;
  private final AtomicBoolean abortFlag = new AtomicBoolean();
  private Future<?> terminateResult;
  private final Set<PacketListener> packetListener = new CopyOnWriteArraySet<>();
  private final ConcurrentMap<CommandGroup, Set<PacketListener>> filteredPacketListener = new ConcurrentHashMap<>();
  private final AtomicReference<Short> masterNID = new AtomicReference<>((short) -1);
  private final AtomicInteger masterUID = new AtomicInteger();
  private final AtomicInteger session = new AtomicInteger();
  private final short myNID;
  private volatile LinkState linkState = LinkState.CLOSED;
  private final RequestProcessor requestProcessor;
  private final RequestProcessor.Task disconnectTimer;
  private volatile int linkTimeout = 10000;
  private final Set<LinkStateListener> linkStateListener = new CopyOnWriteArraySet<>();
  private final NetworkControlImpl networkControl;
  private final SystemControlImpl systemControl;
  private final TrackConfig trackConfig;
  private final LocoControlImpl locoControl;
  private final ZAccessoryControlImpl accessoryImpl;
  private final Lookup myLookup;
  private final Object lock;
  private final String appName;

  public ZCANImpl(@NotNull ZPort port,
                  Map<String, String> properties,
                  Object lock)
  {
    this.lock = lock != null ? lock : new Object();
    this.port = Objects.requireNonNull(port,
                                       "port is null");
    this.packetThread = Executors.newFixedThreadPool(1,
                                                     this::createPacketThread);
    this.listenerNotifer = Executors.newSingleThreadExecutor(this::createListenerThread);
    String strNid = ZCANFactory.DEFAULT_NID;
    if (properties != null) {
      strNid = properties.getOrDefault(ZCANFactory.PROP_NID,
                                       ZCANFactory.DEFAULT_NID);
      appName = properties.get(MX10PropertiesSet.PROP_APPNAME);
    } else {
      appName = null;
    }
    myNID = (short) Integer.parseInt(strNid,
                                     16);
    requestProcessor = new RequestProcessor(port.getName(),
                                            Runtime.getRuntime().availableProcessors(),
                                            true,
                                            true);
    disconnectTimer = requestProcessor.create(this::onLinkTimeout);
    networkControl = new NetworkControlImpl(this);
    systemControl = new SystemControlImpl(this);
    trackConfig = new TrackConfigImpl(this);
    locoControl = new LocoControlImpl(this);
    accessoryImpl = new ZAccessoryControlImpl(this);
    myLookup = Lookups.fixed(port,
                             networkControl,
                             systemControl,
                             trackConfig,
                             locoControl,
                             requestProcessor,
                             accessoryImpl);
  }

  @Override
  public String getAppName()
  {
    return appName;
  }

  @Override
  public Lookup getLookup()
  {
    return myLookup;
  }

  Object getLock()
  {
    return lock;
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

  <T extends PacketAdapter> Future<T> doSendRaw(@NotNull ByteBuffer buffer,
                                                Predicate<? super Packet> matcher,
                                                Class<? extends T> resultData) throws IOException
  {
    if (!isOpen()) {
      throw new NotConnectedException();
    }
    CompletableFuture<T> future = null;
    if (matcher != null && resultData != null) {
      future = new CompletableFuture<>();
      FutureListener<T> lf = new FutureListener<>(future,
                                                  matcher,
                                                  resultData);
      lf.start();
    }
    port.sendRaw(buffer);
    // Wir habe etwas gesendet, also kann der ping warten...
    networkControl.schedulePing();
    return future;
  }

  void doSendPacket(@NotNull Packet p) throws IOException
  {
    doSendPacket(p,
                 null,
                 null);
  }

  <T extends PacketAdapter> Future<T> doSendPacket(@NotNull Packet p,
                                                   Predicate<? super Packet> matcher,
                                                   Class<? extends T> resultData) throws IOException
  {
    if (!isOpen()) {
      throw new NotConnectedException();
    }
    CompletableFuture<T> future = null;
    if (matcher != null && resultData != null) {
      future = new CompletableFuture<>();
      FutureListener<T> lf = new FutureListener<>(future,
                                                  matcher,
                                                  resultData);
      lf.start();
    }
    port.sendPacket(p);
    // Wir habe etwas gesendet, also kann der ping warten...
    networkControl.schedulePing();
    return future;
  }

  <T extends PacketAdapter> T sendReceive(Packet packet,
                                          Predicate<? super Packet> matcher,
                                          Class<? extends T> adapterClass,
                                          long timeout) throws IOException
  {
    Future<T> future = doSendPacket(packet,
                                    matcher,
                                    adapterClass);
    try {
      return future.get(timeout,
                        TimeUnit.MILLISECONDS);
    } catch (InterruptedException ex) {
      Thread.currentThread().interrupt();
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

  boolean isOpen()
  {
    return terminateResult != null;
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
          Future<Ping> future = doSendPacket(createPacketBuilder().buildLoginPacket(getAppName()),
                                             new CanIdMatcher(CanId.valueOf(
                                                     CommandGroup.NETWORK,
                                                     CommandGroup.NETWORK_PING,
                                                     CommandMode.EVENT,
                                                     (short) 0),
                                                              CanIdMatcher.MASK_NO_ADDRESS),
                                             Ping.class);
          Ping ping = future.get(timeout,
                                 unit);
          masterNID.set(ping.getPacket().getSenderNID());
          session.set(ping.getSession());
          LOGGER.log(Level.INFO,
                     "Connected to 0x{0} Session 0x{1}",
                     new Object[]{Integer.toHexString(masterNID.get() & 0xffff),
                                  Integer.toHexString(session.get() & 0xffff)});
          setLinkState(LinkState.CONNECTED);
          disconnectTimer.schedule(linkTimeout);
          networkControl.setAutopingEnabled(true);
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
    LOGGER.log(Level.FINEST,
               () -> "Dispatch packet " + packet + " to unfiltered listeners");
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
      LOGGER.log(Level.FINEST,
                 () -> "Dispatch packet " + packet + " to filtered listeners");
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
          networkControl.close();
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
        } finally {
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
        l.onLinkStateChanged(null,
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

  short getMasterNID()
  {
    return masterNID.get();
  }

  RequestProcessor.Task postTask(Runnable run,
                                 long timeout)
  {
    return requestProcessor.post(run,
                                 linkTimeout);
  }

  @Override
  public Predicate<Packet> getAccessoryDecoderPacketMatcher(int decoderAddress)
  {
    return ZCANDecoderPacketMatcher.getAccessoryInstance(decoderAddress);
  }

  @Override
  public Predicate<Packet> getLocoDecoderPacketMatcher(int decoderAddress)
  {
    return ZCANDecoderPacketMatcher.getLocomotiveInstance(decoderAddress);
  }

  @Override
  public String toString()
  {
    return port.getName();
  }

}
