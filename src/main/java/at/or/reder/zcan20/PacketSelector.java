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
package at.or.reder.zcan20;

import at.or.reder.zcan20.packet.Packet;
import java.util.Objects;
import java.util.function.Predicate;

/**
 *
 * @author Wolfgang Reder
 */
public final class PacketSelector implements Predicate<Packet>
{

  private final CommandGroup group;
  private final byte command;
  private final CommandMode mode;
  private final int dlc;

  public PacketSelector(CommandGroup group,
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
  public boolean test(Packet t)
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

  public boolean matches(PacketSelector t)
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

  public CommandGroup getCommandGroup()
  {
    return group;
  }

  public byte getCommand()
  {
    return command;
  }

  public CommandMode getCommandMode()
  {
    return mode;
  }

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
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final PacketSelector other = (PacketSelector) obj;
    if (this.command != other.command) {
      return false;
    }
    if (this.dlc != other.dlc) {
      return false;
    }
    if (!Objects.equals(this.group,
                        other.group)) {
      return false;
    }
    return this.mode == other.mode;
  }

  @Override
  public String toString()
  {
    return "PacketSelector{" + "group=" + group + ", command=" + command + ", mode=" + mode + ", dlc=" + dlc + '}';
  }

}
