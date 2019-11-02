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
import at.or.reder.zcan20.CommandMode;
import at.or.reder.zcan20.PacketSelector;
import at.or.reder.zcan20.PowerMode;
import at.or.reder.dcc.PowerPort;
import at.or.reder.zcan20.packet.Packet;
import at.or.reder.zcan20.packet.PacketAdapter;
import at.or.reder.zcan20.packet.PacketAdapterFactory;
import at.or.reder.zcan20.packet.PowerStateInfo;
import at.or.reder.dcc.util.Utils;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Wolfgang Reder
 */
final class PowerStateInfoImpl extends AbstractPacketAdapter implements PowerStateInfo
{

  @ServiceProvider(service = PacketAdapterFactory.class, path = Packet.LOOKUPPATH)
  public static final class Factory implements PacketAdapterFactory
  {

    private static final Set<PacketSelector> SELECTOR = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(
            new PacketSelector(CommandGroup.SYSTEM,
                               CommandGroup.SYSTEM_POWER,
                               CommandMode.COMMAND,
                               4),
            new PacketSelector(CommandGroup.SYSTEM,
                               CommandGroup.SYSTEM_POWER,
                               CommandMode.EVENT,
                               4),
            new PacketSelector(CommandGroup.SYSTEM,
                               CommandGroup.SYSTEM_POWER,
                               CommandMode.ACK,
                               4))));

    @Override
    public boolean isValid(PacketSelector selector)
    {
      return SELECTOR.stream().filter((s) -> s.matches(selector)).findAny().isPresent();
    }

    @Override
    public PowerStateInfo convert(Packet packet)
    {
      return new PowerStateInfoImpl(packet);
    }

    @Override
    public Class<? extends PacketAdapter> type(Packet obj)
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
  public PowerMode getMode()
  {
    return PowerMode.valueOfMagic(buffer.get(offset + 3) & 0xff);
  }

  @Override
  public String toString()
  {
    StringBuilder builder = new StringBuilder("SYSTEM_POWER(SystemNID: 0x");
    Utils.appendHexString(getSystemNID(),
                          builder,
                          4);
    builder.append(", Port: ");
    builder.append(getOutput());
    builder.append(", Mode: ");
    builder.append(getMode());
    return builder.append(')').toString();
  }

}
