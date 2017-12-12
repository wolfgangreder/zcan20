/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.reder.zcan20.packet;

/**
 *
 * @author Wolfgang Reder
 */
public interface Ping extends PacketAdapter
{

  public int getMasterNID();

  public short getType();

  public short getSession();

}
