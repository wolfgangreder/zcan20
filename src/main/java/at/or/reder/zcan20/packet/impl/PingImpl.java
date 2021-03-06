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

import at.or.reder.dcc.util.DCCUtils;
import at.or.reder.zcan20.CommandGroup;
import at.or.reder.zcan20.CommandMode;
import at.or.reder.zcan20.PacketSelector;
import at.or.reder.zcan20.packet.Packet;
import at.or.reder.zcan20.packet.PacketAdapterFactory;
import at.or.reder.zcan20.packet.Ping;
import javax.validation.constraints.NotNull;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Wolfgang Reder
 */
final class PingImpl extends AbstractPacketAdapter implements Ping
{

  @ServiceProvider(service = PacketAdapterFactory.class, path = Packet.LOOKUPPATH)
  public static final class Factory implements PacketAdapterFactory<Ping>
  {

    @Override
    public boolean isValid(PacketSelector selector)
    {
      return SELECTOR.test(selector);
    }

    @Override
    public Ping convert(Packet packet)
    {
      return new PingImpl(packet);
    }

    @Override
    public Class<? extends Ping> type(Packet obj)
    {
      return Ping.class;
    }

  }

  private PingImpl(@NotNull Packet packet)
  {
    super(packet);
    if (packet.getCommandGroup() != CommandGroup.NETWORK) {
      throw new IllegalArgumentException("illegal commandGroup");
    }
    if (packet.getCommand() != CommandGroup.NETWORK_PING) {
      throw new IllegalArgumentException("illegal command");
    }
    if (packet.getCommandMode() != CommandMode.EVENT) {
      throw new IllegalArgumentException("illegal commandMode");
    }
    if (buffer.capacity() < 8) {
      throw new IllegalArgumentException("illegal datasize");
    }
  }

  @Override
  public int getMasterNID()
  {
    return buffer.getInt(0);
  }

  @Override
  public short getType()
  {
    return buffer.getShort(4);
  }

  @Override
  public short getSession()
  {
    return buffer.getShort(6);
  }

  @Override
  public String toString()
  {
    StringBuilder builder = new StringBuilder("PING(0x");
    DCCUtils.appendHexString(getMasterNID(),
                          builder,
                          8);
    builder.append(", 0x");
    DCCUtils.appendHexString(getType(),
                          builder,
                          4);
    builder.append(", 0x");
    DCCUtils.appendHexString(getSession(),
                          builder,
                          4);
    return builder.append(')').toString();
  }

}
