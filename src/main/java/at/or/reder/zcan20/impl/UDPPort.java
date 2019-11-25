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
package at.or.reder.zcan20.impl;

import at.or.reder.dcc.NotConnectedException;
import at.or.reder.dcc.util.BufferPool;
import at.or.reder.dcc.util.CanIdMatcher;
import at.or.reder.dcc.util.Utils;
import at.or.reder.zcan20.CanId;
import at.or.reder.zcan20.CommandGroup;
import at.or.reder.zcan20.CommandMode;
import at.or.reder.zcan20.packet.Packet;
import at.or.reder.zcan20.packet.PacketAdapter;
import at.or.reder.zcan20.packet.Ping;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

/**
 *
 * @author Wolfgang Reder
 */
@SuppressWarnings("ClassWithMultipleLoggers")
public final class UDPPort implements ZPort
{

  public static final Logger READ_LOGGER = Logger.getLogger("at.or.reder.zcan.zport.read");
  public static final Logger WRITE_LOGGER = Logger.getLogger("at.or.reder.zcan.zport.write");
  public static final int SO_TIMEOUT = 5000;
  public static final int SO_TRAFFIC = 0x14; // IPTOS_RELIABILITY (0x04),IPTOS_LOWDELAY (0x10)
  private final String name;
  private final SocketAddress outAddress;
  private DatagramSocket socket;
  private final int localPort;
  private final InetAddress local2Bound;
  private final int mtu;
  private final BufferPool bufferPool;
  private final Set<Byte> readGroupFilter;
  private final Set<Byte> writeGroupFilter;
  private long writeSequence = 0;
  private long readSequence = 0;

  public UDPPort(String address,
                 int remotePort,
                 int localPort) throws IOException
  {
    this.name = "UDPPeer to MX10@" + address + ":" + remotePort;
    InetAddress inetAddress = InetAddress.getByName(address);
    InetAddress l2b = getMatchingAddress(inetAddress);
    if (l2b == null) {
      l2b = InetAddress.getLocalHost();
    }
    local2Bound = l2b;
    NetworkInterface intf = NetworkInterface.getByInetAddress(local2Bound);
    mtu = intf != null ? intf.getMTU() : 1500; //assume ethernet
    outAddress = new InetSocketAddress(inetAddress,
                                       remotePort);
    this.localPort = localPort;
    bufferPool = new BufferPool(mtu,
                                Runtime.getRuntime().availableProcessors());
    readGroupFilter = createFilter(getLoggerProp("at.or.reder.zcan.zport.read.filter"));
    writeGroupFilter = createFilter(getLoggerProp("at.or.reder.zcan.zport.write.filter"));
  }

  private String getLoggerProp(String prop)
  {
    String result = LogManager.getLogManager().getProperty(prop);
    if (result == null) {
      return System.getProperty(prop);
    }
    return result;
  }

  private Set<Byte> createFilter(String prop)
  {
    if (prop == null || prop.isBlank()) {
      return null;
    }
    Set<Byte> result = new HashSet<>();
    String[] parts = prop.split(",");
    for (String p : parts) {
      try {
        byte tmp = Byte.parseByte(p,
                                  16);
        result.add(tmp);
      } catch (Throwable th) {
      }
    }
    if (result.isEmpty()) {
      return null;
    }
    return Collections.unmodifiableSet(result);
  }

  private InetAddress getMatchingAddress(InetAddress remoteAddress) throws SocketException
  {
    List<InterfaceAddress> addresses = Utils.getAllInterfaceAddresses();
    for (InterfaceAddress a : addresses) {
      if (Utils.matchesSubnet(remoteAddress,
                              a.getAddress(),
                              a.getNetworkPrefixLength())) {
        return a.getAddress();
      }
    }
    return null;
  }

  private boolean isOpen()
  {
    synchronized (this) {
      return socket != null;
    }
  }

  @Override
  public String getName()
  {
    return name;
  }

  @Override
  public void start() throws IOException
  {
    synchronized (this) {
      if (!isOpen()) {
        socket = null;
        socket = new DatagramSocket(localPort,
                                    local2Bound);
        socket.setSoTimeout(SO_TIMEOUT);
        socket.setTrafficClass(SO_TRAFFIC);
      }
    }
  }

  @Override
  public void close() throws IOException
  {
    synchronized (this) {
      socket.close();
      socket = null;
    }
  }

  private void logPacket(Logger logger,
                         Packet packet,
                         long sequence,
                         Set<Byte> filter,
                         String action)
  {
    if ((filter == null || filter.contains(packet.getCommandGroup().getMagic())) && (logger.isLoggable(Level.FINER))) {
      StringBuilder builder = Utils.appendHexString(sequence,
                                                    new StringBuilder(),
                                                    8);
      builder.append(' ');
      builder.append(action);
      builder.append(" packet :");
      builder.append(packet.toString());
      logger.log(Level.FINER,
                 builder.toString());
      if (logger.isLoggable(Level.FINEST)) {
        PacketAdapter adapter = packet.getAdapter(PacketAdapter.class);
        if (adapter != null) {
          builder.setLength(0);
          Utils.appendHexString(sequence,
                                builder,
                                8);
          builder.append(' ');
          builder.append(action);
          builder.append(" adapter:");
          builder.append(adapter.toString());
          logger.log(Level.FINEST,
                     builder.toString());
        }
      }
    }
  }

  @Override
  public void sendPacket(Packet packet) throws IOException
  {
    Objects.requireNonNull(packet,
                           "packet is null");
    logPacket(WRITE_LOGGER,
              packet,
              writeSequence++,
              writeGroupFilter,
              "send");
    try (BufferPool.BufferItem item = bufferPool.getBuffer()) {
      ByteBuffer buffer = item.getBuffer();
      int numBytes = UDPMarshaller.marshalPacket(packet,
                                                 buffer);
      DatagramSocket s;
      synchronized (this) {
        s = socket;
      }
      if (s != null) {
        s.send(new DatagramPacket(buffer.array(),
                                  numBytes,
                                  outAddress));
      } else {
        throw new NotConnectedException();
      }
    }
  }

  @Override
  public void sendRaw(ByteBuffer buffer) throws IOException
  {
    Objects.requireNonNull(buffer,
                           "buffer is null");
    WRITE_LOGGER.log(Level.FINEST,
                     () -> {
                       StringBuilder builder = new StringBuilder("Sending raw ");
                       Utils.byteBuffer2HexString(buffer,
                                                  builder,
                                                  ' ');
                       return builder.toString();
                     });
    DatagramSocket s;
    synchronized (this) {
      s = socket;
    }
    if (s != null) {
      s.send(new DatagramPacket(buffer.array(),
                                buffer.remaining(),
                                outAddress));
    } else {
      throw new NotConnectedException();
    }
  }

  @Override
  public Packet readPacket() throws IOException
  {
    try (BufferPool.BufferItem item = bufferPool.getBuffer()) {
      ByteBuffer buffer = item.getBuffer();
      DatagramPacket packet = new DatagramPacket(buffer.array(),
                                                 buffer.capacity());
      DatagramSocket s;
      synchronized (this) {
        s = socket;
      }
      try {
        s.receive(packet);
      } catch (SocketTimeoutException ex) {
        READ_LOGGER.log(Level.FINE,
                        "Packet Timeout",
                        ex);
        return null;
      }
      ByteBuffer packetBytes = ByteBuffer.wrap(packet.getData());
      packetBytes.limit(packet.getLength() + packet.getOffset());
      packetBytes.position(packet.getOffset());
      Packet result = UDPMarshaller.unmarshalPacket(packetBytes);
      logPacket(READ_LOGGER,
                result,
                readSequence++,
                readGroupFilter,
                "receive");
      return result;
    }
  }

  @Override
  public Future<Ping> sendInitPacket(ZCANImpl device) throws IOException
  {
    return device.doSendPacket(device.createPacketBuilder().buildLoginPacket(),
                               new CanIdMatcher(CanId.valueOf(
                                       CommandGroup.NETWORK,
                                       CommandGroup.NETWORK_PING,
                                       CommandMode.EVENT,
                                       (short) 0),
                                                CanIdMatcher.MASK_NO_ADDRESS),
                               Ping.class);
  }

  @Override
  public String toString()
  {
    return name;
  }

}
