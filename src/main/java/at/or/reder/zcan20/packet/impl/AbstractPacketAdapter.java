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

import at.or.reder.zcan20.packet.Packet;
import at.or.reder.zcan20.packet.PacketAdapter;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Objects;

/**
 *
 * @author Wolfgang Reder
 */
public abstract class AbstractPacketAdapter implements PacketAdapter
{

  private final Packet packet;
  protected final ByteBuffer buffer;

  protected AbstractPacketAdapter(Packet packet)
  {
    this.packet = Objects.requireNonNull(packet,
                                         "packet is null");
    this.buffer = packet.getData().order(ByteOrder.LITTLE_ENDIAN);
  }

  @Override
  public Packet getPacket()
  {
    return packet;
  }

}
