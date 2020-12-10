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

import at.or.reder.dcc.PowerMode;
import at.or.reder.mx1.MX1Command;
import at.or.reder.mx1.MX1Packet;
import at.or.reder.mx1.PowerModePacketAdapter;

/**
 *
 * @author Wolfgang Reder
 */
public class PowerModePacketAdapterImpl extends AbstractPacketAdapter implements PowerModePacketAdapter
{

  public PowerModePacketAdapterImpl(MX1Packet packet)
  {
    super(packet);
    if (packet.getCommand() != MX1Command.TRACK_CONTROL) {
      throw new IllegalArgumentException("packet is not a track control packet");
    }
  }

  @Override
  public PowerMode getPowerMode()
  {
    int mode = packet.getData().get(0) & 0xff;
    if ((mode & 0xc0) != 0) { // DCC or MMC on
      if ((mode & 0x4) != 0) {
        return PowerMode.OVERCURRENT;
      }
      if ((mode & 0x1) != 0) {
        return PowerMode.SSPEM;
      }
      if ((mode & 0x02) != 0) {
        return PowerMode.ON;
      }
      return PowerMode.OFF;
    }
    return PowerMode.PENDING;
  }

}
