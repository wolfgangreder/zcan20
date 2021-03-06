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

import at.or.reder.dcc.PowerPort;
import at.or.reder.zcan20.packet.Packet;
import at.or.reder.zcan20.packet.PowerInfoRequestAdapter;
import at.or.reder.dcc.util.DCCUtils;
import java.util.Set;

/**
 *
 * @author Wolfgang Reder
 */
final class PowerInfoRequestAdapterImpl extends AbstractPacketAdapter implements PowerInfoRequestAdapter
{

//  @ServiceProvider(service = PacketAdapterFactory.class, path = Packet.LOOKUPPATH)
//  public static final class Factory implements PacketAdapterFactory
//  {
//
//    @Override
//    public boolean isValid(CommandGroup group,
//                           int command,
//                           CommandMode mode,
//                           int dlc)
//    {
//      return group == CommandGroup.SYSTEM && command == CommandGroup.SYSTEM_POWER && mode == CommandMode.REQUEST;
//    }
//
//    @Override
//    public PowerInfoRequestAdapter createAdapter(Packet packet)
//    {
//      return new PowerInfoRequestAdapterImpl(packet);
//    }
//
//  }
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
  public Set<PowerPort> getOutputs()
  {
    return PowerPort.toSet(buffer.get(2));
  }

  @Override
  public String toString()
  {
    StringBuilder builder = new StringBuilder("PowerInfoRequest(0x");
    DCCUtils.appendHexString(getMasterNID() & 0xffff,
                          builder,
                          4);
    builder.append(", ");
    Set<PowerPort> outputs = getOutputs();
    for (PowerPort o : outputs) {
      builder.append(o);
      builder.append(' ');
    }
    if (!outputs.isEmpty()) {
      builder.setLength(builder.length() - 1);
    }
    return builder.append(')').toString();
  }

}
