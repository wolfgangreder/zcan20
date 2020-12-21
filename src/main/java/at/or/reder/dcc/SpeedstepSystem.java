/*
 * Copyright 2020 Wolfgang Reder.
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
package at.or.reder.dcc;

public enum SpeedstepSystem
{
  UNKNOWN(0,
          1024),
  SPEED_14(0x4,
           14),
  SPEED_28(0x8,
           28),
  SPEED_128(0xc,
            126);
  private final int magic;
  private final int stepMax;

  private SpeedstepSystem(int magic,
                          int stepMax)
  {
    this.magic = magic;
    this.stepMax = stepMax;
  }

  public int getMagic()
  {
    return magic;
  }

  public int getStepMax()
  {
    return stepMax;
  }

  public int normalizedToSystem(int normalized)
  {
    return (int) (((float) normalized / 1024f) * ((float) getStepMax()));
  }

  public int systemToNormalized(int speed)
  {
    return (int) ((1024f * speed) / (float) getStepMax());
  }

  public static SpeedstepSystem valueOfMagic(int magic)
  {
    for (SpeedstepSystem ss : values()) {
      if (ss.magic == magic) {
        return ss;
      }
    }
    return SpeedstepSystem.UNKNOWN;
  }

}
