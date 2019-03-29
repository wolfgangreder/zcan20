/*
 * Copyright 2017 Wolfgang Reder.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package at.or.reder.zcan20.impl;

import at.or.reder.zcan20.packet.Packet;
import at.or.reder.zcan20.util.Utils;
import gnu.io.PortInUseException;
import gnu.io.RXTXPort;
import java.io.IOException;
import java.util.logging.Level;

/**
 *
 * @author Wolfgang Reder
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
      Utils.LOGGER.log(Level.SEVERE,
                       null,
                       ex);
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
