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

import at.or.reder.dcc.util.Utils;
import at.or.reder.zcan20.CVReadState;
import at.or.reder.zcan20.CommandGroup;
import at.or.reder.zcan20.PacketSelector;
import at.or.reder.zcan20.packet.CVInfoAdapter;
import at.or.reder.zcan20.packet.Packet;
import at.or.reder.zcan20.packet.PacketAdapterFactory;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Wolfgang Reder
 */
final class CVInfoPacketAdpaterImpl extends AbstractPacketAdapter implements CVInfoAdapter
{

  @ServiceProvider(service = PacketAdapterFactory.class, path = Packet.LOOKUPPATH)
  public static final class Factory implements PacketAdapterFactory<CVInfoAdapter>
  {

    @Override
    public boolean isValid(PacketSelector selector)
    {
      return SELECTOR.test(selector);
    }

    @Override
    public CVInfoAdapter convert(Packet obj)
    {
      return new CVInfoPacketAdpaterImpl(obj);
    }

    @Override
    public Class<? extends CVInfoAdapter> type(Packet obj)
    {
      return CVInfoAdapter.class;
    }

  }
  private final CVReadState state;

  private CVInfoPacketAdpaterImpl(Packet packet)
  {
    super(packet);
    if (packet.getCommand() == CommandGroup.TSE_PROG_BUSY) {
      state = CVReadState.BUSY;
    } else {
      state = CVReadState.READ;
    }
  }

  @Override
  public CVReadState getReadState()
  {
    return state;
  }

  @Override
  public short getSystemID()
  {
    return buffer.getShort(0);
  }

  @Override
  public short getDecoderID()
  {
    return buffer.getShort(2);
  }

  @Override
  public int getNumber()
  {
    return buffer.getInt(4);
  }

  @Override
  public short getValue()
  {
    if (buffer.capacity() > 8) {
      return buffer.getShort(8);
    } else {
      return 0;
    }
  }

  @Override
  public String toString()
  {
    StringBuilder builder = new StringBuilder("CVInfo(");
    builder.append(getReadState());
    builder.append(", 0x");
    Utils.appendHexString(getSystemID() & 0xffff,
                          builder,
                          4);
    builder.append(", ");
    builder.append(getDecoderID());
    builder.append(", #");
    builder.append(getNumber());
    builder.append(", ");
    builder.append(getValue());
    return builder.append(')').toString();
  }

}
