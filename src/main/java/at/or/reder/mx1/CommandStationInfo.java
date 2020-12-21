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

/**
 *
 * @author Wolfgang Reder
 */
public interface CommandStationInfo extends MX1PacketAdapter
{

//  public int getCANAddress();
  public int getDeviceId();

//  public int getROMPages();
//
//  public default int getROMBytes()
//  {
//    return 32 * 1024 * getROMPages();
//  }
//
//  public int getRAMPages();
//
//  public default int getRAMBytes()
//  {
//    return 32 * 1024 * getRAMPages();
//  }
//
//  public int getHWVersionMajor();
//
//  public int getHWVersionMinor();
  public int getSWVersionMajor();

  public int getSWVersionMinor();

//  public LocalDate getReleaseDate();
//
//  public BitSet getSwitches();
//
//  public char getPrereleaseVersion();
//
//  public int getBootROMVersionMajor();
//
//  public int getBootROMVersionMinor();
}
