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
package at.or.reder.zcan20.packet.impl;

import at.or.reder.zcan20.LocoActive;
import at.or.reder.zcan20.PacketSelector;
import at.or.reder.zcan20.packet.LocoActivePacketAdapter;
import at.or.reder.zcan20.packet.Packet;
import at.or.reder.zcan20.packet.PacketAdapter;
import at.or.reder.zcan20.packet.PacketAdapterFactory;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Wolfgang Reder
 */
final class LocoActivePacketAdapterImpl extends AbstractPacketAdapter implements LocoActivePacketAdapter
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
      return new LocoActivePacketAdapterImpl(obj);
    }

    @Override
    public Class<? extends PacketAdapter> type(Packet obj)
    {
      return LocoActivePacketAdapter.class;
    }

  }

  LocoActivePacketAdapterImpl(Packet packet)
  {
    super(packet);
  }

  @Override
  public short getLocoID()
  {
    return buffer.getShort(0);
  }

  @Override
  public LocoActive getState()
  {
    return LocoActive.valueOfMagic(buffer.getShort(2));
  }

  @Override
  public String toString()
  {
    StringBuilder builder = new StringBuilder("LOCO_ACTIVE(");
    builder.append(getLocoID());
    builder.append(", ");
    builder.append(getState());
    return builder.append(')').toString();
  }

}
