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
package at.or.reder.zcan20;

import at.or.reder.dcc.util.Utils;
import javax.validation.constraints.NotNull;

/**
 *
 * @author Wolfgang Reder
 */
public final class CanId extends Number
{

  private final int value;

  public static CanId valueOf(int val)
  {
    return new CanId(val);
  }

  public static CanId valueOf(@NotNull CommandGroup group,
                              byte command,
                              @NotNull CommandMode mode,
                              short senderNid)
  {
    int result = 1 << 28;
    result |= (group.getMagic() & 0xf) << 24;
    result |= (command & 0x3f) << 18;
    result |= (mode.getMagic() & 0x3) << 16;
    result |= (senderNid & 0xffff);
    return new CanId(result);
  }

  private CanId(int value)
  {
    if ((value & (1 << 28)) == 0) {
      throw new IllegalArgumentException("Invalid zimo can id");
    }
    this.value = value;
  }

  public CommandGroup getCommandGroup()
  {
    return CommandGroup.valueOf((byte) ((value >> 24) & 0x0f));
  }

  public byte getCommand()
  {
    return (byte) ((value >> 18) & 0x3f);
  }

  public CommandMode getCommandMode()
  {
    return CommandMode.valueOfMagic((byte) ((value >> 16) & 0x3));
  }

  public short getSenderNid()
  {
    return (short) value;
  }

  @Override
  public int hashCode()
  {
    int hash = 7;
    hash = 67 * hash + this.value;
    return hash;
  }

  @Override
  public int intValue()
  {
    return value;
  }

  @Override
  public long longValue()
  {
    return value & 0xffff_ffff;
  }

  @Override
  public float floatValue()
  {
    return value;
  }

  @Override
  public double doubleValue()
  {
    return value;
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
    final CanId other = (CanId) obj;
    return this.value == other.value;
  }

  @Override
  public String toString()
  {
    StringBuilder builder = new StringBuilder("CanId(0x");
    Utils.appendHexString(value,
                          builder,
                          8);
    builder.append(", ");
    builder.append(getCommandGroup());
    builder.append(", ");
    Utils.appendHexString(getCommand(),
                          builder,
                          1);
    builder.append(", ");
    builder.append(getCommandMode());
    builder.append(", 0x");
    Utils.appendHexString(getSenderNid() & 0xffff,
                          builder,
                          4);
    return builder.append(')').toString();
  }

}
