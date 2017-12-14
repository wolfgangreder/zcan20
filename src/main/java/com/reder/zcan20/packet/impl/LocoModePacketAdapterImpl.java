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
import com.reder.zcan20.Protocol;
import com.reder.zcan20.SpeedSteps;
import com.reder.zcan20.SpeedlimitMode;
import com.reder.zcan20.packet.LocoModePacketAdapter;
import com.reder.zcan20.packet.Packet;
import com.reder.zcan20.packet.PacketAdapterFactory;
import com.reder.zcan20.util.Utils;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Wolfgang Reder
 */
final class LocoModePacketAdapterImpl extends AbstractPacketAdapter implements LocoModePacketAdapter
{

  @ServiceProvider(service = PacketAdapterFactory.class, path = Packet.LOOKUPPATH)
  public static final class Factory implements PacketAdapterFactory
  {

    @Override
    public boolean isValid(CommandGroup group,
                           int command,
                           CommandMode mode)
    {
      if (group == CommandGroup.LOCO && command == CommandGroup.LOCO_MODE) {
        return mode == CommandMode.COMMAND || mode == CommandMode.ACK;
      }
      return false;
    }

    @Override
    public LocoModePacketAdapter createAdapter(Packet packet)
    {
      return new LocoModePacketAdapterImpl(packet);
    }

  }

  private LocoModePacketAdapterImpl(Packet packet)
  {
    super(packet);
  }

  @Override
  public short getLocoID()
  {
    return buffer.getShort(0);
  }

  @Override
  public SpeedSteps getSpeedsteps()
  {
    byte tmp = (byte) (buffer.get(2) & 0x0f);
    return SpeedSteps.valueOfMagic(tmp);
  }

  @Override
  public Protocol getProtocol()
  {
    byte tmp = (byte) ((buffer.get(2) >> 4) & 0x0f);
    return Protocol.valueOfMagic(tmp);
  }

  @Override
  public int getNumFunctions()
  {
    return (byte) buffer.get(3);
  }

  @Override
  public SpeedlimitMode getSpeedlimitMode()
  {
    int tmp = (buffer.get(4) & 0x0c) >> 2;
    return SpeedlimitMode.valueOf(tmp);
  }

  @Override
  public boolean isPulseFx()
  {
    return (buffer.get(4) & 0x01) != 0;
  }

  @Override
  public boolean isAnalogFx()
  {
    return (buffer.get(4) & 0x02) != 0;
  }

  @Override
  public String toString()
  {
    StringBuilder builder = new StringBuilder("LOCO_MODE(0x");
    Utils.appendHexString(getLocoID(),
                          builder,
                          4);
    builder.append(", ");
    builder.append(getSpeedsteps());
    builder.append(", ");
    builder.append(getProtocol());
    builder.append(", ");
    builder.append(getNumFunctions());
    builder.append(", ");
    builder.append(getSpeedlimitMode());
    if (isPulseFx()) {
      builder.append(", pulseFX");
    }
    if (isAnalogFx()) {
      builder.append(", analogFX");
    }
    return builder.append(')').toString();
  }

}
