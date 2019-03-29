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
package at.or.reder.zcan20.impl;

/**
 *
 * @author Wolfgang Reder
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
