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
import com.reder.zcan20.PowerOutput;
import com.reder.zcan20.PowerState;
import com.reder.zcan20.packet.Packet;
import com.reder.zcan20.packet.PacketAdapterFactory;
import com.reder.zcan20.packet.PowerInfo;
import java.util.Set;
import javax.validation.constraints.NotNull;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Wolfgang Reder
 */
final class PowerInfoImpl extends AbstractPacketAdapter implements PowerInfo
{

  @ServiceProvider(service = PacketAdapterFactory.class, path = Packet.LOOKUPPATH)
  public static final class Factory implements PacketAdapterFactory
  {

    @Override
    public boolean isValid(CommandGroup group,
                           int command,
                           CommandMode mode)
    {
      return group == CommandGroup.CONFIG && command == CommandGroup.CONFIG_POWER_INFO && (mode == CommandMode.EVENT
                                                                                           || mode == CommandMode.ACK);
    }

    @Override
    public PowerInfo createAdapter(Packet packet)
    {
      return new PowerInfoImpl(packet);
    }

  }

  private PowerInfoImpl(@NotNull Packet packet)
  {
    super(packet);
    if (packet.getCommandGroup() != CommandGroup.CONFIG) {
      throw new IllegalArgumentException("illegal commandGroup");
    }
    if (packet.getCommand() != CommandGroup.CONFIG_POWER_INFO) {
      throw new IllegalArgumentException("illegal command");
    }
    if (packet.getCommandMode() != CommandMode.ACK && packet.getCommandMode() != CommandMode.EVENT) {
      throw new IllegalArgumentException("illegal commandMode");
    }
    if (buffer.capacity() < 8) {
      throw new IllegalArgumentException("illegal data size");
    }
  }

  @Override
  public PowerOutput getOutput()
  {
    switch (buffer.get(2)) {
      case 1:
        return PowerOutput.OUT_1;
      case 2:
        return PowerOutput.OUT_2;
      case 4:
        return PowerOutput.BOOSTER;
    }
    return PowerOutput.valueOfMagic(buffer.get(2) & 0xff);
  }

  @Override
  public Set<PowerState> getState()
  {
    return PowerState.toSet(buffer.getShort(2) & 0xffff);
  }

  @Override
  public float getVoltage()
  {
    float tmp = buffer.getShort(6);
    return tmp / 1000;
  }

  @Override
  public float getCurrent()
  {
    float tmp = buffer.getShort(4);
    return tmp / 1000;
  }

}
