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
import at.or.reder.zcan20.DataGroup;
import at.or.reder.zcan20.InterfaceOptionType;
import at.or.reder.zcan20.ModuleInfoType;
import at.or.reder.zcan20.Protocol;
import at.or.reder.zcan20.SpeedFlags;
import at.or.reder.zcan20.SpeedSteps;
import at.or.reder.zcan20.SpeedlimitMode;
import at.or.reder.zcan20.ZCANFactory;
import at.or.reder.zcan20.impl.UDPMarshaller;
import at.or.reder.zcan20.packet.Packet;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;
import static org.testng.AssertJUnit.*;
import org.testng.annotations.Test;

/**
 *
 * @author Wolfgang Reder
 */
public class UDPMarshallerTest
{

  private final short nid = (short) 0xcafe;
  private final short masterNid = (short) 0xbabe;

  @Test
  public void testMarshalPortOpen()
  {
    Packet packet = ZCANFactory.createPacketBuilder(nid).buildLoginPacket();
    assertNotNull(packet);
    assertEquals(8,
                 UDPMarshaller.getRequiredBufferSize(packet));
    ByteBuffer buffer = ByteBuffer.allocate(8);
    int bytesWritten = UDPMarshaller.marshalPacket(packet,
                                                   buffer);
    buffer.order(ByteOrder.LITTLE_ENDIAN);
    assertEquals(bytesWritten,
                 8);
    assertEquals((short) 0x00,
                 buffer.getShort());
    assertEquals((short) 0x0000,
                 buffer.getShort());
    assertEquals((byte) 0x0a,
                 buffer.get());
    assertEquals((byte) 0x19,
                 buffer.get());
    assertEquals(nid,
                 buffer.getShort());
  }

  @Test
  public void testMarshalPortClose()
  {
    Packet packet = ZCANFactory.createPacketBuilder(nid).buildLogoutPacket(masterNid);
    assertNotNull(packet);
    assertEquals(10,
                 UDPMarshaller.getRequiredBufferSize(packet));
    ByteBuffer buffer = DCCUtils.allocateLEBuffer(10);
    int bytesWritten = UDPMarshaller.marshalPacket(packet,
                                                   buffer);
    assertEquals(bytesWritten,
                 10);
    assertEquals((short) 0x0002,
                 buffer.getShort());
    assertEquals((short) 0x0000,
                 buffer.getShort());
    assertEquals((byte) 0x0a,
                 buffer.get());
    assertEquals((byte) 0x1d,
                 buffer.get());
    assertEquals(DCCUtils.byte1(nid),
                 buffer.get());
    assertEquals(DCCUtils.byte2(nid),
                 buffer.get());
    assertEquals(DCCUtils.byte1(masterNid),
                 buffer.get());
    assertEquals(DCCUtils.byte2(masterNid),
                 buffer.get());
  }

  @Test
  public void testMarshalDataGroupCount()
  {
    final DataGroup grp = DataGroup.MX9;
    byte[] expected = DCCUtils.toByteArray(4,
                                        0,
                                        0,
                                        0,
                                        7,
                                        0,
                                        nid & 0xff,
                                        (nid & 0xff00) >> 8,
                                        masterNid & 0xff,
                                        (masterNid & 0xff00) >> 8,
                                        grp.getMagic() & 0xff,
                                        (grp.getMagic() & 0xff00) >> 8);
    Packet packet = ZCANFactory.createPacketBuilder(nid).buildDataGroupCountPacket(masterNid,
                                                                                   grp);
    assertNotNull(packet);
    assertEquals(expected.length,
                 UDPMarshaller.getRequiredBufferSize(packet));
    ByteBuffer buffer = ByteBuffer.allocate(expected.length);
    int bytesWritten = UDPMarshaller.marshalPacket(packet,
                                                   buffer);
    assertEquals(expected.length,
                 bytesWritten);
    assertEquals(expected,
                 buffer.array());
  }

  @Test
  public void testMarshalDataPacketIndex()
  {
    final DataGroup grp = DataGroup.MX9;
    final short index = (short) 0x1234;
    final byte[] expected = DCCUtils.toByteArray(6,
                                              0,
                                              0,
                                              0,
                                              7,
                                              4,
                                              nid & 0xff,
                                              (nid & 0xff00) >> 8,
                                              masterNid & 0xff,
                                              (masterNid & 0xff00) >> 8,
                                              grp.getMagic() & 0xff,
                                              (grp.getMagic() & 0xff00) >> 8,
                                              index & 0xff,
                                              (index & 0xff00) >> 8);

    Packet packet = ZCANFactory.createPacketBuilder(nid).buildDataPacket(masterNid,
                                                                         grp,
                                                                         index);
    assertNotNull(packet);
    assertEquals(expected.length,
                 UDPMarshaller.getRequiredBufferSize(packet));
    ByteBuffer buffer = ByteBuffer.allocate(expected.length);
    int bytesWritten = UDPMarshaller.marshalPacket(packet,
                                                   buffer);
    assertEquals(expected.length,
                 bytesWritten);
    assertEquals(expected,
                 buffer.array());
  }

  @Test
  public void testMarshalDataPacketNID()
  {
    final short objectNid = (short) 0x1234;
    final byte[] expected = DCCUtils.toByteArray(4,
                                              0,
                                              0,
                                              0,
                                              7,
                                              8,
                                              nid & 0xff,
                                              (nid & 0xff00) >> 8,
                                              masterNid & 0xff,
                                              (masterNid & 0xff00) >> 8,
                                              objectNid & 0xff,
                                              (objectNid & 0xff00) >> 8);
    Packet packet = ZCANFactory.createPacketBuilder(nid).buildDataPacket(masterNid,
                                                                         objectNid);
    assertNotNull(packet);
    assertEquals(expected.length,
                 UDPMarshaller.getRequiredBufferSize(packet));
    ByteBuffer buffer = ByteBuffer.allocate(expected.length);
    int bytesWritten = UDPMarshaller.marshalPacket(packet,
                                                   buffer);
    assertEquals(expected.length,
                 bytesWritten);
    assertEquals(expected,
                 buffer.array());
  }

  @Test
  public void testMarshalDataNameExt()
  {
    short objectNID = (short) 0xaffe;
    int subID = 0x12345678;
    int val1 = 0x9abcdef0;
    int val2 = 0xfedcba98;
    final byte[] expected = DCCUtils.toByteArray(16,
                                              0,
                                              0,
                                              0,
                                              7,
                                              33 << 2,
                                              DCCUtils.byte1(nid),
                                              DCCUtils.byte2(nid),
                                              DCCUtils.byte1(masterNid),
                                              DCCUtils.byte2(masterNid),
                                              DCCUtils.byte1(objectNID),
                                              DCCUtils.byte2(objectNID),
                                              DCCUtils.byte1(subID),
                                              DCCUtils.byte2(subID),
                                              DCCUtils.byte3(subID),
                                              DCCUtils.byte4(subID),
                                              DCCUtils.byte1(val1),
                                              DCCUtils.byte2(val1),
                                              DCCUtils.byte3(val1),
                                              DCCUtils.byte4(val1),
                                              DCCUtils.byte1(val2),
                                              DCCUtils.byte2(val2),
                                              DCCUtils.byte3(val2),
                                              DCCUtils.byte4(val2));
    DefaultPacketBuilder builder = new DefaultPacketBuilder(nid);
    Packet packet = builder.buildDataNameExt(masterNid,
                                             objectNID,
                                             subID,
                                             val1,
                                             val2);
    assertNotNull(packet);
    assertEquals(expected.length,
                 UDPMarshaller.getRequiredBufferSize(packet));
    ByteBuffer buffer = ByteBuffer.allocate(expected.length);
    int bytesWritten = UDPMarshaller.marshalPacket(packet,
                                                   buffer);
    assertEquals(expected.length,
                 bytesWritten);
    assertEquals(expected,
                 buffer.array());
  }

  @Test(enabled = false)
  public void testMarshalModulePowerInfo()
  {
    PowerPort out = PowerPort.OUT_1;
    DefaultPacketBuilder builder = new DefaultPacketBuilder(nid);
    final byte[] expected = DCCUtils.toByteArray(3,
                                              0,
                                              0,
                                              0,
                                              8,
                                              0,
                                              DCCUtils.byte1(nid),
                                              DCCUtils.byte2(nid),
                                              DCCUtils.byte1(masterNid),
                                              DCCUtils.byte2(masterNid),
                                              0);
    Packet packet = builder.buildSystemPowerInfoPacket(masterNid,
                                                       Collections.singleton(out));
    assertNotNull(packet);
    assertEquals(expected.length,
                 UDPMarshaller.getRequiredBufferSize(packet));
    ByteBuffer buffer = ByteBuffer.allocate(expected.length);
    int bytesWritten = UDPMarshaller.marshalPacket(packet,
                                                   buffer);
    assertEquals(expected.length,
                 bytesWritten);
    assertEquals(expected,
                 buffer.array());
    out = PowerPort.OUT_2;
    expected[expected.length - 1] = 1;
    packet = builder.buildSystemPowerInfoPacket(masterNid,
                                                Collections.singleton(out));
    assertNotNull(packet);
    buffer = ByteBuffer.allocate(expected.length);
    bytesWritten = UDPMarshaller.marshalPacket(packet,
                                               buffer);
    assertEquals(expected.length,
                 bytesWritten);
    assertEquals(expected,
                 buffer.array());
    out = PowerPort.BOOSTER;
    expected[expected.length - 1] = 2;
    packet = builder.buildSystemPowerInfoPacket(masterNid,
                                                Collections.singleton(out));
    assertNotNull(packet);
    buffer = ByteBuffer.allocate(expected.length);
    bytesWritten = UDPMarshaller.marshalPacket(packet,
                                               buffer);
    assertEquals(expected.length,
                 bytesWritten);
    assertEquals(expected,
                 buffer.array());
  }

  @Test(enabled = false)
  public void testMarshalModuleInfo()
  {
    short objectNid = (short) 0x1234;
    ModuleInfoType info = ModuleInfoType.HW_VERSION;
    final byte[] expected = DCCUtils.toByteArray(4,
                                              0,
                                              0,
                                              0,
                                              8,
                                              32,
                                              DCCUtils.byte1(nid),
                                              DCCUtils.byte2(nid),
                                              DCCUtils.byte1(objectNid),
                                              DCCUtils.byte2(objectNid),
                                              DCCUtils.byte1(info.getMagic()),
                                              DCCUtils.byte2(info.getMagic()));
    DefaultPacketBuilder builder = new DefaultPacketBuilder(nid);
    Packet packet = builder.buildModuleInfoPacket(objectNid,
                                                  info);
    ByteBuffer buffer = ByteBuffer.allocate(expected.length);
    assertNotNull(packet);
    assertEquals(expected.length,
                 UDPMarshaller.getRequiredBufferSize(packet));
    int bytesWritten = UDPMarshaller.marshalPacket(packet,
                                                   buffer);
    assertEquals(expected.length,
                 bytesWritten);
    assertEquals(expected,
                 buffer.array());

    info = ModuleInfoType.SW_BUILD_DATE;
    expected[expected.length - 2] = DCCUtils.byte1(info.getMagic());
    expected[expected.length - 1] = DCCUtils.byte2(info.getMagic());
    builder = new DefaultPacketBuilder(nid);
    packet = builder.buildModuleInfoPacket(objectNid,
                                           info);
    buffer = ByteBuffer.allocate(expected.length);
    assertNotNull(packet);
    assertEquals(expected.length,
                 UDPMarshaller.getRequiredBufferSize(packet));
    bytesWritten = UDPMarshaller.marshalPacket(packet,
                                               buffer);
    assertEquals(expected.length,
                 bytesWritten);
    assertEquals(expected,
                 buffer.array());

    info = ModuleInfoType.SW_BUILD_TIME;
    expected[expected.length - 2] = DCCUtils.byte1(info.getMagic());
    expected[expected.length - 1] = DCCUtils.byte2(info.getMagic());
    builder = new DefaultPacketBuilder(nid);
    packet = builder.buildModuleInfoPacket(objectNid,
                                           info);
    buffer = ByteBuffer.allocate(expected.length);
    assertNotNull(packet);
    assertEquals(expected.length,
                 UDPMarshaller.getRequiredBufferSize(packet));
    bytesWritten = UDPMarshaller.marshalPacket(packet,
                                               buffer);
    assertEquals(expected.length,
                 bytesWritten);
    assertEquals(expected,
                 buffer.array());

    info = ModuleInfoType.SW_VERSION;
    expected[expected.length - 2] = DCCUtils.byte1(info.getMagic());
    expected[expected.length - 1] = DCCUtils.byte2(info.getMagic());
    builder = new DefaultPacketBuilder(nid);
    packet = builder.buildModuleInfoPacket(objectNid,
                                           info);
    buffer = ByteBuffer.allocate(expected.length);
    assertNotNull(packet);
    assertEquals(expected.length,
                 UDPMarshaller.getRequiredBufferSize(packet));
    bytesWritten = UDPMarshaller.marshalPacket(packet,
                                               buffer);
    assertEquals(expected.length,
                 bytesWritten);
    assertEquals(expected,
                 buffer.array());

    info = ModuleInfoType.valueOf(0xbabe);
    expected[expected.length - 2] = DCCUtils.byte1(info.getMagic());
    expected[expected.length - 1] = DCCUtils.byte2(info.getMagic());
    builder = new DefaultPacketBuilder(nid);
    packet = builder.buildModuleInfoPacket(objectNid,
                                           info);
    buffer = ByteBuffer.allocate(expected.length);
    assertNotNull(packet);
    assertEquals(expected.length,
                 UDPMarshaller.getRequiredBufferSize(packet));
    bytesWritten = UDPMarshaller.marshalPacket(packet,
                                               buffer);
    assertEquals(expected.length,
                 bytesWritten);
    assertEquals(expected,
                 buffer.array());
  }

  @Test
  public void testMarshalInterfaceOption()
  {
    short objectNid = (short) 0x1234;
    InterfaceOptionType info = InterfaceOptionType.SW_PROVIDER;
    final byte[] expected = DCCUtils.toByteArray(4,
                                              0,
                                              0,
                                              0,
                                              10,
                                              40,
                                              DCCUtils.byte1(nid),
                                              DCCUtils.byte2(nid),
                                              DCCUtils.byte1(objectNid),
                                              DCCUtils.byte2(objectNid),
                                              DCCUtils.byte1(info.getMagic()),
                                              DCCUtils.byte2(info.getMagic()));
    DefaultPacketBuilder builder = new DefaultPacketBuilder(nid);
    Packet packet = builder.buildInterfaceOptionPacket(objectNid,
                                                       info);
    assertNotNull(packet);
    assertEquals(expected.length,
                 UDPMarshaller.getRequiredBufferSize(packet));
    ByteBuffer buffer = ByteBuffer.allocate(expected.length);
    int bytesWritten = UDPMarshaller.marshalPacket(packet,
                                                   buffer);
    assertEquals(expected.length,
                 bytesWritten);
    assertEquals(expected,
                 buffer.array());
  }

  @Test(enabled = false)

  public void testMarshalGetPowerMode()
  {
    short objectNid = (short) 0x1234;
    Set<PowerPort> outputs = EnumSet.of(PowerPort.OUT_1,
                                        PowerPort.OUT_2,
                                        PowerPort.BOOSTER);
    final byte[] expected = DCCUtils.toByteArray(3,
                                              0,
                                              0,
                                              0,
                                              0, // 4
                                              0,
                                              DCCUtils.byte1(nid),
                                              DCCUtils.byte2(nid),
                                              DCCUtils.byte1(objectNid),
                                              DCCUtils.byte2(objectNid),
                                              0x83);
    DefaultPacketBuilder builder = new DefaultPacketBuilder(nid);
    Packet packet = builder.buildGetPowerModePacket(objectNid,
                                                    outputs);

    assertNotNull(packet);
    assertEquals(expected.length,
                 UDPMarshaller.getRequiredBufferSize(packet));
    ByteBuffer buffer = ByteBuffer.allocate(expected.length);
    int bytesWritten = UDPMarshaller.marshalPacket(packet,
                                                   buffer);
    assertEquals(expected.length,
                 bytesWritten);
    assertEquals(expected,
                 buffer.array());
  }

  @Test
  public void testMarshalGetLocoState()
  {
    short locoNid = ZCANFactory.LOCO_MAX - 2;
    final byte[] expected = DCCUtils.toByteArray(2,
                                              0,
                                              0,
                                              0,
                                              2,
                                              0x0,
                                              DCCUtils.byte1(nid),
                                              DCCUtils.byte2(nid),
                                              DCCUtils.byte1(locoNid),
                                              DCCUtils.byte2(locoNid));
    DefaultPacketBuilder builder = new DefaultPacketBuilder(nid);
    Packet packet = builder.buildLocoStatePacket(locoNid);
    assertNotNull(packet);
    assertEquals(expected.length,
                 UDPMarshaller.getRequiredBufferSize(packet));
    ByteBuffer buffer = ByteBuffer.allocate(expected.length);
    int bytesWritten = UDPMarshaller.marshalPacket(packet,
                                                   buffer);
    assertEquals(expected.length,
                 bytesWritten);
    assertEquals(expected,
                 buffer.array());
  }

  @Test
  public void testMarshalGetLocoMode()
  {
    short locoNid = ZCANFactory.LOCO_MAX - 1;
    byte[] expected = DCCUtils.toByteArray(2,
                                        0,
                                        0,
                                        0,
                                        2,
                                        4,
                                        DCCUtils.byte1(nid),
                                        DCCUtils.byte2(nid),
                                        DCCUtils.byte1(locoNid),
                                        DCCUtils.byte2(locoNid));
    DefaultPacketBuilder builder = new DefaultPacketBuilder(nid);
    Packet packet = builder.buildLocoModePacket(locoNid);
    assertNotNull(packet);
    assertEquals(expected.length,
                 UDPMarshaller.getRequiredBufferSize(packet));
    ByteBuffer buffer = ByteBuffer.allocate(expected.length);
    int bytesWritten = UDPMarshaller.marshalPacket(packet,
                                                   buffer);
    assertEquals(expected.length,
                 bytesWritten);
    assertEquals(expected,
                 buffer.array());
  }

  @Test
  public void testMarshalSetLocoMode()
  {
    short locoNid = ZCANFactory.LOCO_MAX;
    SpeedSteps steps = SpeedSteps.STEP_128;
    Protocol prot = Protocol.MFX;
    SpeedlimitMode limit = SpeedlimitMode.ZIMO;
    int numFunc = 4;
    byte[] expected = DCCUtils.toByteArray(6,
                                        0,
                                        0,
                                        0,
                                        2,
                                        5,
                                        DCCUtils.byte1(nid),
                                        DCCUtils.byte2(nid),
                                        DCCUtils.byte1(locoNid),
                                        DCCUtils.byte2(locoNid),
                                        0x44,
                                        4,
                                        0x0b,
                                        0);
    DefaultPacketBuilder builder = new DefaultPacketBuilder(nid);
    Packet packet = builder.buildLocoModePacket(locoNid,
                                                steps,
                                                prot,
                                                numFunc,
                                                limit,
                                                true,
                                                true).build();
    assertNotNull(packet);
    assertEquals(expected.length,
                 UDPMarshaller.getRequiredBufferSize(packet));
    ByteBuffer buffer = ByteBuffer.allocate(expected.length);
    int bytesWritten = UDPMarshaller.marshalPacket(packet,
                                                   buffer);
    assertEquals(expected.length,
                 bytesWritten);
    assertEquals(expected,
                 buffer.array());
  }

  @Test
  public void testMarshalGetLocoSpeed()
  {
    short locoNid = ZCANFactory.LOCO_MAX;
    byte[] expected = DCCUtils.toByteArray(2,
                                        0,
                                        0,
                                        0,
                                        2,
                                        8,
                                        DCCUtils.byte1(nid),
                                        DCCUtils.byte2(nid),
                                        DCCUtils.byte1(locoNid),
                                        DCCUtils.byte2(locoNid));
    DefaultPacketBuilder builder = new DefaultPacketBuilder(nid);
    Packet packet = builder.buildLocoSpeedPacket(locoNid);
    assertNotNull(packet);
    assertEquals(expected.length,
                 UDPMarshaller.getRequiredBufferSize(packet));
    ByteBuffer buffer = ByteBuffer.allocate(expected.length);
    int bytesWritten = UDPMarshaller.marshalPacket(packet,
                                                   buffer);
    assertEquals(expected.length,
                 bytesWritten);
    assertEquals(expected,
                 buffer.array());
  }

  @Test
  public void testMarshalSetLocoSpeed()
  {
    short locoNid = 0x123;
    short speed = 0x100;
    EnumSet<SpeedFlags> flags = EnumSet.of(SpeedFlags.REVERSE_FROM_SYSTEM,
                                           SpeedFlags.FORWARD_TO_SYSTEM,
                                           SpeedFlags.EMERGENCY_STOP);
    int sf = speed + SpeedFlags.maskOfSet(flags);
    byte div = 2;
    byte[] expected = DCCUtils.toByteArray(6,
                                        0,
                                        0,
                                        0,
                                        2,
                                        9,
                                        DCCUtils.byte1(nid),
                                        DCCUtils.byte2(nid),
                                        DCCUtils.byte1(locoNid),
                                        DCCUtils.byte2(locoNid),
                                        DCCUtils.byte1(sf),
                                        DCCUtils.byte2(sf),
                                        div,
                                        0);
    DefaultPacketBuilder builder = new DefaultPacketBuilder(nid);
    Packet packet = builder.buildLocoSpeedPacket(locoNid,
                                                 speed,
                                                 flags,
                                                 div);
    assertNotNull(packet);
    assertEquals(expected.length,
                 UDPMarshaller.getRequiredBufferSize(packet));
    ByteBuffer buffer = ByteBuffer.allocate(expected.length);
    int bytesWritten = UDPMarshaller.marshalPacket(packet,
                                                   buffer);
    assertEquals(expected.length,
                 bytesWritten);
    assertEquals(expected,
                 buffer.array());
  }

  @Test
  public void testMarshalGetLocoFuncInfo()
  {
    short locoNid = 0x123;
    byte[] expected = DCCUtils.toByteArray(2,
                                        0,
                                        0,
                                        0,
                                        2,
                                        0xc,
                                        DCCUtils.byte1(nid),
                                        DCCUtils.byte2(nid),
                                        DCCUtils.byte1(locoNid),
                                        DCCUtils.byte2(locoNid));
    DefaultPacketBuilder builder = new DefaultPacketBuilder(nid);
    Packet packet = builder.buildLocoFunctionInfoPacket(locoNid);
    assertNotNull(packet);
    assertEquals(expected.length,
                 UDPMarshaller.getRequiredBufferSize(packet));
    ByteBuffer buffer = ByteBuffer.allocate(expected.length);
    int bytesWritten = UDPMarshaller.marshalPacket(packet,
                                                   buffer);
    assertEquals(expected.length,
                 bytesWritten);
    assertEquals(expected,
                 buffer.array());
  }

  @Test
  public void testMarshalGetLocoFunc()
  {
    short locoNid = 0x123;
    byte[] expected = DCCUtils.toByteArray(2,
                                        0,
                                        0,
                                        0,
                                        2,
                                        0x10,
                                        DCCUtils.byte1(nid),
                                        DCCUtils.byte2(nid),
                                        DCCUtils.byte1(locoNid),
                                        DCCUtils.byte2(locoNid));
    DefaultPacketBuilder builder = new DefaultPacketBuilder(nid);
    Packet packet = builder.buildLocoFunctionPacket(locoNid);
    assertNotNull(packet);
    assertEquals(expected.length,
                 UDPMarshaller.getRequiredBufferSize(packet));
    ByteBuffer buffer = ByteBuffer.allocate(expected.length);
    int bytesWritten = UDPMarshaller.marshalPacket(packet,
                                                   buffer);
    assertEquals(expected.length,
                 bytesWritten);
    assertEquals(expected,
                 buffer.array());

  }

  @Test
  public void testMarshalSetLocoFunc()
  {
    short locoNid = 0x123;
    short fxNr = 254;
    short fxVal = (short) 0x89ab;
    byte[] expected = DCCUtils.toByteArray(6,
                                        0,
                                        0,
                                        0,
                                        2,
                                        0x11,
                                        DCCUtils.byte1(nid),
                                        DCCUtils.byte2(nid),
                                        DCCUtils.byte1(locoNid),
                                        DCCUtils.byte2(locoNid),
                                        DCCUtils.byte1(fxNr),
                                        DCCUtils.byte2(fxNr),
                                        DCCUtils.byte1(fxVal),
                                        DCCUtils.byte2(fxVal));
    DefaultPacketBuilder builder = new DefaultPacketBuilder(nid);
    Packet packet = builder.buildLocoFunctionPacket(locoNid,
                                                    fxNr,
                                                    fxVal);
    assertNotNull(packet);
    assertEquals(expected.length,
                 UDPMarshaller.getRequiredBufferSize(packet));
    ByteBuffer buffer = ByteBuffer.allocate(expected.length);
    int bytesWritten = UDPMarshaller.marshalPacket(packet,
                                                   buffer);
    assertEquals(expected.length,
                 bytesWritten);
    assertEquals(expected,
                 buffer.array());

  }

  @Test
  public void testMarshalGetCV()
  {
    short locoNid = 0x123;
    short systemNid = (short) 0xbabe;
    int cvNum = 232860;
    byte[] expected = DCCUtils.toByteArray(8,
                                        0,
                                        0,
                                        0,
                                        0x06,
                                        0x21,
                                        DCCUtils.byte1(nid),
                                        DCCUtils.byte2(nid),
                                        DCCUtils.byte1(systemNid),
                                        DCCUtils.byte2(systemNid),
                                        DCCUtils.byte1(locoNid),
                                        DCCUtils.byte2(locoNid),
                                        DCCUtils.byte1(cvNum),
                                        DCCUtils.byte2(cvNum),
                                        DCCUtils.byte3(cvNum),
                                        DCCUtils.byte4(cvNum));
    DefaultPacketBuilder builder = new DefaultPacketBuilder(nid);
    Packet packet = builder.buildReadCVPacket(systemNid,
                                              locoNid,
                                              cvNum);
    assertNotNull(packet);
    assertEquals(expected.length,
                 UDPMarshaller.getRequiredBufferSize(packet));
    ByteBuffer buffer = ByteBuffer.allocate(expected.length);
    int bytesWritten = UDPMarshaller.marshalPacket(packet,
                                                   buffer);
    assertEquals(expected.length,
                 bytesWritten);
    assertEquals(expected,
                 buffer.array());
  }

  @Test
  public void testMarshalSetCV()
  {
    short locoNid = 0x123;
    short systemNid = (short) 0xbabe;
    int cvNum = 232860;
    short value = (short) 0xcafe;
    byte[] expected = DCCUtils.toByteArray(10,
                                        0,
                                        0,
                                        0,
                                        0x06,
                                        0x25,
                                        DCCUtils.byte1(nid),
                                        DCCUtils.byte2(nid),
                                        DCCUtils.byte1(systemNid),
                                        DCCUtils.byte2(systemNid),
                                        DCCUtils.byte1(locoNid),
                                        DCCUtils.byte2(locoNid),
                                        DCCUtils.byte1(cvNum),
                                        DCCUtils.byte2(cvNum),
                                        DCCUtils.byte3(cvNum),
                                        DCCUtils.byte4(cvNum),
                                        DCCUtils.byte1(value),
                                        DCCUtils.byte2(value));
    DefaultPacketBuilder builder = new DefaultPacketBuilder(nid);
    Packet packet = builder.buildWriteCVPacket(systemNid,
                                               locoNid,
                                               cvNum,
                                               value);
    assertNotNull(packet);
    assertEquals(expected.length,
                 UDPMarshaller.getRequiredBufferSize(packet));
    ByteBuffer buffer = ByteBuffer.allocate(expected.length);
    int bytesWritten = UDPMarshaller.marshalPacket(packet,
                                                   buffer);
    assertEquals(expected.length,
                 bytesWritten);
    assertEquals(expected,
                 buffer.array());
  }

}
