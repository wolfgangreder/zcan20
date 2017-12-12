/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.reder.zcan20;

/**
 *
 * @author Wolfgang Reder
 */
public interface LocoMode
{

  public boolean isUsed();

  public int getLocoAddress();

  public SpeedSteps getSpeedSteps();

  public Protocol getProtocol();

  public int getFunctionCount();

  public boolean isPulsFx();

  public boolean isAnalogFx();

  public SpeedlimitMode getSpeedLimitMode();

}
