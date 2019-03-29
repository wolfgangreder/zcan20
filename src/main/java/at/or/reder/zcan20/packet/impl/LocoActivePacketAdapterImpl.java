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

import at.or.reder.zcan20.CommandGroup;
import at.or.reder.zcan20.CommandMode;
import at.or.reder.zcan20.packet.LocoActivePacketAdapter;
import at.or.reder.zcan20.packet.Packet;
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
    public boolean isValid(CommandGroup group,
                           int command,
                           CommandMode mode)
    {
      return group == CommandGroup.LOCO && command == CommandGroup.LOCO_ACTIVE && mode == CommandMode.EVENT;
    }

    @Override
    public LocoActivePacketAdapter createAdapter(Packet packet)
    {
      return new LocoActivePacketAdapterImpl(packet);
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
  public short getMode()
  {
    return buffer.getShort(2);
  }

  @Override
  public String toString()
  {
    StringBuilder builder = new StringBuilder("LOCO_ACTIVE(");
    builder.append(getLocoID());
    builder.append(", ");
    builder.append(getMode());
    return builder.append(')').toString();
  }

}
