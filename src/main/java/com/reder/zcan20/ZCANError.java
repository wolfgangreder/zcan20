/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.reder.zcan20;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Objects;
import javax.validation.constraints.NotNull;

/**
 *
 * @author reder
 */
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

  protected ZCANError(int nid,
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
    synchronized (value) {
      if (buffer == null) {
        buffer = ByteBuffer.wrap(value).asReadOnlyBuffer();
      }
      return buffer;
    }
  }

  public int getCommand()
  {
    return command;
  }

}
