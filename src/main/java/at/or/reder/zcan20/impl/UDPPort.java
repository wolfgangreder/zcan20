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

import at.or.reder.zcan20.packet.Packet;
import at.or.reder.zcan20.util.BufferPool;
import at.or.reder.zcan20.util.Utils;
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
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Wolfgang Reder
 */
public final class UDPPort implements ZPort
{

  public static final Logger LOGGER = Logger.getLogger("com.reder.zcan.zport");
  public static final int SO_TIMEOUT = 5000;
  public static final int SO_TRAFFIC = 0x14; // IPTOS_RELIABILITY (0x04),IPTOS_LOWDELAY (0x10)
  private final String name;
  private final SocketAddress outAddress;
  private DatagramSocket socket;
  private final int localPort;
  private final InetAddress local2Bound;
  private final int mtu;
  private final BufferPool bufferPool;

  public UDPPort(String address,
                 int remotePort,
                 int localPort) throws IOException
  {
    this.name = "MX10@" + address + ":" + remotePort;
    InetAddress inetAddress = InetAddress.getByName(address);
    InetAddress l2b = getMatchingAddress(inetAddress);
    if (l2b == null) {
      l2b = InetAddress.getLocalHost();
    }
    local2Bound = l2b;
    NetworkInterface intf = NetworkInterface.getByInetAddress(local2Bound);
    mtu = intf.getMTU();
    outAddress = new InetSocketAddress(inetAddress,
                                       remotePort);
    this.localPort = localPort;
    bufferPool = new BufferPool(mtu,
                                Runtime.getRuntime().availableProcessors());
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

  @Override
  public void sendPacket(Packet packet) throws IOException
  {
    LOGGER.log(Level.FINEST,
               "Sending {0}",
               packet.toString());
    try (BufferPool.BufferItem item = bufferPool.getBuffer()) {
      ByteBuffer buffer = item.getBuffer();
      int numBytes = UDPMarshaller.marshalPacket(packet,
                                                 buffer);
      DatagramSocket s;
      synchronized (this) {
        s = socket;
      }
      s.send(new DatagramPacket(buffer.array(),
                                numBytes,
                                outAddress));
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
        return null;
      }
      ByteBuffer packetBytes = ByteBuffer.wrap(packet.getData());
      packetBytes.limit(packet.getLength() + packet.getOffset());
      packetBytes.position(packet.getOffset());
      Packet result = UDPMarshaller.unmarshalPacket(packetBytes);
      LOGGER.log(Level.FINEST,
                 "Reading {0}",
                 result.toString());
      return result;
    }
  }

}
