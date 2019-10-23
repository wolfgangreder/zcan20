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

import at.or.reder.zcan20.InterfaceOptionType;
import at.or.reder.zcan20.packet.InterfaceOptionRequestAdapter;
import at.or.reder.zcan20.packet.Packet;
import at.or.reder.zcan20.util.Utils;

/**
 *
 * @author Wolfgang Reder
 */
final class InterfaceOptionRequestAdapterImpl extends AbstractPacketAdapter implements InterfaceOptionRequestAdapter
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
//      return group == CommandGroup.NETWORK && command == CommandGroup.NETWORK_INTERFACE_OPTION && mode == CommandMode.REQUEST;
//    }
//
//    @Override
//    public InterfaceOptionRequestAdapter createAdapter(Packet packet)
//    {
//      return new InterfaceOptionRequestAdapterImpl(packet);
//    }
//
//  }
  private InterfaceOptionRequestAdapterImpl(Packet packet)
  {
    super(packet);
  }

  @Override
  public short getObjectNID()
  {
    return buffer.getShort(0);
  }

  @Override
  public InterfaceOptionType getOptionType()
  {
    return InterfaceOptionType.valueOf(buffer.getShort(2));
  }

  @Override
  public String toString()
  {
    StringBuilder builder = new StringBuilder("INTERFACE_OPTION(0x");
    Utils.appendHexString(getObjectNID(),
                          builder,
                          4);
    builder.append(", ");
    builder.append(getOptionType());
    return builder.append(')').toString();
  }

}
