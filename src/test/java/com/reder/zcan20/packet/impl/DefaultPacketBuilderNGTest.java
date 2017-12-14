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
import com.reder.zcan20.InterfaceOptionType;
import com.reder.zcan20.ModuleInfoType;
import com.reder.zcan20.PowerOutput;
import com.reder.zcan20.ZCANFactory;
import com.reder.zcan20.packet.DataGroupCountRequestAdapter;
import com.reder.zcan20.packet.DataGroupIndexRequestAdapter;
import com.reder.zcan20.packet.DataGroupNIDRequestAdapter;
import com.reder.zcan20.packet.DataNameExtRequestAdapter;
import com.reder.zcan20.packet.InterfaceOptionRequestAdapter;
import com.reder.zcan20.packet.ModuleInfoRequestAdapter;
import com.reder.zcan20.packet.ModulePowerInfoRequestAdapter;
import com.reder.zcan20.packet.NIDOnlyPacketAdapter;
import com.reder.zcan20.packet.Packet;
import com.reder.zcan20.packet.PowerInfoRequestAdapter;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;
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
    assertSame(CommandGroup.NETWORK,
               packet.getCommandGroup());
    assertSame(CommandMode.COMMAND,
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
    assertSame(CommandGroup.NETWORK,
               packet.getCommandGroup());
    assertSame(CommandMode.COMMAND,
               packet.getCommandMode());
    assertEquals(CommandGroup.NETWORK_PORT_CLOSE,
                 packet.getCommand());
    NIDOnlyPacketAdapter adapter = packet.getAdapter(NIDOnlyPacketAdapter.class);
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
    assertSame(CommandGroup.DATA,
               packet.getCommandGroup());
    assertSame(CommandMode.REQUEST,
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
    assertSame(CommandGroup.DATA,
               packet.getCommandGroup());
    assertSame(CommandMode.REQUEST,
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

  @Test
  public void testBuildDataPacketNID()
  {
    final short nid = 0x1234;
    final short masterNID = (short) 0xcafe;
    DefaultPacketBuilder builder = new DefaultPacketBuilder((short) 0xbabe);
    Packet packet = builder.buildDataPacket(masterNID,
                                            nid);
    assertNotNull(packet);
    assertEquals(0xbabe,
                 packet.getSenderNID() & 0xffff);
    assertSame(CommandGroup.DATA,
               packet.getCommandGroup());
    assertSame(CommandMode.REQUEST,
               packet.getCommandMode());
    assertEquals(CommandGroup.DATA_ITEMLIST_NID,
                 packet.getCommand());
    DataGroupNIDRequestAdapter adapter = packet.getAdapter(DataGroupNIDRequestAdapter.class);
    assertNotNull(adapter);
    assertEquals(masterNID,
                 adapter.getMasterNID());
    assertEquals(nid,
                 adapter.getObjectNID());
  }

  @Test
  public void testBuildDataNameExt()
  {
    short myNID = (short) 0xbabe;
    short masterNID = (short) 0xcafe;
    short objectNID = (short) 0xaffe;
    int subID = 0x12345678;
    int val1 = 0x9abcdef0;
    int val2 = 0xfedcba98;
    DefaultPacketBuilder builder = new DefaultPacketBuilder(myNID);
    Packet packet = builder.buildDataNameExt(masterNID,
                                             objectNID,
                                             subID,
                                             val1,
                                             val2);
    assertNotNull(packet);
    assertEquals(myNID,
                 packet.getSenderNID());
    assertSame(CommandGroup.DATA,
               packet.getCommandGroup());
    assertSame(CommandMode.REQUEST,
               packet.getCommandMode());
    assertEquals(CommandGroup.DATA_NAME_EXT,
                 packet.getCommand());
    DataNameExtRequestAdapter adapter = packet.getAdapter(DataNameExtRequestAdapter.class);
    assertNotNull(adapter);
    assertEquals(masterNID,
                 adapter.getMasterNID());
    assertEquals(objectNID,
                 adapter.getObjectNID());
    assertEquals(subID,
                 adapter.getSubID());
    assertEquals(val1,
                 adapter.getVal1());
    assertEquals(val2,
                 adapter.getVal2());
  }

  @Test
  public void testBuildModulePowerInfo()
  {
    short myNID = (short) 0xbabe;
    short masterNid = (short) 0xcafe;
    PowerOutput out = PowerOutput.OUT_1;
    DefaultPacketBuilder builder = new DefaultPacketBuilder(myNID);
    Packet packet = builder.buildModulePowerInfoPacket(masterNid,
                                                       out);
    assertNotNull(packet);
    assertEquals(myNID,
                 packet.getSenderNID());
    assertSame(CommandGroup.CONFIG,
               packet.getCommandGroup());
    assertSame(CommandMode.REQUEST,
               packet.getCommandMode());
    assertEquals(CommandGroup.CONFIG_POWER_INFO,
                 packet.getCommand());
    ModulePowerInfoRequestAdapter adapter = packet.getAdapter(ModulePowerInfoRequestAdapter.class);
    assertNotNull(adapter);
    assertEquals(masterNid,
                 adapter.getTargetNID());
    assertSame(out,
               adapter.getOutput());

    out = PowerOutput.OUT_2;
    builder = new DefaultPacketBuilder(myNID);
    packet = builder.buildModulePowerInfoPacket(masterNid,
                                                out);
    assertNotNull(packet);
    assertEquals(myNID,
                 packet.getSenderNID());
    assertSame(CommandGroup.CONFIG,
               packet.getCommandGroup());
    assertSame(CommandMode.REQUEST,
               packet.getCommandMode());
    assertEquals(CommandGroup.CONFIG_POWER_INFO,
                 packet.getCommand());
    adapter = packet.getAdapter(ModulePowerInfoRequestAdapter.class);
    assertNotNull(adapter);
    assertEquals(masterNid,
                 adapter.getTargetNID());
    assertSame(out,
               adapter.getOutput());
    out = PowerOutput.BOOSTER;
    builder = new DefaultPacketBuilder(myNID);
    packet = builder.buildModulePowerInfoPacket(masterNid,
                                                out);
    assertNotNull(packet);
    assertEquals(myNID,
                 packet.getSenderNID());
    assertSame(CommandGroup.CONFIG,
               packet.getCommandGroup());
    assertSame(CommandMode.REQUEST,
               packet.getCommandMode());
    assertEquals(CommandGroup.CONFIG_POWER_INFO,
                 packet.getCommand());
    adapter = packet.getAdapter(ModulePowerInfoRequestAdapter.class);
    assertNotNull(adapter);
    assertEquals(masterNid,
                 adapter.getTargetNID());
    assertSame(out,
               adapter.getOutput());
  }

  @Test(expectedExceptions = {IllegalArgumentException.class})
  public void testBuildModulePowerInfoFail3()
  {
    short myNID = (short) 0xbabe;
    short masterNid = (short) 0xcafe;
    PowerOutput out = PowerOutput.OUT_3;
    DefaultPacketBuilder builder = new DefaultPacketBuilder(myNID);
    builder.buildModulePowerInfoPacket(masterNid,
                                       out);
  }

  @Test(expectedExceptions = {IllegalArgumentException.class})
  public void testBuildModulePowerInfoFail4()
  {
    short myNID = (short) 0xbabe;
    short masterNid = (short) 0xcafe;
    PowerOutput out = PowerOutput.OUT_4;
    DefaultPacketBuilder builder = new DefaultPacketBuilder(myNID);
    builder.buildModulePowerInfoPacket(masterNid,
                                       out);
  }

  @Test(expectedExceptions = {IllegalArgumentException.class})
  public void testBuildModulePowerInfoFail5()
  {
    short myNID = (short) 0xbabe;
    short masterNid = (short) 0xcafe;
    PowerOutput out = PowerOutput.OUT_5;
    DefaultPacketBuilder builder = new DefaultPacketBuilder(myNID);
    builder.buildModulePowerInfoPacket(masterNid,
                                       out);
  }

  @Test(expectedExceptions = {IllegalArgumentException.class})
  public void testBuildModulePowerInfoFail6()
  {
    short myNID = (short) 0xbabe;
    short masterNid = (short) 0xcafe;
    PowerOutput out = PowerOutput.OUT_6;
    DefaultPacketBuilder builder = new DefaultPacketBuilder(myNID);
    builder.buildModulePowerInfoPacket(masterNid,
                                       out);
  }

  @Test(expectedExceptions = {IllegalArgumentException.class})
  public void testBuildModulePowerInfoFail7()
  {
    short myNID = (short) 0xbabe;
    short masterNid = (short) 0xcafe;
    PowerOutput out = PowerOutput.OUT_7;
    DefaultPacketBuilder builder = new DefaultPacketBuilder(myNID);
    builder.buildModulePowerInfoPacket(masterNid,
                                       out);
  }

  @Test
  public void testBuildModuleInfoPacket()
  {
    short myNID = (short) 0xbabe;
    short nid = (short) 0x1234;
    ModuleInfoType info = ModuleInfoType.HW_VERSION;
    DefaultPacketBuilder builder = new DefaultPacketBuilder(myNID);
    Packet packet = builder.buildModuleInfoPacket(nid,
                                                  info);
    assertNotNull(packet);
    assertSame(CommandGroup.CONFIG,
               packet.getCommandGroup());
    assertSame(CommandMode.REQUEST,
               packet.getCommandMode());
    assertEquals(CommandGroup.CONFIG_MODULE_INFO,
                 packet.getCommand());
    assertEquals(myNID,
                 packet.getSenderNID());
    ModuleInfoRequestAdapter adapter = packet.getAdapter(ModuleInfoRequestAdapter.class);
    assertNotNull(adapter);
    assertEquals(nid,
                 adapter.getModuleNID());
    assertSame(info,
               adapter.getInfoType());

    info = ModuleInfoType.SW_BUILD_DATE;
    builder = new DefaultPacketBuilder(myNID);
    packet = builder.buildModuleInfoPacket(nid,
                                           info);
    assertNotNull(packet);
    assertSame(CommandGroup.CONFIG,
               packet.getCommandGroup());
    assertSame(CommandMode.REQUEST,
               packet.getCommandMode());
    assertEquals(CommandGroup.CONFIG_MODULE_INFO,
                 packet.getCommand());
    assertEquals(myNID,
                 packet.getSenderNID());
    adapter = packet.getAdapter(ModuleInfoRequestAdapter.class);
    assertNotNull(adapter);
    assertEquals(nid,
                 adapter.getModuleNID());
    assertSame(info,
               adapter.getInfoType());

    info = ModuleInfoType.SW_BUILD_TIME;
    builder = new DefaultPacketBuilder(myNID);
    packet = builder.buildModuleInfoPacket(nid,
                                           info);
    assertNotNull(packet);
    assertSame(CommandGroup.CONFIG,
               packet.getCommandGroup());
    assertSame(CommandMode.REQUEST,
               packet.getCommandMode());
    assertEquals(CommandGroup.CONFIG_MODULE_INFO,
                 packet.getCommand());
    assertEquals(myNID,
                 packet.getSenderNID());
    adapter = packet.getAdapter(ModuleInfoRequestAdapter.class);
    assertNotNull(adapter);
    assertEquals(nid,
                 adapter.getModuleNID());
    assertSame(info,
               adapter.getInfoType());

    info = ModuleInfoType.SW_VERSION;
    builder = new DefaultPacketBuilder(myNID);
    packet = builder.buildModuleInfoPacket(nid,
                                           info);
    assertNotNull(packet);
    assertSame(CommandGroup.CONFIG,
               packet.getCommandGroup());
    assertSame(CommandMode.REQUEST,
               packet.getCommandMode());
    assertEquals(CommandGroup.CONFIG_MODULE_INFO,
                 packet.getCommand());
    assertEquals(myNID,
                 packet.getSenderNID());
    adapter = packet.getAdapter(ModuleInfoRequestAdapter.class);
    assertNotNull(adapter);
    assertEquals(nid,
                 adapter.getModuleNID());
    assertSame(info,
               adapter.getInfoType());

    info = ModuleInfoType.valueOf((short) 0xcafe);
    builder = new DefaultPacketBuilder(myNID);
    packet = builder.buildModuleInfoPacket(nid,
                                           info);
    assertNotNull(packet);
    assertSame(CommandGroup.CONFIG,
               packet.getCommandGroup());
    assertSame(CommandMode.REQUEST,
               packet.getCommandMode());
    assertEquals(CommandGroup.CONFIG_MODULE_INFO,
                 packet.getCommand());
    assertEquals(myNID,
                 packet.getSenderNID());
    adapter = packet.getAdapter(ModuleInfoRequestAdapter.class);
    assertNotNull(adapter);
    assertEquals(nid,
                 adapter.getModuleNID());
    assertSame(info,
               adapter.getInfoType());
  }

  @Test
  public void testBuildInterfaceOptionPacket()
  {
    short myNID = (short) 0xbabe;
    short nid = (short) 0x1234;
    InterfaceOptionType info = InterfaceOptionType.SW_PROVIDER;
    DefaultPacketBuilder builder = new DefaultPacketBuilder(myNID);
    Packet packet = builder.buildInterfaceOptionPacket(nid,
                                                       info);
    assertNotNull(packet);
    assertSame(CommandGroup.NETWORK,
               packet.getCommandGroup());
    assertSame(CommandMode.REQUEST,
               packet.getCommandMode());
    assertEquals(CommandGroup.NETWORK_INTERFACE_OPTION,
                 packet.getCommand());
    assertEquals(myNID,
                 packet.getSenderNID());
    InterfaceOptionRequestAdapter adapter = packet.getAdapter(InterfaceOptionRequestAdapter.class);
    assertNotNull(adapter);
    assertEquals(nid,
                 adapter.getObjectNID());
    assertSame(info,
               adapter.getOptionType());

    info = InterfaceOptionType.valueOf(0x3421);
    builder = new DefaultPacketBuilder(myNID);
    packet = builder.buildInterfaceOptionPacket(nid,
                                                info);
    assertNotNull(packet);
    assertSame(CommandGroup.NETWORK,
               packet.getCommandGroup());
    assertSame(CommandMode.REQUEST,
               packet.getCommandMode());
    assertEquals(CommandGroup.NETWORK_INTERFACE_OPTION,
                 packet.getCommand());
    assertEquals(myNID,
                 packet.getSenderNID());
    adapter = packet.getAdapter(InterfaceOptionRequestAdapter.class);
    assertNotNull(adapter);
    assertEquals(nid,
                 adapter.getObjectNID());
    assertSame(info,
               adapter.getOptionType());
  }

  @Test
  public void testBuildGetPowerModePacket()
  {
    short myNID = (short) 0xbabe;
    short nid = (short) 0x1234;
    Set<PowerOutput> outputs;

    for (PowerOutput currentOut : PowerOutput.values()) {
      if (currentOut == PowerOutput.UNKNOWN) {
        continue;
      }
      outputs = Collections.singleton(currentOut);
      DefaultPacketBuilder builder = new DefaultPacketBuilder(myNID);
      Packet packet = builder.buildGetPowerModePacket(nid,
                                                      outputs);
      assertNotNull(packet);
      assertSame(CommandGroup.SYSTEM,
                 packet.getCommandGroup());
      assertSame(CommandMode.REQUEST,
                 packet.getCommandMode());
      assertEquals(CommandGroup.SYSTEM_POWER,
                   packet.getCommand());
      assertEquals(myNID,
                   packet.getSenderNID());
      PowerInfoRequestAdapter adapter = packet.getAdapter(PowerInfoRequestAdapter.class);
      assertNotNull(adapter);
      assertEquals(nid,
                   adapter.getMasterNID());
      Set<PowerOutput> out = adapter.getOutputs();
      assertEquals(outputs.size(),
                   out.size());
      for (PowerOutput o : outputs) {
        assertTrue(out.contains(o));
      }
    }
    outputs = EnumSet.allOf(PowerOutput.class);
    outputs.remove(PowerOutput.UNKNOWN);
    DefaultPacketBuilder builder = new DefaultPacketBuilder(myNID);
    Packet packet = builder.buildGetPowerModePacket(nid,
                                                    outputs);
    assertNotNull(packet);
    assertSame(CommandGroup.SYSTEM,
               packet.getCommandGroup());
    assertSame(CommandMode.REQUEST,
               packet.getCommandMode());
    assertEquals(CommandGroup.SYSTEM_POWER,
                 packet.getCommand());
    assertEquals(myNID,
                 packet.getSenderNID());
    PowerInfoRequestAdapter adapter = packet.getAdapter(PowerInfoRequestAdapter.class);
    assertNotNull(adapter);
    assertEquals(nid,
                 adapter.getMasterNID());
    Set<PowerOutput> out = adapter.getOutputs();
    assertEquals(outputs.size(),
                 out.size());
    for (PowerOutput o : outputs) {
      assertTrue(out.contains(o));
    }
  }

  @Test(expectedExceptions = {NullPointerException.class})
  public void testGetBuildPowerModePacketFailNPE()
  {
    DefaultPacketBuilder builder = new DefaultPacketBuilder((short) 0);
    builder.buildGetPowerModePacket((short) 0,
                                    null);
  }

  @Test(expectedExceptions = {IllegalArgumentException.class})
  public void testGetBuildPowerModePacketFailEmptySet()
  {
    DefaultPacketBuilder builder = new DefaultPacketBuilder((short) 0);
    builder.buildGetPowerModePacket((short) 0,
                                    Collections.emptySet());
  }

  @Test(expectedExceptions = {IllegalArgumentException.class})
  public void testGetBuildPowerModePacketFailUnknown()
  {
    DefaultPacketBuilder builder = new DefaultPacketBuilder((short) 0);
    EnumSet<PowerOutput> set = EnumSet.of(PowerOutput.BOOSTER,
                                          PowerOutput.UNKNOWN);
    builder.buildGetPowerModePacket((short) 0,
                                    set);
  }

  @Test
  public void testGetLocoStatePacket()
  {
    short myNID = (short) 0xcafe;
    short locoNid = ZCANFactory.LOCO_MAX;
    DefaultPacketBuilder builder = new DefaultPacketBuilder(myNID);
    Packet packet = builder.buildLocoStatePacket(locoNid);
    assertNotNull(packet);
    assertEquals(myNID,
                 packet.getSenderNID());
    assertSame(CommandGroup.LOCO,
               packet.getCommandGroup());
    assertSame(CommandMode.REQUEST,
               packet.getCommandMode());
    assertEquals(CommandGroup.LOCO_STATE,
                 packet.getCommand());
    NIDOnlyPacketAdapter adapter = packet.getAdapter(NIDOnlyPacketAdapter.class);
    assertEquals(locoNid,
                 adapter.getMasterNID());
    assertSame(packet,
               adapter.getPacket());
    locoNid = 0;
    builder = new DefaultPacketBuilder(myNID);
    packet = builder.buildLocoStatePacket(locoNid);
    assertNotNull(packet);
    assertEquals(myNID,
                 packet.getSenderNID());
    assertSame(CommandGroup.LOCO,
               packet.getCommandGroup());
    assertSame(CommandMode.REQUEST,
               packet.getCommandMode());
    assertEquals(CommandGroup.LOCO_STATE,
                 packet.getCommand());
    adapter = packet.getAdapter(NIDOnlyPacketAdapter.class);
    assertEquals(locoNid,
                 adapter.getMasterNID());
    assertSame(packet,
               adapter.getPacket());
  }

  @Test(expectedExceptions = {IllegalArgumentException.class})
  public void testGetLocoStatePacketFail1()
  {
    short myNID = (short) 0xcafe;
    short locoNid = ZCANFactory.LOCO_MAX + 1;
    DefaultPacketBuilder builder = new DefaultPacketBuilder(myNID);
    builder.buildLocoStatePacket(locoNid);
  }

}
