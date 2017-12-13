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
package com.reder.zcan20.impl;

import com.reder.zcan20.CommandGroup;
import com.reder.zcan20.CommandMode;
import com.reder.zcan20.ZCANFactory;
import com.reder.zcan20.packet.Packet;
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

  public static ByteBuffer adaptByteBuffer(@NotNull ByteBuffer bufferToFill)
  {
    ByteBuffer buffer = bufferToFill.slice();
    buffer.order(ByteOrder.LITTLE_ENDIAN);
    return buffer;
  }

  public static int marshalPacket(@NotNull Packet packet,
                                  @NotNull ByteBuffer bufferToFill)
  {
    ByteBuffer buffer = bufferToFill.duplicate();
    ByteBuffer data = packet.getData();
    int dlc = data.remaining();
    buffer.limit(8 + dlc);
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
    if (packetBytes.limit() < 8) {
      throw new IOException("Received ZCAN Packet too small");
    }
    packetBytes.order(ByteOrder.LITTLE_ENDIAN);
    final int dlc = packetBytes.getShort() & 0xffff;
    if (packetBytes.limit() < (8 + dlc)) {
      throw new IOException("Received ZCAN Packet too small");
    }
    packetBytes.getShort();// these 16bits are currently (4.10public) not used
    final CommandGroup group = CommandGroup.valueOf(packetBytes.get());
    final byte mcmd = packetBytes.get();
    final CommandMode mode = CommandMode.valueOfMagic(mcmd);
    final byte command = (byte) (mcmd >> 2);
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
