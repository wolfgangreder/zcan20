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
package at.or.reder.mx1;

public enum MX1LocoFlags
{
  MAN(0x80),
  DIRECTION(0x20),
  F0(0x10),
  REDUCED_SPEED(0x0c),
  BZ_ON(0x2),
  AZ_ON(0x01);
  private final int mask;

  private MX1LocoFlags(int mask)
  {
    this.mask = mask;
  }

  public int getMask()
  {
    return mask;
  }

}
