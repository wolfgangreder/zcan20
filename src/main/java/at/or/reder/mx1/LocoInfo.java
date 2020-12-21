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

import at.or.reder.dcc.Direction;
import at.or.reder.dcc.SpeedstepSystem;

public interface LocoInfo
{

  public int getError();

  public int getAddress();

  public int getSpeed();

  public default Direction getDirection()
  {
    return ((getFlags() & 0x10) != 0) ? Direction.REVERSE : Direction.FORWARD;
  }

  public default SpeedstepSystem getSpeedstepSystem()
  {
    return SpeedstepSystem.valueOfMagic((getFlags() & 0xc) >> 2);
  }

  public int getFlags();

  public int getFunctions();

  public int getAZBZ();

  public int getStatus();

}
