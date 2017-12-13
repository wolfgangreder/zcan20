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
import com.reder.zcan20.DataGroup;
import com.reder.zcan20.packet.DataGroupCountRequestAdapter;
import com.reder.zcan20.packet.DataGroupIndexRequestAdapter;
import com.reder.zcan20.packet.LogoutPacketAdapter;
import com.reder.zcan20.packet.Packet;
import static org.testng.AssertJUnit.*;
import org.testng.annotations.Test;

/**
 *
 * @author Wolfgang Reder
 */
public class DefaultPacketBuilderNGTest
{

  @Test
  public void testBuildLoginPacket()
  {
    DefaultPacketBuilder builder = new DefaultPacketBuilder((short) 0xcafe);
    Packet packet = builder.buildLoginPacket();
    assertNotNull(packet);
    assertEquals(0xcafe,
                 packet.getSenderNID() & 0xffff);
    assertEquals(CommandGroup.NETWORK,
                 packet.getCommandGroup());
    assertEquals(CommandMode.COMMAND,
                 packet.getCommandMode());
    assertEquals(CommandGroup.NETWORK_PORT_OPEN,
                 packet.getCommand());
  }

  @Test
  public void testBuildLogoutPacket()
  {
    DefaultPacketBuilder builder = new DefaultPacketBuilder((short) 0xbabe);
    Packet packet = builder.buildLogoutPacket((short) 0xcafe);
    assertNotNull(packet);
    assertEquals(0xbabe,
                 packet.getSenderNID() & 0xffff);
    assertEquals(CommandGroup.NETWORK,
                 packet.getCommandGroup());
    assertEquals(CommandMode.COMMAND,
                 packet.getCommandMode());
    assertEquals(CommandGroup.NETWORK_PORT_CLOSE,
                 packet.getCommand());
    LogoutPacketAdapter adapter = packet.getAdapter(LogoutPacketAdapter.class);
    assertNotNull(adapter);
    assertEquals(0xcafe,
                 adapter.getMasterNID() & 0xffff);
    assertSame(packet,
               adapter.getPacket());
  }

  @Test
  public void testBuildDataGroupCountPacket()
  {
    DefaultPacketBuilder builder = new DefaultPacketBuilder((short) 0xbabe);
    Packet packet = builder.buildDataGroupCountPacket((short) 0xcafe,
                                                      DataGroup.LOCO);
    assertNotNull(packet);
    assertEquals(0xbabe,
                 packet.getSenderNID() & 0xffff);
    assertEquals(CommandGroup.DATA,
                 packet.getCommandGroup());
    assertEquals(CommandMode.REQUEST,
                 packet.getCommandMode());
    assertEquals(CommandGroup.DATA_GROUP_COUNT,
                 packet.getCommand());
    DataGroupCountRequestAdapter adapter = packet.getAdapter(DataGroupCountRequestAdapter.class);
    assertNotNull(adapter);
    assertEquals(0xcafe,
                 adapter.getMasterNID() & 0xffff);
    assertSame(DataGroup.LOCO,
               adapter.getDataGroup());
    assertSame(packet,
               adapter.getPacket());
  }

  @Test
  public void testBuildDataPacketIndex()
  {
    final DataGroup grp = DataGroup.MX8;
    final short index = 0x1234;
    DefaultPacketBuilder builder = new DefaultPacketBuilder((short) 0xbabe);
    Packet packet = builder.buildDataPacket((short) 0xcafe,
                                            grp,
                                            index);
    assertNotNull(packet);
    assertEquals(0xbabe,
                 packet.getSenderNID() & 0xffff);
    assertEquals(CommandGroup.DATA,
                 packet.getCommandGroup());
    assertEquals(CommandMode.REQUEST,
                 packet.getCommandMode());
    assertEquals(CommandGroup.DATA_ITEMLIST_INDEX,
                 packet.getCommand());
    DataGroupIndexRequestAdapter adapter = packet.getAdapter(DataGroupIndexRequestAdapter.class);
    assertNotNull(adapter);
    assertEquals(index,
                 adapter.getIndex());
    assertEquals(0xcafe,
                 adapter.getMasterNID() & 0xffff);
    assertSame(grp,
               adapter.getDataGroup());
    assertSame(packet,
               adapter.getPacket());
  }

}
