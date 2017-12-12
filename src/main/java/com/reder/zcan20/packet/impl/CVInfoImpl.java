/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.reder.zcan20.packet.impl;

import com.reder.zcan20.CommandGroup;
import com.reder.zcan20.CommandMode;
import com.reder.zcan20.packet.Packet;
import com.reder.zcan20.packet.SpecialisationFactory;
import org.openide.util.lookup.ServiceProvider;
import com.reder.zcan20.packet.CVInfoAdapter;

/**
 *
 * @author Wolfgang Reder
 */
public final class CVInfoImpl extends AbstractPacketAdapter implements CVInfoAdapter
{

  @ServiceProvider(service = SpecialisationFactory.class, path = Packet.LOOKUPPATH)
  public static final class Factory implements SpecialisationFactory
  {

    @Override
    public boolean isValid(CommandGroup group,
                           int command,
                           CommandMode mode)
    {
      return group == CommandGroup.TRACK_CONFIG_PUBLIC && (mode == CommandMode.ACK || mode == CommandMode.EVENT);
    }

    @Override
    public Object createSpecialisation(Packet packet)
    {
      return new CVInfoImpl(packet);
    }

  }

  public CVInfoImpl(Packet packet)
  {
    super(packet);
  }

  @Override
  public int getNumber()
  {
    return buffer.getInt(6) & 0xffff_ffff;
  }

  @Override
  public int getValue()
  {
    return buffer.getShort(10) & 0xffff;
  }

  @Override
  public short getDecoderAddress()
  {
    return buffer.getShort(4);
  }

}
