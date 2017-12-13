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
import com.reder.zcan20.ZCANFactory;
import com.reder.zcan20.impl.UDPMarshaller;
import com.reder.zcan20.packet.Packet;
import com.reder.zcan20.util.Utils;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
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

  public static byte[] toByteArray(int... in)
  {
    byte[] result = new byte[in.length];
    for (int i = 0; i < in.length; i++) {
      result[i] = (byte) in[i];
    }
    return result;
  }

  @Test
  public void testMarshalPortOpen()
  {
    Packet packet = ZCANFactory.createPacketBuilder(nid).buildLoginPacket();
    assertNotNull(packet);
    ByteBuffer buffer = ByteBuffer.allocate(10);
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
    byte[] expected = toByteArray(4,
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
    ByteBuffer buffer = ByteBuffer.allocate(expected.length);
    int bytesWritten = UDPMarshaller.marshalPacket(packet,
                                                   buffer);
    assertEquals(bytesWritten,
                 expected.length);
    assertEquals(expected,
                 buffer.array());
  }

  @Test
  public void testMarshalDataPacketIndex()
  {
    final DataGroup grp = DataGroup.MX9;
    final short index = (short) 0x1234;
    final byte[] expected = toByteArray(6,
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
    ByteBuffer buffer = ByteBuffer.allocate(expected.length);
    int bytesWritten = UDPMarshaller.marshalPacket(packet,
                                                   buffer);
    assertEquals(bytesWritten,
                 expected.length);
    assertEquals(expected,
                 buffer.array());
  }

}
