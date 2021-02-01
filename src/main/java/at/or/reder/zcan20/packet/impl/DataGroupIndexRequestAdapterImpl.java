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
import at.or.reder.zcan20.packet.DataGroupIndexRequestAdapter;
import at.or.reder.zcan20.packet.Packet;
import at.or.reder.dcc.util.DCCUtils;

/**
 *
 * @author Wolfgang Reder
 */
final class DataGroupIndexRequestAdapterImpl extends AbstractPacketAdapter implements DataGroupIndexRequestAdapter
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
//      return group == CommandGroup.DATA && command == CommandGroup.DATA_ITEMLIST_INDEX && mode == CommandMode.REQUEST;
//    }
//
//    @Override
//    public DataGroupIndexRequestAdapter createAdapter(Packet packet)
//    {
//      return new DataGroupIndexRequestAdapterImpl(packet);
//    }
//
//  }
  private DataGroupIndexRequestAdapterImpl(Packet packet)
  {
    super(packet);
  }

  @Override
  public short getMasterNID()
  {
    return buffer.getShort(0);
  }

  @Override
  public DataGroup getDataGroup()
  {
    return DataGroup.valueOf(buffer.getShort(2));
  }

  @Override
  public short getIndex()
  {
    return buffer.getShort(4);
  }

  @Override
  public String toString()
  {
    StringBuilder builder = new StringBuilder("ITEMLIST_INDEX(0x");
    DCCUtils.appendHexString(getMasterNID() & 0xffff,
                          builder,
                          4);
    builder.append(", ");
    builder.append(getDataGroup().toString());
    builder.append(", ");
    builder.append(Short.toUnsignedInt(getIndex()));
    builder.append(')');
    return builder.toString();
  }

}
