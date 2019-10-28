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
 */package at.or.reder.dcc;

import java.util.Collection;
import java.util.EnumSet;
import java.util.Objects;
import javax.validation.constraints.NotNull;

/**
 *
 * @author Wolfgang Reder
 */
public enum PowerPort
{
  OUT_1(1,
        true),
  OUT_2(2,
        true),
  OUT_3(4,
        true),
  OUT_4(8,
        true),
  OUT_5(16,
        true),
  OUT_6(32,
        true),
  OUT_7(64,
        true),
  BOOSTER(128,
          true),
  UNKNOWN(0,
          false);
  private final byte magic;
  private final boolean validInSet;

  private PowerPort(int magic,
                    boolean validInSet)
  {
    this.magic = (byte) magic;
    this.validInSet = validInSet;
  }

  public boolean isValidInSet()
  {
    return validInSet;
  }

  public byte getMagic()
  {
    return magic;
  }

  public static PowerPort valueOfMagic(int magic)
  {
    int v = magic & 0xff;
    for (PowerPort o : values()) {
      if (v == o.getMagic()) {
        return o;
      }
    }
    throw new IllegalArgumentException("invalid magic 0x" + Integer.toHexString(magic));
  }

  public static EnumSet<PowerPort> toSet(byte value)
  {
    int v = value & 0xff;
    EnumSet<PowerPort> result = EnumSet.noneOf(PowerPort.class);
    for (PowerPort o : values()) {
      if ((v & o.getMagic()) != 0) {
        result.add(o);
      }
    }
    return result;
  }

  public static byte toValue(@NotNull Collection<? extends PowerPort> flags)
  {
    Objects.requireNonNull(flags);
    byte result = 0;
    for (PowerPort o : flags) {
      result += o.getMagic();
    }
    return result;
  }

}
