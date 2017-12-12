/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.reder.zcan20.packet;

import com.reder.zcan20.PowerMode;
import com.reder.zcan20.PowerOutput;

/**
 *
 * @author Wolfgang Reder
 */
public interface PowerStateInfo extends PacketAdapter
{

  public int getSystemNID();

  public PowerOutput getOutput();

  public PowerMode getMode();

}
