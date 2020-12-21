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
import at.or.reder.mx1.LocoInfo;
import java.time.LocalDateTime;

public final class LocoInfoRecord
{

  private static final class MyLocoInfo implements LocoInfo
  {

    private final int address;
    private final int speed;
    private final Direction direction;
    private final SpeedstepSystem speedSystem;
    private final int flags;
    private final int function;
    private final int azbz;
    private final int state;

    public MyLocoInfo(int address,
                      int speed,
                      Direction direction,
                      SpeedstepSystem speedSystem,
                      int flags,
                      int function,
                      int azbz,
                      int state)
    {
      this.address = address;
      this.speed = speed;
      this.direction = direction;
      this.speedSystem = speedSystem;
      this.flags = flags;
      this.function = function;
      this.azbz = azbz;
      this.state = state;
    }

    @Override
    public int getError()
    {
      return 0;
    }

    @Override
    public int getAddress()
    {
      return address;
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

    @Override
    public SpeedstepSystem getSpeedstepSystem()
    {
      return speedSystem;
    }

    @Override
    public int getFlags()
    {
      return flags;
    }

    @Override
    public int getFunctions()
    {
      return function;
    }

    @Override
    public int getAZBZ()
    {
      return azbz;
    }

    @Override
    public int getStatus()
    {
      return state;
    }

    @Override
    public String toString()
    {
      return "MyLocoInfo{" + "address=" + address + ", speed=" + speed + ", direction=" + direction + ", speedSystem=" + speedSystem + ", flags=" + flags + ", function=" + function + ", azbz=" + azbz + ", state=" + state + '}';
    }

  }
  private final int address;
  private LocalDateTime lastRead;
  private Direction direction;
  private int speed;
  private int flags;
  private int functions;
  private int azbz;
  private int state;

  public LocoInfoRecord(int address)
  {
    this.address = address;
    lastRead = null;
  }

  public LocoInfo toLocoInfo()
  {
    return new MyLocoInfo(address,
                          speed,
                          direction,
                          SpeedstepSystem.SPEED_14,
                          flags,
                          functions,
                          azbz,
                          state);
  }

  public boolean update(LocoInfo li)
  {
    if (li != null) {
      if (li.getAddress() != address) {
        throw new IllegalArgumentException("address does not match");
      }
      if (li.getError() != 0) {
        return false;
      }
      speed = li.getSpeed();
      flags = li.getFlags();
      direction = li.getDirection();
      functions = li.getFunctions();
      lastRead = LocalDateTime.now();
      azbz = li.getAZBZ();
      state = li.getStatus();
    }
    return true;
  }

  public int getAddress()
  {
    return address;
  }

  public LocalDateTime getLastRead()
  {
    return lastRead;
  }

  public int getSpeed()
  {
    return speed;
  }

  public boolean isMANSet()
  {
    return (flags & 0x80) != 0;
  }

  public Direction getDirection()
  {
    return direction;
  }

  public SpeedstepSystem getSpeedstepSystem()
  {
    int magic = (flags & 0xc) >> 2;
    return SpeedstepSystem.valueOfMagic(magic);
  }

  public boolean isFunctionSet(int iFunction)
  {
    if (iFunction == 0) {
      return (flags & 0x10) != 0;
    } else if (iFunction > 0 && iFunction < 13) {
      int mask = 1 << iFunction;
      return (functions & mask) != 0;
    }
    return false;
  }

  public int getFunctions()
  {
    return functions;
  }

  public int getFlags()
  {
    return flags;
  }

  public int getAZBZ()
  {
    return azbz;
  }

}
