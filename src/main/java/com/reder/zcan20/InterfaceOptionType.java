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
public enum InterfaceOptionType
{
  PROVIDER(0x0001);
  private final int magic;

  private InterfaceOptionType(int magic)
  {
    this.magic = magic;
  }

  public int getMagic()
  {
    return magic;
  }

}
