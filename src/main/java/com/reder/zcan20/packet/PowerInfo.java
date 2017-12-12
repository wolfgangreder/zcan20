/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.reder.zcan20.packet;

import com.reder.zcan20.PowerOutput;
import com.reder.zcan20.PowerState;
import java.util.Set;

/**
 *
 * @author reder
 */
public interface PowerInfo extends PacketAdapter
{

  public PowerOutput getOutput();

  public Set<PowerState> getState();

  public default float getVoltage()
  {
    return Float.NaN;
  }

  public default float getCurrent()
  {
    return Float.NaN;
  }

}
