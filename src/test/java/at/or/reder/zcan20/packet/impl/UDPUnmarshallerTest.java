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

import at.or.reder.zcan20.CVReadState;
import at.or.reder.zcan20.CommandGroup;
import at.or.reder.zcan20.CommandMode;
import at.or.reder.zcan20.impl.UDPMarshaller;
import at.or.reder.zcan20.packet.CVInfoAdapter;
import at.or.reder.zcan20.packet.Packet;
import at.or.reder.dcc.util.DCCUtils;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.ParseException;
import static org.testng.AssertJUnit.*;
import org.testng.annotations.Test;

/**
 *
 * @author Wolfgang Reder
 */
public class UDPUnmarshallerTest
{

  @Test(enabled = false)

  public void testUnmarshalReadCV1118_29() throws IOException
  {
    byte[] data = DCCUtils.toByteArray(0x0a,
                                    0,
                                    0x16,
                                    0,
                                    0x16,
                                    0x23,
                                    0xdd,
                                    0xc3,
                                    0xa6,
                                    0xc0,
                                    0x5e,
                                    0x04,
                                    0x1d,
                                    0x00,
                                    0x00,
                                    0x00,
                                    0x2a,
                                    0x00);
    ByteBuffer buffer = ByteBuffer.allocate(100);
    buffer.put(data);
    buffer.limit(buffer.position());
    buffer.clear();
    Packet packet = UDPMarshaller.unmarshalPacket(buffer);
    assertNotNull(packet);
    assertEquals((short) 0xc3dd,
                 packet.getSenderNID());
    assertSame(CommandGroup.TRACK_CONFIG_PUBLIC,
               packet.getCommandGroup());
    assertSame(CommandMode.ACK,
               packet.getCommandMode());
    assertEquals(CommandGroup.TSE_PROG_READ,
                 packet.getCommand());

    CVInfoAdapter info = packet.getAdapter(CVInfoAdapter.class);
    assertNotNull(info);
    assertEquals(CVReadState.READ,
                 info.getReadState());
    assertEquals((short) 1118,
                 info.getDecoderID());
    assertEquals((short) 0xc0a6,
                 info.getSystemID());
    assertEquals(29,
                 info.getNumber());
    assertEquals(42,
                 info.getValue());
  }

  @Test(enabled = false)
  public void testUnmarshalReadCV1118_03busy() throws IOException, ParseException
  {
    ByteBuffer buffer = DCCUtils.hexString2ByteBuffer("0a:00:01:00:16:0b:a6:c0:a6:c0:5e:04:03:00:00:00:00:10",
                                                   null,
                                                   ':');
    buffer.clear();
    Packet packet = UDPMarshaller.unmarshalPacket(buffer);
    assertNotNull(packet);
    assertEquals((short) 0xc0a6,
                 packet.getSenderNID());
    assertSame(CommandGroup.TRACK_CONFIG_PUBLIC,
               packet.getCommandGroup());
    assertSame(CommandMode.ACK,
               packet.getCommandMode());
    assertEquals(CommandGroup.TSE_PROG_BUSY,
                 packet.getCommand());

    CVInfoAdapter info = packet.getAdapter(CVInfoAdapter.class);
    assertNotNull(info);
    assertEquals(CVReadState.BUSY,
                 info.getReadState());
    assertEquals((short) 1118,
                 info.getDecoderID());
    assertEquals((short) 0xc0a6,
                 info.getSystemID());
    assertEquals(3,
                 info.getNumber());
    assertEquals(0x1000,
                 info.getValue());
  }

}
