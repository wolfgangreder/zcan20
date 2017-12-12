/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.reder.zcan20;

/**
 *
 * @author reder
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
  private final int magic;

  private CommandMode(int magic)
  {
    this.magic = magic;
  }

  public int getMagic()
  {
    return magic;
  }

  public static CommandMode valueOfMagic(int magic)
  {
    final int tmp = magic & 0x3;
    for (CommandMode m : values()) {
      if (m.magic == tmp) {
        return m;
      }
    }
    throw new IllegalArgumentException("Invalid magic " + magic);
  }

}
