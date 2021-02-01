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

import at.or.reder.zcan20.DataGroup;
import at.or.reder.zcan20.packet.DataGroupRequestPacketAdapter;
import at.or.reder.zcan20.packet.Packet;
import at.or.reder.dcc.util.DCCUtils;

/**
 *
 * @author Wolfgang Reder
 */
final class DataGroupRequestPacketAdapterImpl extends AbstractPacketAdapter implements DataGroupRequestPacketAdapter
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
//      return group == CommandGroup.DATA && command == CommandGroup.DATA_GROUP_COUNT && mode == CommandMode.REQUEST;
//    }
//
//    @Override
//    public DataGroupRequestPacketAdapter createAdapter(Packet packet)
//    {
//      return new DataGroupRequestPacketAdapterImpl(packet);
//    }
//
//  }
  private DataGroupRequestPacketAdapterImpl(Packet packet)
  {
    super(packet);
  }

  @Override
  public short getSourceNid()
  {
    return buffer.getShort(0);
  }

  @Override
  public DataGroup getDataGroup()
  {
    return DataGroup.valueOf(buffer.getShort(2));
  }

  @Override
  public String toString()
  {
    StringBuilder builder = new StringBuilder("GROUP_COUNT(0x");
    DCCUtils.appendHexString(getSourceNid() & 0xffff,
                          builder,
                          4);
    builder.append(", ");
    builder.append(getDataGroup());
    return builder.append(')').toString();
  }

}
