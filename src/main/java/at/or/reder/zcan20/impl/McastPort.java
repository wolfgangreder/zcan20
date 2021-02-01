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

import at.or.reder.dcc.util.BufferPool;
import at.or.reder.dcc.util.CanIdMatcher;
import at.or.reder.dcc.util.DCCUtils;
import at.or.reder.zcan20.CanId;
import at.or.reder.zcan20.CommandGroup;
import at.or.reder.zcan20.CommandMode;
import at.or.reder.zcan20.packet.Packet;
import at.or.reder.zcan20.packet.Ping;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Wolfgang Reder
 */
@SuppressWarnings("ClassWithMultipleLoggers")
public final class McastPort implements ZPort
{

  public static final Logger READ_LOGGER = Logger.getLogger("at.or.reder.zcan.zport.read");
  public static final Logger WRITE_LOGGER = Logger.getLogger("at.or.reder.zcan.zport.write");
  public static final int SO_TIMEOUT = 5000;
  public static final int SO_TRAFFIC = 0x14; // IPTOS_RELIABILITY (0x04),IPTOS_LOWDELAY (0x10)
  private final String name;
  private final InetSocketAddress outAddress;
  private MulticastSocket socket;
  private final int mtu;
  private final BufferPool bufferPool;

  public McastPort(String address,
                   int remotePort) throws IOException
  {
    this.name = "McastPeer to MX10@" + address + ":" + remotePort;
    InetAddress inetAddress = InetAddress.getByName(address);
    mtu = McastMarshaller.PREFIX_LEN + 8;
    outAddress = new InetSocketAddress(inetAddress,
                                       remotePort);
    bufferPool = new BufferPool(mtu,
                                Runtime.getRuntime().availableProcessors());
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
        socket = new MulticastSocket(outAddress.getPort());
        socket.joinGroup(outAddress.getAddress());
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

  @Override
  public void sendPacket(Packet packet) throws IOException
  {
    Objects.requireNonNull(packet,
                           "packet is null");
    WRITE_LOGGER.log(Level.FINEST,
                     "Sending {0}",
                     packet.toString());
    try (BufferPool.BufferItem item = bufferPool.getBuffer()) {
      ByteBuffer buffer = item.getBuffer();
      int numBytes = McastMarshaller.marshalPacket(packet,
                                                   LocalDateTime.now(),
                                                   3,
                                                   buffer);
      DatagramSocket s;
      synchronized (this) {
        s = socket;
      }
      s.send(new DatagramPacket(buffer.array(),
                                0,
                                numBytes,
                                outAddress));
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
                       DCCUtils.byteBuffer2HexString(buffer,
                                                  builder,
                                                  ' ');
                       return builder.toString();
                     });
    DatagramSocket s;
    synchronized (this) {
      s = socket;
    }
    s.send(new DatagramPacket(buffer.array(),
                              0,
                              buffer.remaining(),
                              outAddress));
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
      Packet result = McastMarshaller.unmarshalPacket(packetBytes);
      READ_LOGGER.log(Level.FINEST,
                      "Reading {0}",
                      result.toString());
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
