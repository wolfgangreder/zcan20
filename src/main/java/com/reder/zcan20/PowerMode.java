/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.reder.zcan20;

/**
 *
 * @author reder
 */
public enum PowerMode
{
  PENDING(0),
  ON(1),
  SSP0(2),
  SSPE(3),
  OFF(4),
  SERVICE(5);
  private final int magic;

  private PowerMode(int magic)
  {
    this.magic = magic;
  }

  public int getMagic()
  {
    return magic;
  }

  public static PowerMode valueOfMagic(int magic)
  {
    int tmp = magic & 0xff;
    for (PowerMode m : values()) {
      if (m.magic == tmp) {
        return m;
      }
    }
    throw new IllegalArgumentException("invalid magic 0x" + magic);
  }

}
