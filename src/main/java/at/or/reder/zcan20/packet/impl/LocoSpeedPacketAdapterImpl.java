/*
 * Copyright 2017-2020 Wolfgang Reder.
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
import at.or.reder.zcan20.PacketSelector;
import at.or.reder.zcan20.SpeedFlags;
import at.or.reder.zcan20.packet.LocoSpeedPacketAdapter;
import at.or.reder.zcan20.packet.Packet;
import at.or.reder.zcan20.packet.PacketAdapterFactory;
import java.util.Set;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Wolfgang Reder
 */
final class LocoSpeedPacketAdapterImpl extends AbstractPacketAdapter implements LocoSpeedPacketAdapter
{

  @ServiceProvider(service = PacketAdapterFactory.class, path = Packet.LOOKUPPATH)
  public static final class Factory implements PacketAdapterFactory<LocoSpeedPacketAdapter>
  {

    @Override
    public boolean isValid(PacketSelector selector)
    {
      return SELECTOR.test(selector);
    }

    @Override
    public LocoSpeedPacketAdapter convert(Packet obj)
    {
      return new LocoSpeedPacketAdapterImpl(obj);
    }

    @Override
    public Class<? extends LocoSpeedPacketAdapter> type(Packet obj)
    {
      return LocoSpeedPacketAdapter.class;
    }

  }

  private LocoSpeedPacketAdapterImpl(Packet packet)
  {
    super(packet);
  }

  @Override
  public short getDecoderId()
  {
    return buffer.getShort(0);
  }

  @Override
  public short getSpeed()
  {
    return (short) (buffer.getShort(2) & 0x3ff);
  }

  @Override
  public Set<SpeedFlags> getFlags()
  {
    return SpeedFlags.setOfMask(buffer.getShort(2));
  }

  @Override
  public short getDivisor()
  {
    return (short) (buffer.get(4) & 0xff);
  }

  @Override
  public String toString()
  {
    StringBuilder builder = new StringBuilder("LOCO_SPEED(0x");
    Utils.appendHexString(getDecoderId(),
                          builder,
                          4);
    builder.append(", ");
    builder.append(getSpeed());
    builder.append(", ");
    builder.append(getDivisor());
    builder.append(", ");
    for (SpeedFlags f : getFlags()) {
      builder.append(f);
      builder.append(" ");
    }
    return builder.append(')').toString();
  }

}
