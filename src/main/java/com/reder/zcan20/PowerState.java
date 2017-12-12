/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.reder.zcan20;

import java.util.EnumSet;
import java.util.Set;

/**
 *
 * @author Wolfgang Reder
 */
public enum PowerState
{
  // bits 0..3
  RUN(0),
  SSP(1),
  SERVICE(2),
  FREE(3),
  DECODER_UPDATE(4),
  SOUND_LOAD(5),
  // bits 4..7
  UNDERVOLTAGE(0x10),
  OVERCURRENT(0x20),
  SUPPLYVOLTAGE(0x40),
  // bit 10
  ZACK(0x400),
  // bit11
  RAILCOM(0x800),
  // bit12
  MFX(0x1000);
  private final int magic;

  private PowerState(int magic)
  {
    this.magic = magic;
  }

  public int getMagic()
  {
    return magic;
  }

  public static Set<PowerState> toSet(int magic)
  {
    EnumSet<PowerState> result = EnumSet.noneOf(PowerState.class);
    switch (magic & 0xf) {
      case 0:
        result.add(RUN);
        break;
      case 1:
        result.add(SSP);
        break;
      case 2:
        result.add(SERVICE);
        break;
      case 3:
        result.add(FREE);
        break;
      case 4:
        result.add(DECODER_UPDATE);
        break;
      case 5:
        result.add(SOUND_LOAD);
        break;
    }
    if ((magic & UNDERVOLTAGE.magic) != 0) {
      result.add(UNDERVOLTAGE);
    }
    if ((magic & OVERCURRENT.magic) != 0) {
      result.add(OVERCURRENT);
    }
    if ((magic & SUPPLYVOLTAGE.magic) != 0) {
      result.add(SUPPLYVOLTAGE);
    }
    if ((magic & ZACK.magic) != 0) {
      result.add(ZACK);
    }
    if ((magic & RAILCOM.magic) != 0) {
      result.add(RAILCOM);
    }
    if ((magic & MFX.magic) != 0) {
      result.add(MFX);
    }
    return result;
  }

}
