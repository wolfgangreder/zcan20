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
package at.or.reder.zcan20.ui;

import at.or.reder.dcc.util.DCCUtils;
import at.or.reder.zcan20.PacketSelector;
import at.or.reder.zcan20.packet.Packet;

/**
 *
 * @author Wolfgang Reder
 */
public final class PatternPacketSelector implements PacketSelector
{

  private final int mask;
  private final int pattern;

  public PatternPacketSelector(int mask,
                               int pattern)
  {
    this.mask = mask;
    this.pattern = pattern & mask;
  }

  @Override
  public boolean matches(Packet packet)
  {
    if (packet != null) {
      int id = packet.getCanId().intValue();
      return (id & mask) == pattern;
    }
    return false;
  }

  @Override
  public boolean test(PacketSelector t)
  {
    return equals(t);
  }

  @Override
  public int hashCode()
  {
    int hash = 3;
    hash = 79 * hash + this.mask;
    hash = 79 * hash + this.pattern;
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
    final PatternPacketSelector other = (PatternPacketSelector) obj;
    if (this.mask != other.mask) {
      return false;
    }
    return this.pattern == other.pattern;
  }

  @Override
  public String toString()
  {
    StringBuilder builder = new StringBuilder("PatternPacketSelector{mask=0x");
    DCCUtils.appendHexString(mask,
                          builder,
                          8);
    builder.append(", pattern=0x");
    DCCUtils.appendHexString(pattern,
                          builder,
                          8);
    builder.append('}');
    return builder.toString();
  }

}
