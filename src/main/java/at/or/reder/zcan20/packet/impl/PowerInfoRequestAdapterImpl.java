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
import at.or.reder.zcan20.packet.Packet;
import at.or.reder.zcan20.packet.PacketAdapterFactory;
import at.or.reder.zcan20.packet.PowerInfoRequestAdapter;
import at.or.reder.zcan20.util.Utils;
import java.util.Set;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Wolfgang Reder
 */
final class PowerInfoRequestAdapterImpl extends AbstractPacketAdapter implements PowerInfoRequestAdapter
{

  @ServiceProvider(service = PacketAdapterFactory.class, path = Packet.LOOKUPPATH)
  public static final class Factory implements PacketAdapterFactory
  {

    @Override
    public boolean isValid(CommandGroup group,
                           int command,
                           CommandMode mode)
    {
      return group == CommandGroup.SYSTEM && command == CommandGroup.SYSTEM_POWER && mode == CommandMode.REQUEST;
    }

    @Override
    public PowerInfoRequestAdapter createAdapter(Packet packet)
    {
      return new PowerInfoRequestAdapterImpl(packet);
    }

  }

  private PowerInfoRequestAdapterImpl(Packet packet)
  {
    super(packet);
  }

  @Override
  public short getMasterNID()
  {
    return buffer.getShort(0);
  }

  @Override
  public Set<PowerOutput> getOutputs()
  {
    return PowerOutput.toSet(buffer.get(2));
  }

  @Override
  public String toString()
  {
    StringBuilder builder = new StringBuilder("PowerInfoRequest(0x");
    Utils.appendHexString(getMasterNID() & 0xffff,
                          builder,
                          4);
    builder.append(", ");
    Set<PowerOutput> outputs = getOutputs();
    for (PowerOutput o : outputs) {
      builder.append(o);
      builder.append(' ');
    }
    if (!outputs.isEmpty()) {
      builder.setLength(builder.length() - 1);
    }
    return builder.append(')').toString();
  }

}
