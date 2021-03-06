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
public enum Protocol
{
  UNKNOWN(0,
          false),
  DCC(1,
      true),
  MM2(2,
      true),
  NOT_DEFINED(3,
              false),
  MFX(4,
      true);
  private final byte magic;
  private final boolean validInSet;

  private Protocol(int magic,
                   boolean validInSet)
  {
    this.magic = (byte) magic;
    this.validInSet = validInSet;
  }

  public boolean isValidInSet()
  {
    return validInSet;
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
