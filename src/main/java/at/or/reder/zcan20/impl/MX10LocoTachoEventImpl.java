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
package at.or.reder.zcan20.impl;

import at.or.reder.dcc.Controller;
import at.or.reder.dcc.Direction;
import at.or.reder.dcc.Locomotive;
import at.or.reder.dcc.LocomotiveTachoEvent;
import at.or.reder.dcc.impl.LocomotiveEventImpl;
import at.or.reder.zcan20.LocoDirection;
import at.or.reder.zcan20.LocoSpeed;
import at.or.reder.zcan20.LocoVoltage;
import at.or.reder.zcan20.packet.LocoTachoPacketAdapter;
import org.openide.util.lookup.InstanceContent;

public final class MX10LocoTachoEventImpl extends LocomotiveEventImpl implements LocomotiveTachoEvent
{

  private final int speed;
  private final boolean speedSet;
  private final Direction dir;
  private final boolean pendingDir;
  private final boolean dirSet;
  private final boolean voltageSet;
  private final float voltage;

  public MX10LocoTachoEventImpl(Controller controller,
                                Locomotive locomotive,
                                int sender,
                                LocoTachoPacketAdapter packet)
  {
    super(controller,
          locomotive,
          sender,
          packet.getDecoderId(),
          createInstanceContent(packet));
    speedSet = packet instanceof LocoSpeed;
    dirSet = packet instanceof LocoDirection;
    voltageSet = packet instanceof LocoVoltage;
    int tmpSpeed = -1;
    Direction tmpDir = null;
    boolean tmpPendingDir = false;
    float tmpVoltage = -1;
    if (speedSet) {
      tmpSpeed = ((LocoSpeed) packet).getSpeed();
    }
    if (dirSet) {
      tmpDir = ((LocoDirection) packet).getDirection();
      tmpPendingDir = ((LocoDirection) packet).isDirectionPending();
    }
    if (voltageSet) {
      tmpVoltage = ((LocoVoltage) packet).getVoltage();
    }
    speed = tmpSpeed;
    dir = tmpDir;
    pendingDir = tmpPendingDir;
    voltage = tmpVoltage;
  }

  private static InstanceContent createInstanceContent(LocoTachoPacketAdapter packet)
  {
    InstanceContent ic = new InstanceContent();
    ic.add(packet);
    return ic;
  }

  @Override
  public int getSpeed()
  {
    return speed;
  }

  @Override
  public boolean isSpeedSet()
  {
    return speedSet;
  }

  @Override
  public boolean isDirectionSet()
  {
    return dirSet;
  }

  @Override
  public Direction getDirection()
  {
    return dir;
  }

  @Override
  public boolean isDirectionPending()
  {
    return pendingDir;
  }

  @Override
  public boolean isVoltageSet()
  {
    return voltageSet;
  }

  @Override
  public float getVoltage()
  {
    return voltage;
  }

}
