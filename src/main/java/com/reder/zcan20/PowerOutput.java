/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.reder.zcan20;

import java.util.Collection;
import java.util.EnumSet;
import java.util.Objects;
import javax.validation.constraints.NotNull;

/**
 *
 * @author reder
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
  BOOSTER(128);
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
