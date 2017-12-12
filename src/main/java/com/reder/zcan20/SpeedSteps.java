/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.reder.zcan20;

/**
 *
 * @author Wolfgang Reder
 */
public enum SpeedSteps
{
  UNKNOWN(0),
  STEP_14(1),
  STEP_27(2),
  STEP_28(3),
  STEP_128(4),
  STEP_1024(5);
  private final int magic;

  private SpeedSteps(int magic)
  {
    this.magic = magic;
  }

  public byte getMagic()
  {
    return (byte) magic;
  }

  public static SpeedSteps valueOfMagic(int magic)

  {
    int tmp = magic & 0xff;
    for (SpeedSteps ss : values()) {
      if (ss.magic == tmp) {
        return ss;
      }
    }
    return UNKNOWN;
  }

}
