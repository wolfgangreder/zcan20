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

import at.or.reder.dcc.util.Utils;
import at.or.reder.zcan20.PacketSelector;
import at.or.reder.zcan20.Protocol;
import at.or.reder.zcan20.SpeedSteps;
import at.or.reder.zcan20.SpeedlimitMode;
import at.or.reder.zcan20.packet.LocoModePacketAdapter;
import at.or.reder.zcan20.packet.Packet;
import at.or.reder.zcan20.packet.PacketAdapter;
import at.or.reder.zcan20.packet.PacketAdapterFactory;
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
      return LocoModePacketAdapter.class;
    }

  }

  private LocoModePacketAdapterImpl(Packet packet)
  {
    super(packet);
  }

  @Override
  public short getLocoAddress()
  {
    return buffer.getShort(0);
  }

  @Override
  public SpeedSteps getSpeedSteps()
  {
    byte tmp = (byte) ((buffer.get(2) >> 4) & 0x0f);
    return SpeedSteps.valueOfMagic(tmp);
  }

  @Override
  public Protocol getProtocol()
  {
    byte tmp = (byte) (buffer.get(2) & 0x0f);
    return Protocol.valueOfMagic(tmp);
  }

  @Override
  public byte getFunctionCount()
  {
    return (byte) buffer.get(3);
  }

  @Override
  public SpeedlimitMode getSpeedLimitMode()
  {
    int tmp = (buffer.get(4) & 0x0c) >> 2;
    return SpeedlimitMode.valueOf(tmp);
  }

  @Override
  public boolean isPulsFx()
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
    Utils.appendHexString(getLocoAddress(),
                          builder,
                          4);
    builder.append(", ");
    builder.append(getSpeedSteps());
    builder.append(", ");
    builder.append(getProtocol());
    builder.append(", ");
    builder.append(getFunctionCount());
    builder.append(", ");
    builder.append(getSpeedLimitMode());
    if (isPulsFx()) {
      builder.append(", pulseFX");
    }
    if (isAnalogFx()) {
      builder.append(", analogFX");
    }
    return builder.append(')').toString();
  }

}
