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
package at.or.reder.zcan20;

import at.or.reder.zcan20.util.IntBitFlagCollector;
import java.util.Collection;
import java.util.EnumSet;
import java.util.Set;

/**
 *
 * @author Wolfgang Reder
 */
public enum PowerStateEx
{
  RUN(0),
  OFF(1),
  OVERCURRENT(0x04),
  SSPF0(0x10),
  SSPEM(0x20);
  private final int magic;

  private PowerStateEx(int magic)
  {
    this.magic = (byte) magic;
  }

  public int getMagic()
  {
    return magic;
  }

  public static Set<PowerStateEx> toSet(int magic)
  {
    EnumSet<PowerStateEx> result = EnumSet.noneOf(PowerStateEx.class);
    if (magic != 0) {
      for (PowerStateEx pse : values()) {
        if ((pse.magic & magic) != 0) {
          result.add(pse);
        }
      }
    } else {
      result.add(PowerStateEx.RUN);
    }
    return result;
  }

  public static int toFlags(Collection<? extends PowerStateEx> c)
  {
    return c.stream().map(PowerStateEx::getMagic).collect(new IntBitFlagCollector());
  }

}
