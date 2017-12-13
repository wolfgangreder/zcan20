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
package com.reder.zcan20.packet.impl;

import com.reder.zcan20.CommandGroup;
import com.reder.zcan20.CommandMode;
import com.reder.zcan20.packet.Packet;
import com.reder.zcan20.packet.PacketAdapter;
import com.reder.zcan20.util.Utils;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.function.Function;
import javax.validation.constraints.NotNull;
import org.openide.util.Lookup;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author Wolfgang Reder
 */
public final class DefaultPacket implements Packet
{

  private final CommandGroup group;
  private final CommandMode mode;
  private final byte command;
  private final short address;
  private final ByteBuffer data;
  private final Lookup lookup;
  private String string;

  @SuppressWarnings("LeakingThisInConstructor")
  DefaultPacket(@NotNull CommandGroup group,
                @NotNull CommandMode mode,
                byte command,
                short address,
                ByteBuffer data,
                Function<? super Packet, ? extends PacketAdapter> adapterFactory)
  {
    this.group = group;
    this.mode = mode;
    this.command = command;
    this.address = address;
    final int dataLen = data != null ? data.remaining() : 0;
    if (dataLen > 0) {
      ByteBuffer tmp = Utils.allocateLEBuffer(dataLen);
      tmp.put(data);
      tmp.clear();
      this.data = tmp.asReadOnlyBuffer();
    } else {
      this.data = ByteBuffer.allocate(0).asReadOnlyBuffer();
    }
    if (adapterFactory != null) {
      lookup = Lookups.singleton(adapterFactory.apply(this));
    } else {
      lookup = Lookup.EMPTY;
    }
  }

  @Override
  public Lookup getLookup()
  {
    return lookup;
  }

  @Override
  public CommandGroup getCommandGroup()
  {
    return group;
  }

  @Override
  public CommandMode getCommandMode()
  {
    return mode;
  }

  @Override
  public byte getCommand()
  {
    return command;
  }

  @Override
  public short getSenderNID()
  {
    return address;
  }

  @Override
  public Packet getPacket()
  {
    return null;
  }

  @Override
  public ByteBuffer getData()
  {
    return data.duplicate().order(ByteOrder.LITTLE_ENDIAN);
  }

  @Override
  public synchronized String toString()
  {
    if (string == null) {
      StringBuilder tmp = new StringBuilder();
      tmp.append("0x");
      tmp.append(Integer.toHexString(group.getMagic()));
      tmp.append(", 0x");
      tmp.append(Integer.toHexString(command));
      tmp.append(", ");
      tmp.append(mode.name());
      tmp.append(", ");
      tmp.append(data.capacity());
      if (data.capacity() > 0) {
        tmp.append(", ");
        for (int i = 0; i < data.capacity(); ++i) {
          Utils.appendHexString(data.get(i) & 0xff,
                                tmp,
                                2);
          tmp.append(' ');
        }
      }
      string = tmp.toString();
    }
    return string;
  }

}
