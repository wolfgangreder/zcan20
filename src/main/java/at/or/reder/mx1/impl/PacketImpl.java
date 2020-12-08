/*
 * Copyright 2020 Wolfgang Reder.
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
package at.or.reder.mx1.impl;

import at.or.reder.dcc.util.Utils;
import at.or.reder.mx1.MX1Command;
import at.or.reder.mx1.MX1Packet;
import at.or.reder.mx1.MX1PacketAdapter;
import at.or.reder.mx1.MX1PacketFlags;
import java.nio.ByteBuffer;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import javax.validation.constraints.NotNull;

final class PacketImpl implements MX1Packet
{

  private final LocalDateTime timestamp = LocalDateTime.now();
  private final int sequence;
  private final Set<MX1PacketFlags> flags;
  private final MX1Command command;
  private final ByteBuffer payload;
  private final MX1PacketAdapter adapter;
  private String toString;

  PacketImpl(byte sequence,
             @NotNull Collection<? extends MX1PacketFlags> flags,
             @NotNull MX1Command command,
             ByteBuffer payload)
  {
    this.sequence = sequence;
    this.command = Objects.requireNonNull(command,
                                          "command is null");
    this.flags = Utils.copyToUnmodifiableEnumSet(Objects.requireNonNull(flags,
                                                                        "flags is null"),
                                                 MX1PacketFlags.class,
                                                 null);
    ByteBuffer tmp;
    if (payload != null && payload.remaining() > 0) {
      tmp = Utils.allocateBEBuffer(payload.remaining());
      tmp.put(payload);
    } else {
      tmp = ByteBuffer.allocate(0);
    }
    tmp.rewind();
    this.payload = tmp.asReadOnlyBuffer();
    switch (command) {
      case RW_DECODER_CV:
        adapter = new CVPacketAdapterImpl(this);
        break;
      case ACCEL:
      case ACCESSORY:
      case ADDRESS_CONTROL:
      case CS_EQ_QUERY:
      case CURRENT_DECODER_MEM:
      case CURRENT_LOCO_MEM:
      case INVERT_FUNCTION:
      case LOCO_CONTROL:
      case NACK:
      case QUERY_CS_DECODER:
      case QUERY_CS_LOCO:
      case READ_CS_IO:
      case RESET:
      case RW_CS_CV:
      case SERIAL_INFO:
      case SHUTTLE_TRAIN:
      case TRACK_CONTROL:
      default:
        adapter = null;
    }
    toString = null; // hilft bei debuggen, und kostet nicht viel
  }

  @Override
  public LocalDateTime getTimestamp()
  {
    return timestamp;
  }

  @Override
  public int getSequence()
  {
    return sequence;
  }

  @Override
  public Set<MX1PacketFlags> getFlags()
  {
    return flags;
  }

  @Override
  public MX1Command getCommand()
  {
    return command;
  }

  @Override
  public int getDataLength()
  {
    return payload.limit();
  }

  @Override
  public ByteBuffer getData()
  {
    return payload.rewind();
  }

  @Override
  public <A extends MX1PacketAdapter> A getAdapter(@NotNull Class<? extends A> clazz)
  {
    if (Objects.requireNonNull(clazz,
                               "clazz is null").isInstance(adapter)) {
      return clazz.cast(adapter);
    }
    return null;
  }

  @Override
  public String toString()
  {
    synchronized (this) {
      if (toString == null) {
        StringBuilder tmp = new StringBuilder("M1Packet: #");
        tmp.append(sequence);
        tmp.append(", ");
        tmp.append(flags.stream().map(MX1PacketFlags::name).collect(Collectors.joining(",",
                                                                                       "[",
                                                                                       "]")));
        tmp.append(", ");
        tmp.append(command.name());
        tmp.append(", ");
        Utils.byteBuffer2HexString(payload,
                                   tmp,
                                   ' ');
        toString = tmp.toString();
      }
      return toString;
    }
  }

}
