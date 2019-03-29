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

/**
 *
 * @author Wolfgang Reder
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
