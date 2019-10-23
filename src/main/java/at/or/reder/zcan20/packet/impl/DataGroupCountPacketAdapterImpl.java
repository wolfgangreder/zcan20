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
import at.or.reder.zcan20.packet.DataGroupCountPacketAdapter;
import at.or.reder.zcan20.packet.Packet;

/**
 *
 * @author Wolfgang Reder
 */
final class DataGroupCountPacketAdapterImpl extends AbstractPacketAdapter implements DataGroupCountPacketAdapter
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
//      return group == CommandGroup.DATA && command == CommandGroup.DATA_GROUP_COUNT && mode == CommandMode.ACK;
//    }
//
//    @Override
//    public DataGroupCountPacketAdapter createAdapter(Packet packet)
//    {
//      return new DataGroupCountPacketAdapterImpl(packet);
//    }
//
//  }
  private DataGroupCountPacketAdapterImpl(Packet packet)
  {
    super(packet);
  }

  @Override
  public DataGroup getDataGroup()
  {
    return DataGroup.valueOf(buffer.getShort(0));
  }

  @Override
  public int getCount()
  {
    short tmp = buffer.getShort(2);
    if (tmp == -1) {
      return -1;
    } else {
      return tmp & 0xffff;
    }
  }

  @Override
  public String toString()
  {
    StringBuilder builder = new StringBuilder("GROUP_COUNT(");
    builder.append(getDataGroup());
    builder.append(", ");
    builder.append(getCount());
    return builder.append(')').toString();
  }

}
