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
package com.reder.zcan20;

import java.util.Collection;
import java.util.EnumSet;
import java.util.Set;
import javax.validation.constraints.NotNull;

/**
 *
 * @author Wolfgang Reder
 */
public enum SpeedFlags
{
  FORWARD_TO_SYSTEM,
  FORWARD_FROM_SYSTEM,
  REVERSE_TO_SYSTEM,
  REVERSE_FROM_SYSTEM,
  EMERGENCY_STOP;

  /**
   * Speedflags.
   * Bit 0..9 Speed
   * Bit 10 Direction to System
   * Bit 11 Direction from System
   * Bit 15 Emergency stop
   *
   * @param mask speedflags
   * @return Set of flags. Never {@code null} or empty.
   */
  public static Set<SpeedFlags> setOfMask(short mask)
  {
    EnumSet<SpeedFlags> result = EnumSet.noneOf(SpeedFlags.class);
    if ((mask & 0x400) != 0) {
      result.add(REVERSE_TO_SYSTEM);
    } else {
      result.add(FORWARD_TO_SYSTEM);
    }
    if ((mask & 0x800) != 0) {
      result.add(REVERSE_FROM_SYSTEM);
    } else {
      result.add(FORWARD_FROM_SYSTEM);
    }
    if ((mask & 0x8000) != 0) {
      result.add(EMERGENCY_STOP);
    }
    return result;
  }

  /**
   * Composes a mask from the collection {@code flags}.
   * If conficting Flags are in the collection a {@link java.lang.IllegalArgumentException} is thrown
   *
   * @param flags flags to mask
   * @return mask
   * @throws java.lang.IllegalArgumentException
   * @see #setOfMask(short)
   */
  public static short maskOfSet(@NotNull Collection<? extends SpeedFlags> flags) throws IllegalArgumentException
  {
    if (flags.contains(FORWARD_FROM_SYSTEM) && flags.contains(REVERSE_FROM_SYSTEM)) {
      throw new IllegalArgumentException("conflicting from system flags");
    }
    if (flags.contains(FORWARD_TO_SYSTEM) && (flags.contains(REVERSE_TO_SYSTEM))) {
      throw new IllegalArgumentException("conflicting to system flags");
    }
    int mask = 0;
    if (flags.contains(REVERSE_TO_SYSTEM)) {
      mask |= 0x400;
    }
    if (flags.contains(REVERSE_FROM_SYSTEM)) {
      mask |= 0x800;
    }
    if (flags.contains(EMERGENCY_STOP)) {
      mask |= 0x8000;
    }
    return (short) mask;
  }

}
