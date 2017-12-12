/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.reder.zcan20.impl;

import com.reder.zcan20.packet.Packet;
import java.io.IOException;
import javax.validation.constraints.NotNull;

/**
 *
 * @author reder
 */
public interface ZPort extends AutoCloseable
{

  public String getName();

  public void start() throws IOException;

  @Override
  public void close() throws IOException;

  public void sendPacket(@NotNull Packet packet) throws IOException;

  public Packet readPacket() throws IOException;

}
