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
import at.or.reder.zcan20.packet.DataGroupInfoPacketAdapter;
import at.or.reder.zcan20.packet.Packet;
import at.or.reder.dcc.util.Utils;

/**
 *
 * @author Wolfgang Reder
 */
final class DataGroupInfoPacketAdapterImpl extends AbstractPacketAdapter implements DataGroupInfoPacketAdapter
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
//      if (group == CommandGroup.DATA && mode == CommandMode.ACK) {
//        return command == CommandGroup.DATA_ITEMLIST_INDEX || command == CommandGroup.DATA_ITEMLIST_NID;
//      }
//      return false;
//    }
//
//    @Override
//    public DataGroupInfoPacketAdapter createAdapter(Packet packet)
//    {
//      return new DataGroupInfoPacketAdapterImpl(packet);
//    }
//
//  }
  private final int indexOffset;
  private final int nidOffset;

  private DataGroupInfoPacketAdapterImpl(Packet packet)
  {
    super(packet);
    if (packet.getCommand() == CommandGroup.DATA_ITEMLIST_INDEX) {
      indexOffset = 0;
      nidOffset = 2;
    } else {
      indexOffset = 2;
      nidOffset = 0;
    }
  }

  @Override
  public short getLastTick()
  {
    return buffer.getShort(4);
  }

  @Override
  public short getIndex()
  {
    return buffer.getShort(indexOffset);
  }

  @Override
  public short getObjectNid()
  {
    return buffer.getShort(nidOffset);
  }

  @Override
  public String toString()
  {
    StringBuilder builder = new StringBuilder("DATAINFO(0x");
    Utils.appendHexString(getObjectNid() & 0xffff,
                          builder,
                          4);
    builder.append(", ");
    builder.append(getIndex());
    builder.append(", ");
    builder.append(getLastTick());
    return builder.append("ms)").toString();
  }

}
