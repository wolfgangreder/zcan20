/*
 * Copyright 2019 Wolfgang Reder.
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

import at.or.reder.zcan20.CanId;
import at.or.reder.zcan20.ZCANFactory;
import at.or.reder.zcan20.packet.Packet;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import javax.validation.constraints.NotNull;

/**
 *
 * @author Wolfgang Reder
 */
public class McastMarshaller
{

  public static final int PREFIX_LEN = 4 + 8 + 1 + 1 + 4; // nanos + seconds + intf + flags + canid

  public static int getRequiredBufferSize(@NotNull Packet packet)
  {
    return PREFIX_LEN + packet.getData().remaining();
  }

  public static int marshalPacket(@NotNull Packet packet,
                                  LocalDateTime ts,
                                  int intf,
                                  @NotNull ByteBuffer bufferToFill)
  {
    Instant instant = ts.toInstant(ZoneOffset.UTC);
    ByteBuffer buffer = bufferToFill.duplicate();
    ByteBuffer data = packet.getData();
    int dlc = data.remaining();
    buffer.limit(PREFIX_LEN + dlc);
    buffer.order(ByteOrder.LITTLE_ENDIAN);
    buffer.putInt(instant.getNano());
    buffer.putLong(instant.getEpochSecond());
    buffer.put((byte) intf);
    buffer.put((byte) 1); // extended packet
    buffer.putInt(packet.getCanId().intValue());
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
    final int nanos = packetBytes.getInt();
    final long senconds = packetBytes.getLong();
    final int intf = packetBytes.get() & 0xff;
    final int flags = packetBytes.get() & 0xff;
    final int a = packetBytes.getInt();
    final CanId canId = CanId.valueOf(a);
    return ZCANFactory.createPacketBuilder(canId.getSenderNid()).
            senderNID(canId.getSenderNid()).
            command(canId.getCommand()).
            commandGroup(canId.getCommandGroup()).
            commandMode(canId.getCommandMode()).
            data(packetBytes).
            build();

  }

}
