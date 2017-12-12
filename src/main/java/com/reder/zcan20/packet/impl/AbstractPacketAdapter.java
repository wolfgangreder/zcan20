/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.reder.zcan20.packet.impl;

import com.reder.zcan20.packet.Packet;
import com.reder.zcan20.packet.PacketAdapter;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Objects;

/**
 *
 * @author Wolfgang Reder
 */
public abstract class AbstractPacketAdapter implements PacketAdapter
{

  private final Packet packet;
  protected final ByteBuffer buffer;

  protected AbstractPacketAdapter(Packet packet)
  {
    this.packet = Objects.requireNonNull(packet,
                                         "packet is null");
    this.buffer = packet.getData().order(ByteOrder.LITTLE_ENDIAN);
  }

  @Override
  public Packet getPacket()
  {
    return packet;
  }

}
