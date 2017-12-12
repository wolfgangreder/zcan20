/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.reder.zcan20.impl;

import com.reder.zcan20.packet.Packet;
import gnu.io.PortInUseException;
import gnu.io.RXTXPort;
import java.io.IOException;
import org.openide.util.Exceptions;

/**
 *
 * @author reder
 */
public final class VCOMPort implements ZPort
{

  private final String port;

  public VCOMPort(String port)
  {
    this.port = port;
    try {
      RXTXPort p = new RXTXPort(port);
    } catch (PortInUseException ex) {
      Exceptions.printStackTrace(ex);
    }
  }

  @Override
  public String getName()
  {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public void start() throws IOException
  {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public void close() throws IOException
  {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public void sendPacket(Packet packet) throws IOException
  {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public Packet readPacket() throws IOException
  {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

}
