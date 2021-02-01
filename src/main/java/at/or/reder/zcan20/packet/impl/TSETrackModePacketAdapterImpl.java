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

import at.or.reder.dcc.PowerPort;
import at.or.reder.dcc.util.DCCUtils;
import at.or.reder.zcan20.PacketSelector;
import at.or.reder.zcan20.packet.Packet;
import at.or.reder.zcan20.packet.PacketAdapterFactory;
import at.or.reder.zcan20.packet.TSETrackModePacketAdapter;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Wolfgang Reder
 */
public final class TSETrackModePacketAdapterImpl extends AbstractPacketAdapter implements TSETrackModePacketAdapter
{

  @ServiceProvider(service = PacketAdapterFactory.class, path = Packet.LOOKUPPATH)
  public static final class Factory implements PacketAdapterFactory<TSETrackModePacketAdapter>
  {

    @Override
    public boolean isValid(PacketSelector selector)
    {
      return SELECTOR.test(selector);
    }

    @Override
    public TSETrackModePacketAdapter convert(Packet obj)
    {
      return new TSETrackModePacketAdapterImpl(obj);
    }

    @Override
    public Class<? extends TSETrackModePacketAdapter> type(Packet obj)
    {
      return TSETrackModePacketAdapter.class;
    }

  }

  public TSETrackModePacketAdapterImpl(Packet packet)
  {
    super(packet);
  }

  @Override
  public short getSenderNID()
  {
    return buffer.getShort(0);
  }

  @Override
  public PowerPort getPort()
  {
    return PowerPort.valueOfMagic(buffer.get(2));
  }

  @Override
  public byte getMode()
  {
    return buffer.get(3);
  }

  @Override
  public float getVoltage()
  {
    return buffer.getShort(4) / 1000f;
  }

  @Override
  public float getCurrent()
  {
    return buffer.getShort(6) / 1000f;
  }

  @Override
  public String toString()
  {
    StringBuilder builder = new StringBuilder("TSE_TRACK_MODE(SystemNID: 0x");
    DCCUtils.appendHexString(getSenderNID(),
                          builder,
                          4);
    builder.append(", Port: ");
    builder.append(getPort());
    builder.append(", Mode: ");
    builder.append(getMode());
    builder.append(", Voltage: ");
    builder.append(getVoltage());
    builder.append(", Current: ");
    builder.append(getCurrent());
    return builder.append(')').toString();
  }

}
