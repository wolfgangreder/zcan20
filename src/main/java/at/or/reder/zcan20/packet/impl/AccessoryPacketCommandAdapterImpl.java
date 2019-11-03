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
package at.or.reder.zcan20.packet.impl;

import at.or.reder.dcc.util.Utils;
import at.or.reder.zcan20.PacketSelector;
import at.or.reder.zcan20.packet.AccessoryPacketCommandAdapter;
import at.or.reder.zcan20.packet.Packet;
import at.or.reder.zcan20.packet.PacketAdapter;
import at.or.reder.zcan20.packet.PacketAdapterFactory;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Wolfgang Reder
 */
final class AccessoryPacketCommandAdapterImpl extends AbstractPacketAdapter implements AccessoryPacketCommandAdapter
{

  @ServiceProvider(service = PacketAdapterFactory.class, path = Packet.LOOKUPPATH)
  public static final class Factory implements PacketAdapterFactory
  {

    @Override
    public boolean isValid(PacketSelector selector)
    {
      return SELECTOR.test(selector);
    }

    @Override
    public PacketAdapter convert(Packet obj)
    {
      return new AccessoryPacketCommandAdapterImpl(obj);
    }

    @Override
    public Class<? extends PacketAdapter> type(Packet obj)
    {
      return AccessoryPacketCommandAdapter.class;
    }

  }

  public AccessoryPacketCommandAdapterImpl(Packet packet)
  {
    super(packet);
  }

  @Override
  public short getNID()
  {
    return (short) (buffer.getShort(0) & 0x1ff);
  }

  @Override
  public byte getPort()
  {
    return (byte) (buffer.get(2) & 0xff);
  }

  @Override
  public byte getValue()
  {
    return (byte) (buffer.get(3) & 0xff);
  }

  @Override
  public String toString()
  {
    StringBuilder builder = new StringBuilder("AccessoryPacketRequest(");
    builder.append(getPacket().getCommandMode());
    builder.append(", nid=0x");
    Utils.appendHexString(getNID() & 0xffff,
                          builder,
                          4);
    builder.append(", port=0x");
    Utils.appendHexString(getPort() & 0xff,
                          builder,
                          2);
    builder.append(", value=0x");
    Utils.appendHexString(getValue() & 0xff,
                          builder,
                          2);
    builder.append(')');
    return builder.toString();
  }

}
