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
import at.or.reder.zcan20.PowerOutput;
import at.or.reder.zcan20.packet.ModulePowerInfoRequestAdapter;
import at.or.reder.zcan20.packet.Packet;
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

    @Override
    public boolean isValid(CommandGroup group,
                           int command,
                           CommandMode mode)
    {
      return group == CommandGroup.CONFIG && command == CommandGroup.CONFIG_POWER_INFO && mode == CommandMode.REQUEST;
    }

    @Override
    public ModulePowerInfoRequestAdapter createAdapter(Packet packet)
    {
      return new ModulePowerInfoRequestAdapterImpl(packet);
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
  public PowerOutput getOutput()
  {
    switch (buffer.get(2)) {
      case 0:
        return PowerOutput.OUT_1;
      case 1:
        return PowerOutput.OUT_2;
      case 2:
        return PowerOutput.BOOSTER;
      default:
        return PowerOutput.UNKNOWN;
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
