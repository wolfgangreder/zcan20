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
import javax.validation.constraints.NotNull;

/**
 *
 * @author Wolfgang Reder
 */
public class CANMarshaller
{

  public static final int PREFIX_LEN = 5; // CanID+DLC

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
    buffer.putInt(packet.getCanId().intValue());
    buffer.put((byte) (dlc & 0x0f));
    buffer.put(data);
    return buffer.position();
  }

  public static Packet unmarshalPacket(@NotNull ByteBuffer buffer) throws IOException
  {
    ByteBuffer packetBytes = buffer.duplicate();
    if (packetBytes.remaining() < PREFIX_LEN) {
      throw new IOException("Received ZCAN Packet too small");
    }
    final int i = packetBytes.getInt();
    CanId canId = CanId.valueOf(i);
    final int dlc = packetBytes.get() & 0xff;
    if (packetBytes.remaining() < dlc) {
      throw new IOException("Received ZCAN Packet too small");
    }
    return ZCANFactory.createPacketBuilder(canId).
            data(packetBytes).
            build();

  }

}
