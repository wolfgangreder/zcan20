/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.reder.zcan20.packet.impl;

import com.reder.zcan20.CommandGroup;
import com.reder.zcan20.CommandMode;
import com.reder.zcan20.packet.Packet;
import com.reder.zcan20.packet.Ping;
import javax.validation.constraints.NotNull;
import com.reder.zcan20.packet.SpecialisationFactory;

/**
 *
 * @author Wolfgang Reder
 */
public final class PingImpl extends AbstractPacketAdapter implements Ping
{

  //@ServiceProvider(service=SpecialisationFactory.class)
  public static final class Factory implements SpecialisationFactory
  {

    @Override
    public boolean isValid(CommandGroup group,
                           int command,
                           CommandMode mode)
    {
      return group == CommandGroup.NETWORK && command == CommandGroup.NETWORK_PING && mode == CommandMode.EVENT;
    }

    @Override
    public Ping createSpecialisation(Packet packet)
    {
      return new PingImpl(packet);
    }

  }

  public PingImpl(@NotNull Packet packet)
  {
    super(packet);
    if (packet.getCommandGroup() != CommandGroup.NETWORK) {
      throw new IllegalArgumentException("illegal commandGroup");
    }
    if (packet.getCommand() != CommandGroup.NETWORK_PING) {
      throw new IllegalArgumentException("illegal command");
    }
    if (packet.getCommandMode() != CommandMode.EVENT) {
      throw new IllegalArgumentException("illegal commandMode");
    }
    if (buffer.capacity() < 8) {
      throw new IllegalArgumentException("illegal datasize");
    }
  }

  @Override
  public int getMasterNID()
  {
    return buffer.getInt(0);
  }

  @Override
  public short getType()
  {
    return buffer.getShort(4);
  }

  @Override
  public short getSession()
  {
    return buffer.getShort(6);
  }

}
