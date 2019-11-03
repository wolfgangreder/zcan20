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
package at.or.reder.zcan20.impl;

import at.or.reder.dcc.PowerPort;
import at.or.reder.dcc.util.CanIdMatcher;
import at.or.reder.zcan20.CanId;
import at.or.reder.zcan20.CommandGroup;
import at.or.reder.zcan20.CommandMode;
import at.or.reder.zcan20.TrackConfig;
import at.or.reder.zcan20.packet.CVInfoAdapter;
import at.or.reder.zcan20.packet.Packet;
import at.or.reder.zcan20.packet.PacketBuilder;
import at.or.reder.zcan20.packet.TSETrackModePacketAdapter;
import java.io.IOException;
import java.util.concurrent.TimeoutException;
import java.util.function.Predicate;

/**
 *
 * @author Wolfgang Reder
 */
final class TrackConfigImpl implements TrackConfig
{

  private final ZCANImpl zcan;

  public TrackConfigImpl(ZCANImpl zcan)
  {
    this.zcan = zcan;
  }

  @Override
  public void requestPowerPortMode(PowerPort port) throws IOException
  {
    Packet packet = zcan.createPacketBuilder().buildQueryTSEPortModePacket(zcan.getMasterNID(),
                                                                           port);
    zcan.doSendPacket(packet);
  }

  @Override
  public TSETrackModePacketAdapter getPowerPortMode(PowerPort port,
                                                    long timeout) throws IOException, TimeoutException
  {
    Packet packet = zcan.createPacketBuilder().buildQueryTSEPortModePacket(zcan.getMasterNID(),
                                                                           port);
    return zcan.sendReceive(packet,
                            TSETrackModePacketAdapter.SELECTOR::matches,
                            TSETrackModePacketAdapter.class,
                            timeout);
  }

  @Override
  public TSETrackModePacketAdapter enterPowerMode(PowerPort port,
                                                  byte mode,
                                                  long timeout) throws IOException, TimeoutException
  {
    PacketBuilder builder = zcan.createPacketBuilder();
    Packet packet = builder.buildSetTSEPowerModePacket(zcan.getMasterNID(),
                                                       port,
                                                       mode);
    return zcan.sendReceive(packet,
                            TSETrackModePacketAdapter.SELECTOR::matches,
                            TSETrackModePacketAdapter.class,
                            timeout);
  }

  @Override
  public void readCV(short address,
                     int cv) throws IOException
  {
    PacketBuilder builder = zcan.createPacketBuilder();
    Packet packet = builder.buildReadCVPacket(zcan.getMasterNID(),
                                              address,
                                              cv);
    zcan.doSendPacket(packet);
  }

  @Override
  public CVInfoAdapter readCV(short address,
                              int cv,
                              long timeout,
                              Predicate<? super Packet> packetMatcher) throws IOException
  {
    short masterNID = zcan.getMasterNID();
    Packet packet = zcan.createPacketBuilder().
            buildReadCVPacket(masterNID,
                              address,
                              cv);
    return zcan.sendReceive(packet,
                            CVInfoAdapter.SELECTOR::matches,
                            CVInfoAdapter.class,
                            timeout);
  }

  @Override
  public void writeCV(short address,
                      int cv,
                      short value) throws IOException
  {
    Packet packet = zcan.createPacketBuilder().
            buildWriteCVPacket(zcan.getMasterNID(),
                               address,
                               cv,
                               value);
    zcan.doSendPacket(packet);
  }

  @Override
  public CVInfoAdapter writeCV(short address,
                               int cv,
                               short value,
                               long timeout) throws IOException
  {
    short masterNID = zcan.getMasterNID();
    Packet packet = zcan.createPacketBuilder().
            buildWriteCVPacket(masterNID,
                               address,
                               cv,
                               value);
    return zcan.sendReceive(packet,
                            new CanIdMatcher(CanId.valueOf(CommandGroup.TRACK_CONFIG_PRIVATE,
                                                           CommandGroup.TSE_PROG_WRITE,
                                                           CommandMode.ACK,
                                                           masterNID),
                                             CanIdMatcher.MASK_NO_ADDRESS & (~CanIdMatcher.MASK_COMMAND)),
                            CVInfoAdapter.class,
                            timeout);
  }

}
