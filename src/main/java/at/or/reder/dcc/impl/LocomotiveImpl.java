/*
 * Copyright 2019 Wolfgang Reder.
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
package at.or.reder.dcc.impl;

import at.or.reder.dcc.Controller;
import at.or.reder.dcc.Direction;
import at.or.reder.dcc.Locomotive;
import at.or.reder.zcan20.Loco;
import java.io.IOException;
import java.util.BitSet;
import java.util.concurrent.TimeoutException;

/**
 *
 * @author Wolfgang Reder
 */
public final class LocomotiveImpl implements Locomotive
{

  private final Loco loco;
  private final Controller controller;

  public LocomotiveImpl(Loco loco,
                        Controller controller)
  {
    this.loco = loco;
    this.controller = controller;
  }

  @Override
  public void close() throws IOException
  {
    loco.close();
  }

  @Override
  public int getAddress()
  {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public int getCurrentSpeed()
  {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public Direction getDirection()
  {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public BitSet getFunctions()
  {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public byte readCV(int cvIndex,
                     int timeout) throws IOException, TimeoutException
  {
    return loco.readCV(cvIndex,
                       timeout);
  }

  @Override
  public void clearCV() throws IOException
  {
    loco.clearCV();
  }

}
