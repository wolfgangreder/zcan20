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
import at.or.reder.zcan20.ZCAN;
import java.io.IOException;
import java.util.Map;
import java.util.SortedMap;
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
                        Controller controller) throws IOException
  {
    this.loco = loco;
    this.controller = controller;
    loco.scanFunctions();
  }

  @Override
  public Controller getController()
  {
    return controller;
  }

  @Override
  public void close() throws IOException
  {
    loco.close();
  }

  @Override
  public boolean isOwner()
  {
    return loco.isOwner();
  }

  @Override
  public void takeOwnership() throws IOException
  {
    loco.setOwner(true);
  }

  @Override
  public int getAddress()
  {
    return loco.getLoco();
  }

  @Override
  public Integer getCurrentSpeed()
  {
    return loco.getSpeed();
  }

  @Override
  public Direction getDirection()
  {
    return loco.getDirection();
  }

  @Override
  public void control(Direction dir,
                      int speed) throws IOException
  {
    short s = (short) (Math.min(1023,
                                speed) & 0x3ff);
    loco.control(dir,
                 s);
  }

  @Override
  public SortedMap<Integer, Integer> getFunctions()
  {
    return loco.getAllFunctions();
  }

  @Override
  public void setFunctions(Map<Integer, Integer> functions) throws IOException
  {
    for (Map.Entry<Integer, Integer> e : functions.entrySet()) {
      if (e.getValue() != null && e.getKey() != null && e.getKey() > 0 && e.getValue() < ZCAN.NUM_FUNCTION) {
        loco.setFunction(e.getKey(),
                         e.getValue());
      }
    }
  }

  @Override
  public void toggleFunction(int iFunction) throws IOException
  {
    if (iFunction < 0 || iFunction >= ZCAN.NUM_FUNCTION) {
      throw new IndexOutOfBoundsException();
    }
    Integer i = loco.getFunction(iFunction);
    boolean val = !(i != null && i != 0);
    loco.setFunction(iFunction,
                     val ? 1 : 0);
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

  @Override
  public String toString()
  {
    return "Decoder " + loco.getLoco() + "@" + controller;
  }

}
