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
public enum Protocol
{
  UNKNOWN(0),
  DCC(1),
  MM2(2),
  NOT_DEFINED(3),
  MFX(4);
  private final byte magic;

  private Protocol(int magic)
  {
    this.magic = (byte) magic;
  }

  public byte getMagic()
  {
    return magic;
  }

  public static Protocol valueOfMagic(int magic)
  {
    byte tmp = (byte) magic;
    for (Protocol p : values()) {
      if (p.magic == tmp) {
        return p;
      }
    }
    return UNKNOWN;
  }

}
