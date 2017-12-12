/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.reder.zcan20.packet;

/**
 * Interface to specialize Packetdata.
 *
 * @author Wolfgang Reder
 */
public interface PacketAdapter
{

  /**
   * Get the general Packet.
   *
   * @return General Packet or {@code null} if {@code this} is the general Packet.
   */
  public Packet getPacket();

}
