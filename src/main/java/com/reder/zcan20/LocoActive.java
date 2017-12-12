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
package com.reder.zcan20;

/**
 *
 * @author Wolfgang Reder
 */
public enum LocoActive
{
  UNKNOWN(0),
  ACTIVE(0x01),
  FORECE_ACTIVE(0x10);
  private final byte magic;

  private LocoActive(int magic)
  {
    this.magic = (byte) magic;
  }

  public byte getMagic()
  {
    return magic;
  }

  public LocoActive valueOfMagic(byte magic)
  {
    for (LocoActive a : values()) {
      if (a.magic == magic) {
        return a;
      }
    }
    throw new IllegalArgumentException("Invalid LocoActive magic " + magic);
  }

}
