/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.reder.zcan20.packet.impl;

import com.reder.zcan20.PowerOutput;
import com.reder.zcan20.PowerState;
import com.reder.zcan20.CommandGroup;
import com.reder.zcan20.CommandMode;
import com.reder.zcan20.packet.Packet;
import com.reder.zcan20.packet.PowerInfo;
import java.util.Set;
import javax.validation.constraints.NotNull;
import com.reder.zcan20.packet.SpecialisationFactory;

/**
 *
 * @author Wolfgang Reder
 */
public final class PowerInfoImpl extends AbstractPacketAdapter implements PowerInfo
{

  //@ServiceProvider(service=SpecialisationFactory.class)
  public static final class Factory implements SpecialisationFactory
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
    public PowerInfo createSpecialisation(Packet packet)
    {
      return new PowerInfoImpl(packet);
    }

  }

  public PowerInfoImpl(@NotNull Packet packet)
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
