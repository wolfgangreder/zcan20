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

import at.or.reder.dcc.PowerPort;
import at.or.reder.zcan20.packet.CVInfoAdapter;
import at.or.reder.zcan20.packet.Packet;
import at.or.reder.zcan20.packet.TSETrackModePacketAdapter;
import java.io.IOException;
import java.util.concurrent.TimeoutException;
import java.util.function.Predicate;

public interface TrackConfig
{

  public void requestPowerPortMode(PowerPort port) throws IOException;

  public TSETrackModePacketAdapter getPowerPortMode(PowerPort port,
                                                    long timeout) throws IOException, TimeoutException;

  public TSETrackModePacketAdapter enterPowerMode(PowerPort port,
                                                  byte mode,
                                                  long timeout) throws IOException, TimeoutException;

  public void readCV(short address,
                     int cv) throws IOException;

  public default CVInfoAdapter readCV(short address,
                                      int cv,
                                      long timeout) throws IOException
  {
    return readCV(address,
                  cv,
                  timeout,
                  (p) -> true);
  }

  public CVInfoAdapter readCV(short address,
                              int cv,
                              long timeout,
                              Predicate<? super Packet> finishPredicate) throws IOException;

  public void writeCV(short address,
                      int cv,
                      short value) throws IOException;

  public CVInfoAdapter writeCV(short address,
                               int cv,
                               short value,
                               long timeout) throws IOException;

}
