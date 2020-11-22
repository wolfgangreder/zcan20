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
import at.or.reder.dcc.LocomotiveSpeedEvent;
import at.or.reder.dcc.impl.LocomotiveEventImpl;
import at.or.reder.zcan20.LocoSpeed;
import at.or.reder.zcan20.SpeedFlags;
import java.util.Set;
import org.openide.util.lookup.InstanceContent;

/**
 *
 * @author Wolfgang Reder
 */
public final class MX10LocoSpeedEventImpl extends LocomotiveEventImpl implements LocomotiveSpeedEvent
{

  private final int speed;
  private final Direction direction;

  public MX10LocoSpeedEventImpl(Controller controller,
                                Locomotive locomotive,
                                int sender,
                                LocoSpeed packet)
  {
    super(controller,
          locomotive,
          sender,
          packet.getLocoID(),
          createInstanceContent(packet));
    speed = packet.getSpeed();
    Set<SpeedFlags> flags = packet.getFlags();
    if (flags.contains(SpeedFlags.FORWARD_FROM_SYSTEM)) {
      direction = Direction.FORWARD;
    } else {
      direction = Direction.REVERSE;
    }
  }

  private static InstanceContent createInstanceContent(LocoSpeed packet)
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
  public Direction getDirection()
  {
    return direction;
  }

}
