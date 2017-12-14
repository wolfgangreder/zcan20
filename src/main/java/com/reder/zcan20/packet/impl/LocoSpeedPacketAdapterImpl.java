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
package com.reder.zcan20.packet.impl;

import com.reder.zcan20.CommandGroup;
import com.reder.zcan20.CommandMode;
import com.reder.zcan20.SpeedFlags;
import com.reder.zcan20.packet.LocoSpeedPacketAdapter;
import com.reder.zcan20.packet.Packet;
import com.reder.zcan20.packet.PacketAdapterFactory;
import com.reder.zcan20.util.Utils;
import java.util.Set;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Wolfgang Reder
 */
final class LocoSpeedPacketAdapterImpl extends AbstractPacketAdapter implements LocoSpeedPacketAdapter
{

  @ServiceProvider(service = PacketAdapterFactory.class, path = Packet.LOOKUPPATH)
  public static final class Factory implements PacketAdapterFactory
  {

    @Override
    public boolean isValid(CommandGroup group,
                           int command,
                           CommandMode mode)
    {
      if (group == CommandGroup.LOCO && command == CommandGroup.LOCO_SPEED) {
        return mode == CommandMode.COMMAND || mode == CommandMode.ACK;
      }
      return false;
    }

    @Override
    public LocoSpeedPacketAdapter createAdapter(Packet packet)
    {
      return new LocoSpeedPacketAdapterImpl(packet);
    }

  }

  private LocoSpeedPacketAdapterImpl(Packet packet)
  {
    super(packet);
  }

  @Override
  public short getLocoID()
  {
    return buffer.getShort(0);
  }

  @Override
  public short getSpeed()
  {
    return (short) (buffer.getShort(2) & 0x3ff);
  }

  @Override
  public Set<SpeedFlags> getFlags()
  {
    return SpeedFlags.setOfMask(buffer.getShort(2));
  }

  @Override
  public short getDivisor()
  {
    return (short) (buffer.get(4) & 0xff);
  }

  @Override
  public String toString()
  {
    StringBuilder builder = new StringBuilder("LOCO_SPEED(0x");
    Utils.appendHexString(getLocoID(),
                          builder,
                          4);
    builder.append(", ");
    builder.append(getSpeed());
    builder.append(", ");
    builder.append(getDivisor());
    builder.append(", ");
    for (SpeedFlags f : getFlags()) {
      builder.append(f);
      builder.append(" ");
    }
    return builder.append(')').toString();
  }

}
