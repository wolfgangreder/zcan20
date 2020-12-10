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
package at.or.reder.mx1.impl;

import at.or.reder.dcc.TrackProtocol;
import at.or.reder.mx1.CVPacketAdapter;
import at.or.reder.mx1.MX1Command;
import at.or.reder.mx1.MX1Packet;
import at.or.reder.mx1.MX1PacketFlags;

/**
 *
 * @author Wolfgang Reder
 */
final class CVPacketAdapterImpl extends AbstractPacketAdapter implements CVPacketAdapter
{

  CVPacketAdapterImpl(MX1Packet packet)
  {
    super(packet);
    if (packet.getCommand() != MX1Command.RW_DECODER_CV) {
      throw new IllegalArgumentException("Invalid packet command");
    }
  }

  private boolean hasData()
  {
    return getPacket().getFlags().contains(MX1PacketFlags.REPLY);
  }

  @Override
  public TrackProtocol getTrackProtocol()
  {
    if (hasData()) {
      int address = getAddress();
      int val = getPacket().getData().get(4) & 0xc0;
      if (address != 0) {
        switch (val) {
          case 0x80:
          case 0xc0:
            return TrackProtocol.DCC;
          case 0x00:
          case 0x40:
          default:
            return TrackProtocol.UNKNOWN;
        }
      } else {
        switch (val) {
          case 0x80:
            return TrackProtocol.DCC;
          case 0x40:
            return TrackProtocol.MMC;
          case 0x00:
          case 0xc0:
          default:
            return TrackProtocol.UNKNOWN;
        }
      }
    } else {
      return TrackProtocol.UNKNOWN;
    }
  }

  @Override
  public int getAddress()
  {
    if (hasData()) {
      return getPacket().getData().getShort(1) & 0x3fff;
    }
    return -1;
  }

  @Override
  public int getCV()
  {
    if (hasData()) {
      return getPacket().getData().getShort(3) & 0xffff;
    } else {
      return -1;
    }
  }

  @Override
  public int getValue()
  {
    if (hasData()) {
      return getPacket().getData().get(5) & 0xff;
    } else {
      return -1;
    }
  }

  @Override
  public String toString()
  {
    if (hasData()) {
      return "CVPacketAdapterImpl without data";
    } else {
      return "CVPacketAdapterImpl for CV " + getCV();
    }
  }

}
