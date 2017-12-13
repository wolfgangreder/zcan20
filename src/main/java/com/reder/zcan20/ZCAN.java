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
 */package com.reder.zcan20;

import com.reder.zcan20.packet.PacketBuilder;
import java.io.IOException;
import java.util.function.BiConsumer;
import org.openide.util.Lookup;

public interface ZCAN extends AutoCloseable, NetworkControl, SystemControl, TrackConfig, LocoManagement, Lookup.Provider
{

  @Override
  public void close() throws IOException;

  public PacketBuilder createPacketBuilder();

  public short getNID();

  public void addLinkStateListener(BiConsumer<ZCAN, LinkState> listener);

  public void removeLinkStateListener(BiConsumer<ZCAN, LinkState> listener);

  public void addPacketListener(PacketListener packetListener);

  public void removePacketListener(PacketListener packetListener);

  public void addPacketListener(CommandGroup group,
                                PacketListener packetListener);

  public void removePacketListener(CommandGroup group,
                                   PacketListener packetListener);

  public default <T> T getExtension(Class<? extends T> clazz)
  {
    return null;
  }

}
