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

import at.or.reder.zcan20.CommandGroup;
import at.or.reder.zcan20.CommandMode;
import at.or.reder.zcan20.ZCANFactory;
import at.or.reder.zcan20.packet.Packet;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import javax.validation.constraints.NotNull;

/**
 *
 * @author Wolfgang Reder
 */
public final class UDPMarshaller
{

  public static final int PREFIX_LEN = 8;

  /**
   * Returns the required buffer size to hold the packet.
   *
   * @param packet Packet to check the size
   * @return required buffersize
   */
  public static int getRequiredBufferSize(@NotNull Packet packet)
  {
    return PREFIX_LEN + packet.getData().remaining();
  }

  public static int marshalPacket(@NotNull Packet packet,
                                  @NotNull ByteBuffer bufferToFill)
  {
    ByteBuffer buffer = bufferToFill.duplicate();
    ByteBuffer data = packet.getData();
    int dlc = data.remaining();
    buffer.limit(PREFIX_LEN + dlc);
    buffer.order(ByteOrder.LITTLE_ENDIAN);
    buffer.putShort((short) dlc);
    buffer.putShort((short) 0);
    buffer.put(packet.getCommandGroup().getMagic());
    int cmd = (packet.getCommand() & 0x3f) << 2;
    int mode = packet.getCommandMode().getMagic() & 0x3;
    buffer.put((byte) (cmd + mode));
    buffer.putShort(packet.getSenderNID());
    buffer.put(data);
    return buffer.position();
  }

  public static Packet unmarshalPacket(@NotNull ByteBuffer buffer) throws IOException
  {
    ByteBuffer packetBytes = buffer.duplicate();
    if (packetBytes.remaining() < PREFIX_LEN) {
      throw new IOException("Received ZCAN Packet too small");
    }
    packetBytes.order(ByteOrder.LITTLE_ENDIAN);
    final int dlc = packetBytes.getShort() & 0xffff;
    if (packetBytes.remaining() < (PREFIX_LEN + dlc - 2)) {
      throw new IOException("Received ZCAN Packet too small");
    }
    packetBytes.getShort();// these 16bits are currently (4.10public) not used
    final CommandGroup group = CommandGroup.valueOf(packetBytes.get());
    final byte mcmd = packetBytes.get();
    final CommandMode mode = CommandMode.valueOfMagic(mcmd);
    final byte command = (byte) ((mcmd >> 2) & 0x3f);
    final short senderNID = packetBytes.getShort();
    return ZCANFactory.createPacketBuilder(senderNID).
            senderNID(senderNID).
            command(command).
            commandGroup(group).
            commandMode(mode).
            data(packetBytes).
            build();

  }

}
