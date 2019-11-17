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
package at.or.reder.zcan20.impl;

import at.or.reder.zcan20.CommandGroup;
import at.or.reder.zcan20.CommandMode;
import at.or.reder.zcan20.ZAccessoryControl;
import at.or.reder.zcan20.packet.AccessoryPacketAdapter;
import at.or.reder.zcan20.packet.Packet;
import at.or.reder.zcan20.util.ScalarFuture;
import java.io.IOException;
import java.util.concurrent.Future;
import java.util.function.Predicate;

final class ZAccessoryControlImpl implements ZAccessoryControl
{

  private static final class MyPacketMatcher implements Predicate<Packet>
  {

    private final short decoder;
    private final byte port;
    private final CommandMode mode;

    public MyPacketMatcher(short decoder,
                           byte port,
                           CommandMode mode)
    {
      this.decoder = decoder;
      this.port = port;
      this.mode = mode;
    }

    @Override
    public boolean test(Packet t)
    {
      if (t.getCommandGroup() == CommandGroup.ACCESSORY && t.getCommand() == CommandGroup.ACCESSORY_PORT4 && t.getCommandMode()
                                                                                                                     == mode) {
        AccessoryPacketAdapter adapter = t.getAdapter(AccessoryPacketAdapter.class);
        if (adapter != null) {
          return adapter.getNID() == decoder && adapter.getPort() == port;
        }
      }
      return false;
    }

  }
  private final ZCANImpl zcan;

  public ZAccessoryControlImpl(ZCANImpl zcan)
  {
    this.zcan = zcan;
  }

  @Override
  public Future<Byte> getAccessoryState(short decoder,
                                        byte port) throws IOException
  {
    Packet packet = zcan.createPacketBuilder().
            senderNID(zcan.getNID()).
            buildAccessoryRequestPacket(decoder,
                                        port).
            build();

    return new ScalarFuture<>(zcan.doSendPacket(packet,
                                                new MyPacketMatcher(decoder,
                                                                    port,
                                                                    CommandMode.ACK),
                                                AccessoryPacketAdapter.class),
                              AccessoryPacketAdapter::getValue);
  }

  @Override
  public void setAccessoryState(short decoder,
                                byte port,
                                byte state) throws IOException
  {
    Packet packet = zcan.createPacketBuilder().
            senderNID(zcan.getNID()).
            buildAccessoryCommandPacket(decoder,
                                        port,
                                        state).
            build();
    zcan.doSendPacket(packet);
  }

  @Override
  public Future<Byte> setAccessoryStateChecked(short decoder,
                                               byte port,
                                               byte state) throws IOException
  {
    Packet packet = zcan.createPacketBuilder().
            senderNID(zcan.getNID()).
            buildAccessoryCommandPacket(decoder,
                                        port,
                                        state).
            build();

    return new ScalarFuture<>(zcan.doSendPacket(packet,
                                                new MyPacketMatcher(decoder,
                                                                    port,
                                                                    CommandMode.ACK),
                                                AccessoryPacketAdapter.class),
                              AccessoryPacketAdapter::getValue);
  }

}
