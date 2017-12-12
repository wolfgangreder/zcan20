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
public enum SpeedlimitMode
{
  NO_LIMIT(0),
  NMRA(1),
  ZIMO(2);

  private final byte magic;

  private SpeedlimitMode(int magic)
  {
    this.magic = (byte) magic;
  }

  public byte getMagic()
  {
    return magic;
  }

  public static SpeedlimitMode valueOfMagic(int magic)
  {
    byte tmp = (byte) magic;
    for (SpeedlimitMode m : values()) {
      if (m.magic == tmp) {
        return m;
      }
    }
    return NO_LIMIT;
  }

}
