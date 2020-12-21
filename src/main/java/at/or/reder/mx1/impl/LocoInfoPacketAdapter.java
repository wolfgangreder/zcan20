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

import at.or.reder.dcc.Direction;
import at.or.reder.dcc.SpeedstepSystem;
import at.or.reder.mx1.LocoInfoAdpater;
import at.or.reder.mx1.MX1Command;
import at.or.reder.mx1.MX1Packet;

public final class LocoInfoPacketAdapter extends AbstractPacketAdapter implements LocoInfoAdpater
{

  private final int dataOffset;

  public LocoInfoPacketAdapter(MX1Packet packet)
  {
    super(packet);
    if (packet.getCommand() == MX1Command.QUERY_CS_LOCO) {
      dataOffset = 2;
    } else {
      dataOffset = 0;
    }
  }

  @Override
  public int getError()
  {
    if (dataOffset > 0) {
      return getPacket().getData().get(1) & 0xff;
    }
    return 0;
  }

  @Override
  public int getAddress()
  {
    return getPacket().getData().getShort(dataOffset) & 0x3fff;
  }

  @Override
  public int getSpeed()
  {
    SpeedstepSystem ss = getSpeedstepSystem();
    int speed = getPacket().getData().get(dataOffset + 2) & 0xff;
    return ss.systemToNormalized(speed);
  }

  @Override
  public Direction getDirection()
  {
    return ((getFlags() & 0x10) != 0) ? Direction.REVERSE : Direction.FORWARD;
  }

  @Override
  public SpeedstepSystem getSpeedstepSystem()
  {
    return SpeedstepSystem.valueOfMagic((getFlags() & 0xc));
  }

  @Override
  public int getFlags()
  {
    return getPacket().getData().get(dataOffset + 3) & 0xff;
  }

  @Override
  public int getFunctions()
  {
    return getPacket().getData().getShort(dataOffset + 4) & 0x3ff;
  }

  @Override
  public int getAZBZ()
  {
    return getPacket().getData().get(dataOffset + 6) & 0xff;
  }

  @Override
  public int getStatus()
  {
    return getPacket().getData().get(dataOffset + 7) & 0xff;
  }

  @Override
  public String toString()
  {
    return "LocoInfoPacketAdapter(address=" + getAddress() + ", speed=" + getSpeed()
           + " steps=" + getSpeedstepSystem() + " f0=" + ((getFlags() & 0x10) != 0
                                                          ? "on"
                                                          : "off") + " functions=" + Integer.
            toUnsignedString(
                    getFunctions(),
                    2);
  }

}
