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
package com.reder.zcan20.packet.impl;

import com.reder.zcan20.CommandGroup;
import com.reder.zcan20.CommandMode;
import com.reder.zcan20.packet.NIDOnlyPacketAdapter;
import com.reder.zcan20.packet.Packet;
import com.reder.zcan20.packet.PacketAdapterFactory;
import com.reder.zcan20.util.Utils;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Wolfgang Reder
 */
public final class NIDOnlyPacketAdapterImpl extends AbstractPacketAdapter implements NIDOnlyPacketAdapter
{

  @ServiceProvider(service = PacketAdapterFactory.class, path = Packet.LOOKUPPATH)
  public static final class Factory implements PacketAdapterFactory
  {

    @Override
    public boolean isValid(CommandGroup group,
                           int command,
                           CommandMode mode)
    {
      return (group == CommandGroup.NETWORK && command == CommandGroup.NETWORK_PORT_CLOSE && mode == CommandMode.COMMAND)
                     || (group == CommandGroup.LOCO && command == CommandGroup.LOCO_STATE && mode == CommandMode.REQUEST);
    }

    @Override
    public NIDOnlyPacketAdapter createAdapter(Packet packet)
    {
      return new NIDOnlyPacketAdapterImpl(packet);
    }

  }

  public NIDOnlyPacketAdapterImpl(Packet packet)
  {
    super(packet);
  }

  @Override
  public short getMasterNID()
  {
    return buffer.getShort(0);
  }

  @Override
  public String toString()
  {
    StringBuilder builder = new StringBuilder("LOGOUT(0x");
    Utils.appendHexString(getMasterNID() & 0xffff,
                          builder,
                          4);
    return builder.append(')').toString();
  }

}
