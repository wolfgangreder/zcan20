/*
 * Copyright 2019 Wolfgang Reder.
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

import at.or.reder.dcc.util.DCCUtils;
import at.or.reder.zcan20.PacketSelector;
import at.or.reder.zcan20.packet.Packet;
import at.or.reder.zcan20.packet.PacketAdapterFactory;
import at.or.reder.zcan20.packet.TSEInfoPacketAdapter;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Wolfgang Reder
 */
public final class TSEInfoPacketAdapterImpl extends AbstractPacketAdapter implements TSEInfoPacketAdapter
{

  @ServiceProvider(service = PacketAdapterFactory.class, path = Packet.LOOKUPPATH)
  public static final class Factory implements PacketAdapterFactory<TSEInfoPacketAdapter>
  {

    @Override
    public boolean isValid(PacketSelector selector)
    {
      return SELECTOR.test(selector);
    }

    @Override
    public TSEInfoPacketAdapter convert(Packet obj)
    {
      return new TSEInfoPacketAdapterImpl(obj);
    }

    @Override
    public Class<? extends TSEInfoPacketAdapter> type(Packet obj)
    {
      return TSEInfoPacketAdapter.class;
    }

  }

  public TSEInfoPacketAdapterImpl(Packet packet)
  {
    super(packet);
  }

  @Override
  public short getSenderNID()
  {
    return buffer.getShort(0);
  }

  @Override
  public short getDecoderID()
  {
    return buffer.getShort(2);
  }

  @Override
  public int getCVIndex()
  {
    return buffer.getInt(4);
  }

  @Override
  public byte getState()
  {
    return buffer.get(8);
  }

  @Override
  public byte getCode()
  {
    return buffer.get(9);
  }

  @Override
  public String toString()
  {
    StringBuilder builder = new StringBuilder("TSE_INFO_MODE(SystemNID: 0x");
    DCCUtils.appendHexString(getSenderNID() & 0xffff,
                          builder,
                          4);
    builder.append(", Decoder: ");
    builder.append(getDecoderID());
    builder.append(", CVIndex: ");
    builder.append(getCVIndex());
    builder.append(", State: 0x");
    DCCUtils.appendHexString(getState() & 0xff,
                          builder,
                          2);
    builder.append(", Code: 0x");
    DCCUtils.appendHexString(getCode() & 0xff,
                          builder,
                          2);
    return builder.append(')').toString();
  }

}
