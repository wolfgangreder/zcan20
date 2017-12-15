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

import com.reder.zcan20.DataGroup;
import com.reder.zcan20.InterfaceOptionType;
import com.reder.zcan20.ModuleInfoType;
import com.reder.zcan20.PowerOutput;
import com.reder.zcan20.Protocol;
import com.reder.zcan20.SpeedFlags;
import com.reder.zcan20.SpeedSteps;
import com.reder.zcan20.SpeedlimitMode;
import com.reder.zcan20.ZCANFactory;
import com.reder.zcan20.impl.UDPMarshaller;
import com.reder.zcan20.packet.Packet;
import com.reder.zcan20.util.Utils;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
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
    ByteBuffer buffer = Utils.allocateLEBuffer(10);
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
    assertEquals(Utils.byte1(nid),
                 buffer.get());
    assertEquals(Utils.byte2(nid),
                 buffer.get());
    assertEquals(Utils.byte1(masterNid),
                 buffer.get());
    assertEquals(Utils.byte2(masterNid),
                 buffer.get());
  }

  @Test
  public void testMarshalDataGroupCount()
  {
    final DataGroup grp = DataGroup.MX9;
    byte[] expected = Utils.toByteArray(4,
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
    final byte[] expected = Utils.toByteArray(6,
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
    final byte[] expected = Utils.toByteArray(4,
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
    final byte[] expected = Utils.toByteArray(16,
                                              0,
                                              0,
                                              0,
                                              7,
                                              33 << 2,
                                              Utils.byte1(nid),
                                              Utils.byte2(nid),
                                              Utils.byte1(masterNid),
                                              Utils.byte2(masterNid),
                                              Utils.byte1(objectNID),
                                              Utils.byte2(objectNID),
                                              Utils.byte1(subID),
                                              Utils.byte2(subID),
                                              Utils.byte3(subID),
                                              Utils.byte4(subID),
                                              Utils.byte1(val1),
                                              Utils.byte2(val1),
                                              Utils.byte3(val1),
                                              Utils.byte4(val1),
                                              Utils.byte1(val2),
                                              Utils.byte2(val2),
                                              Utils.byte3(val2),
                                              Utils.byte4(val2));
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

  @Test
  public void testMarshalModulePowerInfo()
  {
    PowerOutput out = PowerOutput.OUT_1;
    DefaultPacketBuilder builder = new DefaultPacketBuilder(nid);
    final byte[] expected = Utils.toByteArray(3,
                                              0,
                                              0,
                                              0,
                                              8,
                                              0,
                                              Utils.byte1(nid),
                                              Utils.byte2(nid),
                                              Utils.byte1(masterNid),
                                              Utils.byte2(masterNid),
                                              0);
    Packet packet = builder.buildModulePowerInfoPacket(masterNid,
                                                       out);
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
    out = PowerOutput.OUT_2;
    expected[expected.length - 1] = 1;
    packet = builder.buildModulePowerInfoPacket(masterNid,
                                                out);
    assertNotNull(packet);
    buffer = ByteBuffer.allocate(expected.length);
    bytesWritten = UDPMarshaller.marshalPacket(packet,
                                               buffer);
    assertEquals(expected.length,
                 bytesWritten);
    assertEquals(expected,
                 buffer.array());
    out = PowerOutput.BOOSTER;
    expected[expected.length - 1] = 2;
    packet = builder.buildModulePowerInfoPacket(masterNid,
                                                out);
    assertNotNull(packet);
    buffer = ByteBuffer.allocate(expected.length);
    bytesWritten = UDPMarshaller.marshalPacket(packet,
                                               buffer);
    assertEquals(expected.length,
                 bytesWritten);
    assertEquals(expected,
                 buffer.array());
  }

  @Test
  public void testMarshalModuleInfo()
  {
    short objectNid = (short) 0x1234;
    ModuleInfoType info = ModuleInfoType.HW_VERSION;
    final byte[] expected = Utils.toByteArray(4,
                                              0,
                                              0,
                                              0,
                                              8,
                                              32,
                                              Utils.byte1(nid),
                                              Utils.byte2(nid),
                                              Utils.byte1(objectNid),
                                              Utils.byte2(objectNid),
                                              Utils.byte1(info.getMagic()),
                                              Utils.byte2(info.getMagic()));
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
    expected[expected.length - 2] = Utils.byte1(info.getMagic());
    expected[expected.length - 1] = Utils.byte2(info.getMagic());
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
    expected[expected.length - 2] = Utils.byte1(info.getMagic());
    expected[expected.length - 1] = Utils.byte2(info.getMagic());
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
    expected[expected.length - 2] = Utils.byte1(info.getMagic());
    expected[expected.length - 1] = Utils.byte2(info.getMagic());
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
    expected[expected.length - 2] = Utils.byte1(info.getMagic());
    expected[expected.length - 1] = Utils.byte2(info.getMagic());
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
    final byte[] expected = Utils.toByteArray(4,
                                              0,
                                              0,
                                              0,
                                              10,
                                              40,
                                              Utils.byte1(nid),
                                              Utils.byte2(nid),
                                              Utils.byte1(objectNid),
                                              Utils.byte2(objectNid),
                                              Utils.byte1(info.getMagic()),
                                              Utils.byte2(info.getMagic()));
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

  @Test
  public void testMarshalGetPowerMode()
  {
    short objectNid = (short) 0x1234;
    Set<PowerOutput> outputs = EnumSet.of(PowerOutput.OUT_1,
                                          PowerOutput.OUT_2,
                                          PowerOutput.BOOSTER);
    final byte[] expected = Utils.toByteArray(3,
                                              0,
                                              0,
                                              0,
                                              0, // 4
                                              0,
                                              Utils.byte1(nid),
                                              Utils.byte2(nid),
                                              Utils.byte1(objectNid),
                                              Utils.byte2(objectNid),
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
    final byte[] expected = Utils.toByteArray(2,
                                              0,
                                              0,
                                              0,
                                              2,
                                              0x0,
                                              Utils.byte1(nid),
                                              Utils.byte2(nid),
                                              Utils.byte1(locoNid),
                                              Utils.byte2(locoNid));
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
    byte[] expected = Utils.toByteArray(2,
                                        0,
                                        0,
                                        0,
                                        2,
                                        4,
                                        Utils.byte1(nid),
                                        Utils.byte2(nid),
                                        Utils.byte1(locoNid),
                                        Utils.byte2(locoNid));
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
    byte[] expected = Utils.toByteArray(5,
                                        0,
                                        0,
                                        0,
                                        2,
                                        5,
                                        Utils.byte1(nid),
                                        Utils.byte2(nid),
                                        Utils.byte1(locoNid),
                                        Utils.byte2(locoNid),
                                        0x44,
                                        4,
                                        0x0b);
    DefaultPacketBuilder builder = new DefaultPacketBuilder(nid);
    Packet packet = builder.buildLocoModePacket(locoNid,
                                                steps,
                                                prot,
                                                numFunc,
                                                limit,
                                                true,
                                                true);
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
    byte[] expected = Utils.toByteArray(2,
                                        0,
                                        0,
                                        0,
                                        2,
                                        8,
                                        Utils.byte1(nid),
                                        Utils.byte2(nid),
                                        Utils.byte1(locoNid),
                                        Utils.byte2(locoNid));
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
    byte[] expected = Utils.toByteArray(6,
                                        0,
                                        0,
                                        0,
                                        2,
                                        9,
                                        Utils.byte1(nid),
                                        Utils.byte2(nid),
                                        Utils.byte1(locoNid),
                                        Utils.byte2(locoNid),
                                        Utils.byte1(sf),
                                        Utils.byte2(sf),
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
    byte[] expected = Utils.toByteArray(2,
                                        0,
                                        0,
                                        0,
                                        2,
                                        0xc,
                                        Utils.byte1(nid),
                                        Utils.byte2(nid),
                                        Utils.byte1(locoNid),
                                        Utils.byte2(locoNid));
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
    byte[] expected = Utils.toByteArray(2,
                                        0,
                                        0,
                                        0,
                                        2,
                                        0x10,
                                        Utils.byte1(nid),
                                        Utils.byte2(nid),
                                        Utils.byte1(locoNid),
                                        Utils.byte2(locoNid));
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
    byte[] expected = Utils.toByteArray(6,
                                        0,
                                        0,
                                        0,
                                        2,
                                        0x11,
                                        Utils.byte1(nid),
                                        Utils.byte2(nid),
                                        Utils.byte1(locoNid),
                                        Utils.byte2(locoNid),
                                        Utils.byte1(fxNr),
                                        Utils.byte2(fxNr),
                                        Utils.byte1(fxVal),
                                        Utils.byte2(fxVal));
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
    byte[] expected = Utils.toByteArray(8,
                                        0,
                                        0,
                                        0,
                                        0x16,
                                        0x21,
                                        Utils.byte1(nid),
                                        Utils.byte2(nid),
                                        Utils.byte1(systemNid),
                                        Utils.byte2(systemNid),
                                        Utils.byte1(locoNid),
                                        Utils.byte2(locoNid),
                                        Utils.byte1(cvNum),
                                        Utils.byte2(cvNum),
                                        Utils.byte3(cvNum),
                                        Utils.byte4(cvNum));
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
    byte[] expected = Utils.toByteArray(10,
                                        0,
                                        0,
                                        0,
                                        0x16,
                                        0x25,
                                        Utils.byte1(nid),
                                        Utils.byte2(nid),
                                        Utils.byte1(systemNid),
                                        Utils.byte2(systemNid),
                                        Utils.byte1(locoNid),
                                        Utils.byte2(locoNid),
                                        Utils.byte1(cvNum),
                                        Utils.byte2(cvNum),
                                        Utils.byte3(cvNum),
                                        Utils.byte4(cvNum),
                                        Utils.byte1(value),
                                        Utils.byte2(value));
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
