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
 * @author Wolfang Reder
 */
public enum CommandMode
{
  /**
   * Request some information (0x00)
   */
  REQUEST(0),
  /**
   * Do a command (0x01)
   */
  COMMAND(1),
  /**
   * A notification (0x02)
   */
  EVENT(2),
  /**
   * A command notification (0x03)
   */
  ACK(3);
  private final byte magic;

  private CommandMode(int magic)
  {
    this.magic = (byte) magic;
  }

  public byte getMagic()
  {
    return magic;
  }

  public static CommandMode valueOfMagic(byte magic)
  {
    final byte tmp = (byte) (magic & 0x3);
    for (CommandMode m : values()) {
      if (m.magic == tmp) {
        return m;
      }
    }
    throw new IllegalArgumentException("Invalid magic " + magic);
  }

}
