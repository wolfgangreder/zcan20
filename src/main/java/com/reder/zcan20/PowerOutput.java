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
 */package com.reder.zcan20;

import java.util.Collection;
import java.util.EnumSet;
import java.util.Objects;
import javax.validation.constraints.NotNull;

/**
 *
 * @author Wolfgang Reder
 */
public enum PowerOutput
{
  OUT_1(1),
  OUT_2(2),
  OUT_3(4),
  OUT_4(8),
  OUT_5(16),
  OUT_6(32),
  OUT_7(64),
  BOOSTER(128),
  UNKNOWN(0);
  private final int magic;

  private PowerOutput(int magic)
  {
    this.magic = magic;
  }

  public int getMagic()
  {
    return magic;
  }

  public static PowerOutput valueOfMagic(int magic)
  {
    int v = magic & 0xff;
    for (PowerOutput o : values()) {
      if (v == o.getMagic()) {
        return o;
      }
    }
    throw new IllegalArgumentException("invalid magic 0x" + Integer.toHexString(magic));
  }

  public static EnumSet<PowerOutput> toSet(int value)
  {
    int v = value & 0xff;
    EnumSet<PowerOutput> result = EnumSet.noneOf(PowerOutput.class);
    for (PowerOutput o : values()) {
      if ((v & o.getMagic()) != 0) {
        result.add(o);
      }
    }
    return result;
  }

  public static int toValue(@NotNull Collection<? extends PowerOutput> flags)
  {
    Objects.requireNonNull(flags);
    int result = 0;
    for (PowerOutput o : flags) {
      result += o.getMagic();
    }
    return result;
  }

}
