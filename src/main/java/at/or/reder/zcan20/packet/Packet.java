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
package at.or.reder.zcan20.packet;

import at.or.reder.zcan20.CanId;
import at.or.reder.zcan20.CommandGroup;
import at.or.reder.zcan20.CommandMode;
import java.nio.ByteBuffer;
import org.openide.util.Lookup;

/**
 * Represents a general Packet. Datapecialisation can be obtained via {@link #getAdapter(java.lang.Class) }.
 *
 * @author reder
 */
public interface Packet extends PacketAdapter, Lookup.Provider
{

  public static final String LOOKUPPATH = "com/reder/zcan20/adapter";

  /**
   * Returns the Id field of the can packet.
   *
   * @return id field of this packet
   */
  public CanId getCanId();

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
   * Packet payload. The returned ByteBuffer data cannot be modifed. ByteOrder is Little Endian. position is 0, limit is dlc.
   *
   * @return data (never {@code null})
   */
  public ByteBuffer getData();

  public default <T extends PacketAdapter> T getAdapter(Class<? extends T> clazz)
  {
    return getLookup().lookup(clazz);
  }

}
