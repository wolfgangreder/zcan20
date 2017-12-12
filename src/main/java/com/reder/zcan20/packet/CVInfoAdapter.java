/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.reder.zcan20.packet;

import com.reder.zcan20.packet.PacketAdapter;

/**
 *
 * @author reder
 */
public interface CVInfoAdapter extends PacketAdapter
{

  public int getNumber();

  public int getValue();

  public short getDecoderAddress();

}
