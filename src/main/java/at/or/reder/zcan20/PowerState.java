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
 */
package at.or.reder.zcan20;

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
