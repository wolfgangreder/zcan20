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

import at.or.reder.zcan20.packet.DataGroupNidPacketAdapter;
import at.or.reder.zcan20.packet.Packet;
import at.or.reder.zcan20.util.Utils;

/**
 *
 * @author Wolfgang Reder
 */
final class DataGroupNidPacketAdapterImpl extends AbstractPacketAdapter implements DataGroupNidPacketAdapter
{
//
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
//      return group == CommandGroup.DATA && command == CommandGroup.DATA_ITEMLIST_NID && mode == CommandMode.REQUEST;
//    }
//
//    @Override
//    public DataGroupNidPacketAdapter createAdapter(Packet packet)
//    {
//      return new DataGroupNidPacketAdapterImpl(packet);
//    }
//
//  }

  private DataGroupNidPacketAdapterImpl(Packet packet)
  {
    super(packet);
  }

  @Override
  public short getSystemNID()
  {
    return buffer.getShort(0);
  }

  @Override
  public short getObjectNID()
  {
    return buffer.getShort(2);
  }

  @Override
  public String toString()
  {
    StringBuilder builder = new StringBuilder("DATAINFO_NID(0x");
    Utils.appendHexString(getSystemNID(),
                          builder,
                          4);
    builder.append(", 0x");
    Utils.appendHexString(getObjectNID(),
                          builder,
                          4);
    return builder.append(')').toString();
  }

}
