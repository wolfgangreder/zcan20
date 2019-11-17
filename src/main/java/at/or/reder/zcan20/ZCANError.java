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
 */package at.or.reder.zcan20;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Objects;
import javax.validation.constraints.NotNull;

public class ZCANError extends IOException
{

  private static final long serialVersionUID = 1L;
  private final int nid;
  private final CommandGroup commandGroup;
  private final int command;
  private final byte[] value;
  private transient ByteBuffer buffer;

  public static ZCANError valueOf(@NotNull ByteBuffer data)
  {
    Objects.requireNonNull(data,
                           "Databuffer is null");
    if (data.remaining() < 8) {
      throw new IllegalArgumentException("Databuffer must be 8 bytes long or longer");
    }
    ByteBuffer working = data.slice();
    working.order(ByteOrder.LITTLE_ENDIAN);
    int nid = working.getShort() & 0xffff;
    CommandGroup group = CommandGroup.valueOf(working.get());
    int command = working.getShort() & 0xffff;
    byte[] value = new byte[data.remaining()];
    data.get(value);
    return new ZCANError(nid,
                         group,
                         command,
                         value,
                         "ZCANError");
  }

  public ZCANError(int nid,
                   CommandGroup commandGroup,
                   int command,
                   byte[] value,
                   String message)
  {
    super(message);
    Objects.requireNonNull(value,
                           "value is null");
    this.nid = nid;
    this.command = command;
    this.commandGroup = commandGroup;
    this.value = value;
  }

  public int getNid()
  {
    return nid;
  }

  public CommandGroup getCommandGroup()
  {
    return commandGroup;
  }

  public ByteBuffer getValue()
  {
    if (value != null) {
      synchronized (value) {
        if (buffer == null) {
          buffer = ByteBuffer.wrap(value).asReadOnlyBuffer();
        }
        return buffer;
      }
    }
    return null;
  }

  public int getCommand()
  {
    return command;
  }

}
