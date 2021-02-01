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

import at.or.reder.dcc.util.DCCUtils;
import at.or.reder.zcan20.CanId;
import at.or.reder.zcan20.CommandGroup;
import at.or.reder.zcan20.CommandMode;
import at.or.reder.zcan20.packet.Packet;
import at.or.reder.zcan20.packet.PacketAdapter;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Objects;
import javax.validation.constraints.NotNull;
import org.openide.util.Lookup;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;

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
  private final CanId canId;
  private String string;

  @SuppressWarnings("LeakingThisInConstructor")
  DefaultPacket(@NotNull CommandGroup group,
                @NotNull CommandMode mode,
                byte command,
                short address,
                ByteBuffer data,
                InstanceContent.Convertor<Packet, PacketAdapter> adapterFactory)
  {
    this.group = group;
    this.mode = mode;
    this.command = command;
    this.address = address;
    final int dataLen = data != null ? data.remaining() : 0;
    if (dataLen > 0) {
      ByteBuffer tmp = DCCUtils.allocateLEBuffer(dataLen);
      tmp.put(data);
      tmp.clear();
      this.data = tmp.asReadOnlyBuffer();
    } else {
      this.data = ByteBuffer.allocate(0).asReadOnlyBuffer();
    }
    canId = CanId.valueOf(group,
                          command,
                          mode,
                          address);
    if (adapterFactory != null) {
      InstanceContent ic = new InstanceContent();
      ic.add(this,
             adapterFactory);
      lookup = new AbstractLookup(ic);
    } else {
      lookup = Lookup.EMPTY;
    }
  }

  @Override
  public CanId getCanId()
  {
    return canId;
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
  public int getDLC()
  {
    return data.limit();
  }

  @Override
  public int hashCode()
  {
    return getCanId().intValue();
  }

  @Override
  public boolean equals(Object obj)
  {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final DefaultPacket other = (DefaultPacket) obj;
    if (this.command != other.command) {
      return false;
    }
    if (this.address != other.address) {
      return false;
    }
    if (!Objects.equals(this.group,
                        other.group)) {
      return false;
    }
    if (this.mode != other.mode) {
      return false;
    }
    return Objects.equals(this.data,
                          other.data);
  }

  @Override
  public synchronized String toString()
  {
    if (string == null) {
      string = DCCUtils.packetToString(this);
    }
    return string;
  }

}
