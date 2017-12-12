/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.reder.zcan20.impl;

/**
 *
 * @author reder
 */
public class VCOMMarshaller
{
//
//  public static int marshalPacket(@NotNull Packet packet,
//                                  @NotNull ByteBuffer bufferToFill)
//  {
//    ByteBuffer buffer = bufferToFill.duplicate();
//    ByteBuffer data = packet.getData().duplicate();
//    int dlc = data.remaining();
//    buffer.limit(8 + dlc);
//    buffer.order(ByteOrder.LITTLE_ENDIAN);
//    buffer.put((byte) 0x5a);
//    buffer.put((byte) 0x32);
//    buffer.put((byte) dlc);
//    buffer.put(packet.getCommandGroup().getMagic());
//    int cmd = (packet.getCommand() & 0x3f) << 2;
//    int mode = packet.getCommandMode().getMagic() & 0x3;
//    buffer.put((byte) (cmd + mode));
//    buffer.put(data);
//    buffer.put((byte) 0x32);
//    buffer.put((byte) 0x5a);
//    return buffer.position();
//  }
//
//  public static Packet unmarshalPacket(@NotNull ByteBuffer buffer) throws IOException
//  {
//    ByteBuffer packetBytes = buffer.duplicate();
//    if (packetBytes.limit() < 8) {
//      throw new IOException("Received ZCAN Packet too small");
//    }
//    packetBytes.order(ByteOrder.LITTLE_ENDIAN);
//    final int dlc = packetBytes.get() & 0xff;
//    if (packetBytes.limit() < (8 + dlc)) {
//      throw new IOException("Received ZCAN Packet too small");
//    }
//    final CommandGroup group = CommandGroup.valueOf(packetBytes.get() & 0xff);
//    final int mcmd = packetBytes.get() & 0xff;
//    final CommandMode mode = CommandMode.valueOfMagic(mcmd);
//    final int command = mcmd >> 2;
//    final int address = packetBytes.getShort() & 0xffff;
//    final byte[] data = new byte[dlc];
//    packetBytes.get(data);
//    return new DefaultPacket.Builder().
//            address(address).
//            command(command).
//            commandGroup(group).
//            commandMode(mode).
//            data(data).
//            build();
//
//  }

}
