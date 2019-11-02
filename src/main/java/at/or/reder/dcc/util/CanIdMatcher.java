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
package at.or.reder.dcc.util;

import at.or.reder.zcan20.CanId;
import at.or.reder.zcan20.packet.Packet;
import java.util.Objects;
import java.util.function.Predicate;
import javax.validation.constraints.NotNull;

/**
 *
 * @author Wolfgang Reder
 */
public final class CanIdMatcher implements Predicate<Packet>
{

  public static final int MASK_NO_ADDRESS = 0x1fff0000;
  public static final int MASK_ALL = 0x1fffffff;
  public static final int MASK_ADDRESS = 0x1000ffff;
  public static final int MASK_COMMANDGROUP = 0x1f000000;
  public static final int MASK_MODE = 0x10300000;
  public static final int MASK_COMMAND = 0x1fc00000;
  private final CanId canId;
  private final int mask;

  public CanIdMatcher(@NotNull CanId canId,
                      int mask)
  {
    this.canId = Objects.requireNonNull(canId,
                                        "canId is null");
    this.mask = mask | (1 << 28);
  }

  public CanId getCanId()
  {
    return canId;
  }

  public int getMask()
  {
    return mask;
  }

  @Override
  public boolean test(Packet t)
  {
    return matchesPacket(t);
  }

  public boolean matchesPacket(@NotNull Packet packet)
  {
    return matchesId(packet.getCanId());
  }

  public boolean matchesId(@NotNull CanId ci)
  {
    return (ci.intValue() & mask) == (canId.intValue() & mask);
  }

  @Override
  public int hashCode()
  {
    int hash = 3;
    hash = 97 * hash + Objects.hashCode(this.canId);
    hash = 97 * hash + this.mask;
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
    final CanIdMatcher other = (CanIdMatcher) obj;
    if (this.mask != other.mask) {
      return false;
    }
    return Objects.equals(this.canId,
                          other.canId);
  }

  @Override
  public String toString()
  {
    return "CanIdMatcher{" + "canId=" + Integer.toHexString(canId.intValue()) + ", mask=" + Integer.toHexString(mask) + '}';
  }

}
