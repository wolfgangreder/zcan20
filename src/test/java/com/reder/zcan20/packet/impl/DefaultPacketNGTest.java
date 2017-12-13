/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.reder.zcan20.packet.impl;

import org.testng.annotations.Test;

/**
 *
 * @author Wolfgang Reder
 */
public class DefaultPacketNGTest
{

  public DefaultPacketNGTest()
  {
  }

  private static byte[] toByteArray(int... a)
  {
    byte[] result = new byte[a.length];
    for (int i = 0; i < a.length; ++i) {
      result[i] = (byte) a[i];
    }
    return result;
  }

  @Test
  public void testReadCVResponse()
  {
//    CommandGroup group = CommandGroup.TRACK_CONFIG_PUBLIC;
//    CommandMode mode = CommandMode.ACK;
//    byte command = CommandGroup.TSE_PROG_READ;
//    short address = (short) 0xc0a6;
//    byte[] data = toByteArray(0xa6,
//                              0xc0,
//                              0x9f,
//                              0x02,
//                              0x1d,
//                              0x00,
//                              0x00,
//                              0x00,
//                              0x2e,
//                              0x00);
//    DefaultPacket.Builder builder = new DefaultPacket.Builder().
//            senderNID(address).
//            command(command).
//            commandGroup(group).
//            commandMode(mode).
//            data(data);
//    Packet packet = builder.build();
//    CVInfoAdapter info = packet.getAdapter(CVInfoAdapter.class);
//    assertNotNull(info);
//    assertEquals((short) 0xc0a6,
//                 packet.getSenderNID());
//    assertEquals(12,
//                 packet.getData().capacity());
//    assertEquals(671,
//                 info.getDecoderAddress());
//    assertEquals(29,
//                 info.getNumber());
//    assertEquals(46,
//                 info.getValue());
  }

  @Test
  public void testTakeOwnerShip()
  {
//    DefaultPacket.Builder builder = new DefaultPacket.Builder();
//    Packet packet = builder.buildTakeOwnership((short) 0xc221,
//                                               (short) 246);
//    byte[] expected = toByteArray(4,
//                                  0,
//                                  0,
//                                  0,
//                                  2,
//                                  0x41,
//                                  0xf6,
//                                  0x00,
//                                  0x10,
//                                  0x00);
//    ByteBuffer buffer = ByteBuffer.allocate(expected.length);
//    UDPMarshaller.marshalPacket(packet,
//                                buffer);
//    byte[] result = buffer.array();
//    assertEquals(expected,
//                 result);
  }

  @Test
  public void testLocoActive()
  {
//    DefaultPacket.Builder builder = new DefaultPacket.Builder();
//    builder.commandGroup(CommandGroup.LOCO);
//    builder.command(CommandGroup.LOCO_ACTIVE);
//    builder.commandMode(CommandMode.REQUEST);
//    ByteBuffer buffer = ByteBuffer.allocate(2);
//    buffer.order(ByteOrder.LITTLE_ENDIAN);
//    buffer.putShort((short) 1163);
//    builder.data(buffer.array());
//    Packet packet = builder.build();
//    byte[] expected = toByteArray(2,
//                                  0,
//                                  0,
//                                  0,
//                                  2,
//                                  0x40,
//                                  -117,
//                                  0x04);
//    buffer = ByteBuffer.allocate(expected.length);
//    UDPMarshaller.marshalPacket(packet,
//                                buffer);
//    byte[] result = buffer.array();
//    assertEquals(expected,
//                 result);
  }

}
