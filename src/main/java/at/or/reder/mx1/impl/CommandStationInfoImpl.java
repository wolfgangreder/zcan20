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

import at.or.reder.mx1.CommandStationInfo;
import at.or.reder.mx1.MX1Packet;

public final class CommandStationInfoImpl extends AbstractPacketAdapter implements CommandStationInfo
{

  public CommandStationInfoImpl(MX1Packet packet)
  {
    super(packet);
  }

//  @Override
//  public int getCANAddress()
//  {
//    return packet.getData().getInt(0) & 0xffff;
//  }
  @Override
  public int getDeviceId()
  {
    return packet.getData().get(4) & 0xff;
  }

//  @Override
//  public int getROMPages()
//  {
//    return packet.getData().get(5) & 0xff;
//  }
//  @Override
//  public int getRAMPages()
//  {
//    return packet.getData().get(6) & 0xff;
//  }
//  @Override
//  public int getHWVersionMajor()
//  {
//    return packet.getData().get(7) & 0xff;
//  }
//  @Override
//  public int getHWVersionMinor()
//  {
//    return packet.getData().get(8) & 0xff;
//  }
  @Override
  public int getSWVersionMajor()
  {
    return packet.getData().get(10) & 0xff;
  }

  @Override
  public int getSWVersionMinor()
  {
    return packet.getData().get(11) & 0xff;
  }

//  @Override
//  public LocalDate getReleaseDate()
//  {
//    return LocalDate.now();
//  }
//
//  @Override
//  public BitSet getSwitches()
//  {
//    byte[] bytes = new byte[]{packet.getData().get(11)};
//    return BitSet.valueOf(bytes);
//  }
//
//  @Override
//  public char getPrereleaseVersion()
//  {
//    return (char) (packet.getData().get(12) & 0xff);
//  }
//
//  @Override
//  public int getBootROMVersionMajor()
//  {
//    return packet.getData().get(13) & 0xff;
//  }
//
//  @Override
//  public int getBootROMVersionMinor()
//  {
//    return packet.getData().get(14) & 0xff;
//  }
}
