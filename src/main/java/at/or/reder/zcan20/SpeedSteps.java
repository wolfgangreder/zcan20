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

public enum SpeedSteps
{
  UNKNOWN(0,
          false),
  STEP_14(1,
          true),
  STEP_27(2,
          true),
  STEP_28(3,
          true),
  STEP_128(4,
           true),
  STEP_1024(5,
            true);
  private final int magic;
  private final boolean validInSet;

  private SpeedSteps(int magic,
                     boolean validInSet)
  {
    this.magic = magic;
    this.validInSet = validInSet;
  }

  public boolean isValidInSet()
  {
    return validInSet;
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
