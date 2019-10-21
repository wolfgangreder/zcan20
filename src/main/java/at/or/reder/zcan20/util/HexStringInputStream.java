/*
 * Copyright 2019 Wolfgang Reder.
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
package at.or.reder.zcan20.util;

import java.io.IOException;
import java.io.InputStream;

/**
 *
 * @author Wolfgang Reder
 */
public final class HexStringInputStream extends InputStream
{

  private final String string;
  private int offset;

  public HexStringInputStream(String string)
  {
    this.string = string;
    if ((string.length() & 0x1) != 0) {
      throw new IllegalArgumentException("String must be of even length");
    }
  }

  @Override
  public int read() throws IOException
  {
    if (offset + 2 <= string.length()) {
      String tmp = string.substring(offset,
                                    offset + 2);
      offset += 2;
      try {
        return Integer.parseInt(tmp,
                                16) & 0xff;
      } catch (Throwable th) {
        throw new IOException(th);
      }
    }
    return -1;

  }

  @Override
  public int available()
  {
    return (string.length() - offset) / 2;
  }

}
