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

import at.or.reder.zcan20.CommandGroup;
import at.or.reder.zcan20.CommandMode;
import at.or.reder.zcan20.PacketSelector;
import at.or.reder.zcan20.PacketSelectorCriteria;
import at.or.reder.zcan20.packet.Packet;
import java.util.Objects;

/**
 *
 * @author Wolfgang Reder
 */
public final class PacketSelectorImpl implements PacketSelector, PacketSelectorCriteria
{

  private final CommandGroup group;
  private final byte command;
  private final CommandMode mode;
  private final int dlc;

  public PacketSelectorImpl(CommandGroup group,
                            byte command,
                            CommandMode mode,
                            int dlc)
  {
    this.group = group;
    this.command = command;
    this.mode = mode;
    this.dlc = dlc;
  }

  @Override
  public boolean matches(Packet t)
  {
    if (t == null) {
      return false;
    }
    if (group != null && t.getCommandGroup() != group) {
      return false;
    }
    if (mode != null && t.getCommandMode() != mode) {
      return false;
    }
    if (dlc >= 0 && t.getDLC() != dlc) {
      return false;
    }
    return command == t.getCommand();
  }

  @Override
  public boolean test(PacketSelector t)
  {
    if (!(t instanceof PacketSelectorCriteria)) {
      return false;
    }
    PacketSelectorCriteria c = (PacketSelectorCriteria) t;
    if (group != null && c.getCommandGroup() != group) {
      return false;
    }
    if (mode != null && c.getCommandMode() != mode) {
      return false;
    }
    if (dlc >= 0 && c.getDLC() != dlc) {
      return false;
    }
    return command == c.getCommand();
  }

  @Override
  public CommandGroup getCommandGroup()
  {
    return group;
  }

  @Override
  public byte getCommand()
  {
    return command;
  }

  @Override
  public CommandMode getCommandMode()
  {
    return mode;
  }

  @Override
  public int getDLC()
  {
    return dlc;
  }

  @Override
  public int hashCode()
  {
    int hash = 3;
    hash = 61 * hash + Objects.hashCode(this.group);
    hash = 61 * hash + this.command;
    hash = 61 * hash + Objects.hashCode(this.mode);
    hash = 61 * hash + this.dlc;
    return hash;
  }

  @Override
  public boolean equals(Object obj)
  {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof PacketSelectorCriteria)) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final PacketSelectorCriteria other = (PacketSelectorCriteria) obj;
    if (this.group != other.getCommandGroup()) { // instanzevergleich ist ok
      return false;
    }
    if (this.command != other.getCommand()) {
      return false;
    }
    if (this.dlc != other.getDLC()) {
      return false;
    }
    return this.mode == other.getCommandMode();
  }

  @Override
  public String toString()
  {
    StringBuilder builder = new StringBuilder("PacketSelectorImpl{group=");
    builder.append(group);
    builder.append(", command=");
    builder.append(command);
    builder.append(", mode=");
    if (mode != null) {
      builder.append(mode);
      builder.append(" 0b");
      String tmp = Integer.toBinaryString(mode.getMagic() & 0x3);
      if (tmp.length() == 1) {
        builder.append('0');
      }
      builder.append(tmp);
    } else {
      builder.append("null");
    }
    builder.append(", dlc=");
    builder.append(dlc);
    builder.append('}');
    return builder.toString();
  }

}
