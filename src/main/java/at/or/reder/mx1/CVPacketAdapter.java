/*
 * Copyright 2020 Wolfgang Reder.
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
package at.or.reder.mx1;

import at.or.reder.dcc.TrackProtocol;

/**
 *
 * @author Wolfgang Reder
 */
public interface CVPacketAdapter extends MX1PacketAdapter
{

  public TrackProtocol getTrackProtocol();

  public int getAddress();

  public int getCV();

  public int getValue();

  public static boolean isValidPacket(MX1Packet packet)
  {
    return packet.getCommand() == MX1Command.RW_DECODER_CV;
  }

}
