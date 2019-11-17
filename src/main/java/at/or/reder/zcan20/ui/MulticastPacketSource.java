/*
 * Copyright 2019 Wolfgang Reder.
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
package at.or.reder.zcan20.ui;

import at.or.reder.zcan20.PacketListener;
import at.or.reder.zcan20.impl.McastMarshaller;
import at.or.reder.zcan20.packet.Packet;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import javax.swing.SwingUtilities;

/**
 *
 * @author Wolfgang Reder
 */
public final class MulticastPacketSource implements PacketSource
{

  private final InetSocketAddress address;
  private volatile MulticastSocket socket;
  private boolean stopRequest = false;
  private Thread receiveThread;
  private final Set<PacketListener> listener = new CopyOnWriteArraySet<>();

  public MulticastPacketSource(InetSocketAddress address)
  {
    this.address = address;
  }

  @Override
  public void start() throws IOException
  {
    if (socket == null) {
      socket = new MulticastSocket(address.getPort());
      socket.setSoTimeout(1000);
      receiveThread = new Thread(this::receiveLoop);
      receiveThread.setDaemon(true);
      socket.joinGroup(address.getAddress());
      receiveThread.start();
    }
  }

  @Override
  public void close() throws IOException
  {
    if (socket != null) {
      try {
        synchronized (this) {
          stopRequest = true;
        }
        socket.close();
      } finally {
        receiveThread = null;
        socket = null;
      }
    }
  }

  @Override
  public void addPacketListener(PacketListener packet)
  {
    if (packet != null) {
      listener.add(packet);
    }
  }

  @Override
  public void removePacketListener(PacketListener packet)
  {
    listener.remove(packet);
  }

  private boolean isStopRequested()
  {
    synchronized (this) {
      return stopRequest;
    }
  }

  private void firePacket(Packet packet)
  {
    for (PacketListener l : listener) {
      l.onPacket(null,
                 packet);
    }
  }

  private void receiveLoop()
  {
    DatagramPacket datagram = new DatagramPacket(new byte[256],
                                                 256);

    while (!isStopRequested()) {
      try {
        socket.receive(datagram);
        ByteBuffer buffer = ByteBuffer.wrap(datagram.getData(),
                                            datagram.getOffset(),
                                            datagram.getLength());
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        Packet packet = McastMarshaller.unmarshalPacket(buffer);
        if (packet != null) {
          SwingUtilities.invokeLater(() -> firePacket(packet));
        }
      } catch (Throwable th) {
      }
    }
  }

}
