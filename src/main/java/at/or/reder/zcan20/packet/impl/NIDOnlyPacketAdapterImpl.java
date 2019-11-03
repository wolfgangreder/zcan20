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
import at.or.reder.zcan20.CommandGroup;
import at.or.reder.zcan20.PacketSelector;
import at.or.reder.zcan20.packet.NIDOnlyPacketAdapter;
import at.or.reder.zcan20.packet.Packet;
import at.or.reder.zcan20.packet.PacketAdapter;
import at.or.reder.zcan20.packet.PacketAdapterFactory;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Wolfgang Reder
 */
final class NIDOnlyPacketAdapterImpl extends AbstractPacketAdapter implements NIDOnlyPacketAdapter
{

  @ServiceProvider(service = PacketAdapterFactory.class, path = Packet.LOOKUPPATH)
  public static final class Factory implements PacketAdapterFactory
  {

    @Override
    public boolean isValid(PacketSelector selector)
    {
      return SELECTOR.test(selector);
//      return (group == CommandGroup.NETWORK && command == CommandGroup.NETWORK_PORT_CLOSE && mode == CommandMode.COMMAND)
//                     || (group == CommandGroup.LOCO && command == CommandGroup.LOCO_STATE && mode == CommandMode.REQUEST)
//                     || (group == CommandGroup.LOCO && command == CommandGroup.LOCO_MODE && mode == CommandMode.REQUEST)
//                     || (group == CommandGroup.LOCO && command == CommandGroup.LOCO_FUNC_INFO && mode == CommandMode.REQUEST)
//                     || (group == CommandGroup.LOCO && command == CommandGroup.LOCO_FUNC_SWITCH && mode == CommandMode.REQUEST)
//                     || (group == CommandGroup.LOCO && command == CommandGroup.LOCO_SPEED && mode == CommandMode.REQUEST);
    }

    private String getPacketName(Packet packet)
    {
      CommandGroup group = packet.getCommandGroup();
      if (group == CommandGroup.NETWORK) {
        return "PORT_CLOSE";
      } else if (group == CommandGroup.LOCO) {
        switch (packet.getCommand()) {
          case CommandGroup.LOCO_STATE:
            return "LOCO_STATE";
          case CommandGroup.LOCO_MODE:
            return "LOCO_MODE";
          case CommandGroup.LOCO_SPEED:
            return "LOCO_SPEED";
          case CommandGroup.LOCO_FUNC_INFO:
            return "LOCO_FUNC";
        }
      }
      return "NIDONYLPACKET";
    }

    @Override
    public Class<? extends PacketAdapter> type(Packet obj)
    {
      return NIDOnlyPacketAdapter.class;
    }

    @Override
    public NIDOnlyPacketAdapter convert(Packet packet)
    {
      return new NIDOnlyPacketAdapterImpl(packet,
                                          getPacketName(packet));
    }

  }
  private final String packetName;

  private NIDOnlyPacketAdapterImpl(Packet packet,
                                   String packetName)
  {
    super(packet);
    this.packetName = packetName;
  }

  @Override
  public short getMasterNID()
  {
    return buffer.getShort(0);
  }

  @Override
  public String toString()
  {
    StringBuilder builder = new StringBuilder(packetName);
    builder.append("(0x");
    Utils.appendHexString(getMasterNID() & 0xffff,
                          builder,
                          4);
    return builder.append(')').toString();
  }

}
