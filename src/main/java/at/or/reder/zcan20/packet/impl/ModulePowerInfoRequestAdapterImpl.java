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
import at.or.reder.zcan20.PacketSelector;
import at.or.reder.dcc.PowerPort;
import at.or.reder.zcan20.packet.ModulePowerInfoRequestAdapter;
import at.or.reder.zcan20.packet.Packet;
import at.or.reder.zcan20.packet.PacketAdapter;
import at.or.reder.zcan20.packet.PacketAdapterFactory;
import at.or.reder.zcan20.util.Utils;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Wolfgang Reder
 */
final class ModulePowerInfoRequestAdapterImpl extends AbstractPacketAdapter implements ModulePowerInfoRequestAdapter
{

  @ServiceProvider(service = PacketAdapterFactory.class, path = Packet.LOOKUPPATH)
  public static final class Factory implements PacketAdapterFactory
  {

    public static final PacketSelector SELECTOR = new PacketSelector(CommandGroup.CONFIG,
                                                                     CommandGroup.CONFIG_POWER_INFO,
                                                                     CommandMode.REQUEST,
                                                                     3);

    @Override
    public boolean isValid(PacketSelector selector)
    {
      return SELECTOR.matches(selector);
    }

    @Override
    public ModulePowerInfoRequestAdapter convert(Packet packet)
    {
      return new ModulePowerInfoRequestAdapterImpl(packet);
    }

    @Override
    public Class<? extends PacketAdapter> type(Packet obj)
    {
      return ModulePowerInfoRequestAdapter.class;
    }

  }

  private ModulePowerInfoRequestAdapterImpl(Packet packet)
  {
    super(packet);
  }

  @Override
  public short getTargetNID()
  {
    return buffer.getShort(0);
  }

  @Override
  public PowerPort getOutput()
  {
    switch (buffer.get(2)) {
      case 0:
        return PowerPort.OUT_1;
      case 1:
        return PowerPort.OUT_2;
      case 2:
        return PowerPort.BOOSTER;
      default:
        return PowerPort.UNKNOWN;
    }
  }

  @Override
  public String toString()
  {
    StringBuilder builder = new StringBuilder("MODULE_POWER_INFO(0x");
    Utils.appendHexString(getTargetNID() & 0xffff,
                          builder,
                          4);
    builder.append(", ");
    builder.append(getOutput());
    return builder.append(')').toString();
  }

}
