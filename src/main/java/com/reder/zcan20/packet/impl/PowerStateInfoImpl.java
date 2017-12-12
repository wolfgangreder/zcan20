/*
 * Copyright 2017 Wolfgang Reder.
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
package com.reder.zcan20.packet.impl;

import com.reder.zcan20.CommandGroup;
import com.reder.zcan20.CommandMode;
import com.reder.zcan20.PowerMode;
import com.reder.zcan20.PowerOutput;
import com.reder.zcan20.packet.Packet;
import com.reder.zcan20.packet.PowerStateInfo;
import com.reder.zcan20.packet.SpecialisationFactory;

/**
 *
 * @author Wolfgang Reder
 */
public final class PowerStateInfoImpl extends AbstractPacketAdapter implements PowerStateInfo
{

  //@ServiceProvider(service = SpecialisationFactory.class)
  public static final class Factory implements SpecialisationFactory
  {

    @Override
    public boolean isValid(CommandGroup group,
                           int command,
                           CommandMode mode)
    {
      return group == CommandGroup.SYSTEM && command == CommandGroup.SYSTEM_POWER && mode != CommandMode.REQUEST;
    }

    @Override
    public Object createSpecialisation(Packet packet)
    {
      return new PowerStateInfoImpl(packet);
    }

  }
  private final int offset;

  public PowerStateInfoImpl(Packet packet)
  {
    super(packet);
    if (buffer.capacity() < 4) {
      throw new IllegalArgumentException("invalid dlc");
    }
    if (buffer.capacity() == 4) {
      offset = 0;
    } else {
      offset = 2;
    }
  }

  @Override
  public int getSystemNID()
  {
    return buffer.getShort(offset) & 0xffff;
  }

  @Override
  public PowerOutput getOutput()
  {
    return PowerOutput.valueOfMagic(buffer.get(offset + 2) & 0xff);
  }

  @Override
  public PowerMode getMode()
  {
    return PowerMode.valueOfMagic(buffer.get(offset + 3) & 0xff);
  }

}
