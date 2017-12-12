/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.reder.zcan20.packet.impl;

import com.reder.zcan20.PowerMode;
import com.reder.zcan20.PowerOutput;
import com.reder.zcan20.CommandGroup;
import com.reder.zcan20.CommandMode;
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
