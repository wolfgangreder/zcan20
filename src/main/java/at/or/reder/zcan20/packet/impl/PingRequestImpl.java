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
import at.or.reder.zcan20.packet.Packet;
import at.or.reder.zcan20.packet.PacketAdapter;
import at.or.reder.zcan20.packet.PacketAdapterFactory;
import at.or.reder.zcan20.packet.PingRequest;
import at.or.reder.dcc.util.Utils;
import javax.validation.constraints.NotNull;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Wolfgang Reder
 */
final class PingRequestImpl extends AbstractPacketAdapter implements PingRequest
{

  @ServiceProvider(service = PacketAdapterFactory.class, path = Packet.LOOKUPPATH)
  public static final class Factory implements PacketAdapterFactory
  {

    private static final PacketSelector SELECTOR = new PacketSelector(CommandGroup.NETWORK,
                                                                      CommandGroup.NETWORK_PING,
                                                                      CommandMode.REQUEST,
                                                                      2);

    @Override
    public boolean isValid(PacketSelector selector)
    {
      return SELECTOR.matches(selector);
    }

    @Override
    public PingRequest convert(Packet packet)
    {
      return new PingRequestImpl(packet);
    }

    @Override
    public Class<? extends PacketAdapter> type(Packet obj)
    {
      return PingRequest.class;
    }

  }

  private PingRequestImpl(@NotNull Packet packet)
  {
    super(packet);
    if (packet.getCommandGroup() != CommandGroup.NETWORK) {
      throw new IllegalArgumentException("illegal commandGroup");
    }
    if (packet.getCommand() != CommandGroup.NETWORK_PING) {
      throw new IllegalArgumentException("illegal command");
    }
    if (packet.getCommandMode() != CommandMode.REQUEST) {
      throw new IllegalArgumentException("illegal commandMode");
    }
    if (buffer.capacity() < 8) {
      throw new IllegalArgumentException("illegal datasize");
    }
  }

  @Override
  public short getNID()
  {
    return buffer.getShort(0);
  }

  @Override
  public String toString()
  {
    StringBuilder builder = new StringBuilder("PING(0x");
    Utils.appendHexString(getNID(),
                          builder,
                          4);
    return builder.append(')').toString();
  }

}
