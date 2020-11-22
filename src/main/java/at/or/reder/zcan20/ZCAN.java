/*
 * Copyright 2017-2020 Wolfgang Reder.
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

import at.or.reder.dcc.LinkState;
import at.or.reder.dcc.LinkStateListener;
import at.or.reder.zcan20.packet.Packet;
import at.or.reder.zcan20.packet.PacketBuilder;
import java.io.IOException;
import java.util.function.Predicate;
import java.util.logging.Logger;
import org.openide.util.Lookup;

public interface ZCAN extends AutoCloseable, Lookup.Provider
{

  public static final int NUM_FUNCTION = 29;
  public static final Logger LOGGER = Logger.getLogger("at.or.reder.zcan20");

  public LinkState getLinkState();

  @Override
  public void close() throws IOException;

  public PacketBuilder createPacketBuilder();

  public short getNID();

  public void addLinkStateListener(LinkStateListener listener);

  public void removeLinkStateListener(LinkStateListener listener);

  public void addPacketListener(PacketListener packetListener);

  public void removePacketListener(PacketListener packetListener);

  public void addPacketListener(CommandGroup group,
                                PacketListener packetListener);

  public void removePacketListener(CommandGroup group,
                                   PacketListener packetListener);

  public void addPacketListener(Predicate<? super Packet> matcher,
                                PacketListener packetListener);

  public void removePacketListener(Predicate<? super Packet> matcher,
                                   PacketListener packetListener);

  @Override
  public default Lookup getLookup()
  {
    return Lookup.EMPTY;
  }

}
