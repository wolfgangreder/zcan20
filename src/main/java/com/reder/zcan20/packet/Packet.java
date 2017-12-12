/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.reder.zcan20.packet;

import com.reder.zcan20.CommandGroup;
import com.reder.zcan20.CommandMode;
import java.nio.ByteBuffer;
import org.openide.util.Lookup;

/**
 * Represents a general Packet.
 * Datapecialisation can be obtained via {@link #getExtension(java.lang.Class) }.
 *
 * @author reder
 */
public interface Packet extends PacketAdapter, Lookup.Provider
{

  public static final String LOOKUPPATH = "com/reder/zcan20/adapter";

  /**
   * The CommandGroup of the Packet.
   *
   * @return commandGroup (never {@code null})
   */
  public CommandGroup getCommandGroup();

  /**
   * The CommandMode of the Packet.
   *
   * @return commandMode {never {@code null})
   */
  public CommandMode getCommandMode();

  /**
   * The Command of the Packet.
   *
   * @return command
   */
  public byte getCommand();

  /**
   * NID of the sending device.
   *
   * @return nid
   */
  public short getSenderNID();

  /**
   * Packet payload.
   * The returned ByteBuffer data cannot be modifed. ByteOrder is Little Endian. position is 0, limit is dlc.
   *
   * @return data (never {@code null})
   */
  public ByteBuffer getData();

  public default <T extends PacketAdapter> T getAdapter(Class<? extends T> clazz)
  {
    return getLookup().lookup(clazz);
  }

}
