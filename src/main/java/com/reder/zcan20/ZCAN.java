/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.reder.zcan20;

import java.io.IOException;
import java.util.function.BiConsumer;
import org.openide.util.Lookup;

/**
 *
 * @author reder
 */
public interface ZCAN extends AutoCloseable, NetworkControl, SystemControl, TrackConfig, LocoManagement, Lookup.Provider
{

  @Override
  public void close() throws IOException;

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
