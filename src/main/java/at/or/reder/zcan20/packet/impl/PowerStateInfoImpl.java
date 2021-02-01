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

import at.or.reder.dcc.PowerPort;
import at.or.reder.dcc.util.DCCUtils;
import at.or.reder.zcan20.PacketSelector;
import at.or.reder.zcan20.ZimoPowerMode;
import at.or.reder.zcan20.packet.Packet;
import at.or.reder.zcan20.packet.PacketAdapterFactory;
import at.or.reder.zcan20.packet.PowerStateInfo;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Wolfgang Reder
 */
final class PowerStateInfoImpl extends AbstractPacketAdapter implements PowerStateInfo
{

  @ServiceProvider(service = PacketAdapterFactory.class, path = Packet.LOOKUPPATH)
  public static final class Factory implements PacketAdapterFactory<PowerStateInfo>
  {

    @Override
    public boolean isValid(PacketSelector selector)
    {
      return SELECTOR.test(selector);
    }

    @Override
    public PowerStateInfo convert(Packet packet)
    {
      return new PowerStateInfoImpl(packet);
    }

    @Override
    public Class<? extends PowerStateInfo> type(Packet obj)
    {
      return PowerStateInfo.class;
    }

  }
  private final int offset;

  private PowerStateInfoImpl(Packet packet)
  {
    super(packet);
    if (buffer.capacity() < 4) {
      throw new IllegalArgumentException("invalid dlc");
    }
    if (buffer.capacity() == 4) {
      offset = 0;
    } else {
      offset = 2;
    }
  }

  @Override
  public int getSystemNID()
  {
    return buffer.getShort(offset) & 0xffff;
  }

  @Override
  public PowerPort getOutput()
  {
    return PowerPort.valueOfMagic(buffer.get(offset + 2) & 0xff);
  }

  @Override
  public ZimoPowerMode getMode()
  {
    return ZimoPowerMode.valueOfMagic(buffer.get(offset + 3) & 0xff);
  }

  @Override
  public String toString()
  {
    StringBuilder builder = new StringBuilder("SYSTEM_POWER(SystemNID: 0x");
    DCCUtils.appendHexString(getSystemNID(),
                          builder,
                          4);
    builder.append(", Port: ");
    builder.append(getOutput());
    builder.append(", Mode: ");
    builder.append(getMode());
    return builder.append(')').toString();
  }

}
