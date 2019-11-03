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

import at.or.reder.dcc.*;
import java.util.BitSet;
import org.openide.util.Lookup;

/**
 *
 * @author Wolfgang Reder
 */
public final class LocomotiveEventImpl implements LocomotiveEvent
{

  private final Controller controller;
  private final Locomotive locomotive;
  private final int sender;

  public LocomotiveEventImpl(Controller controller,
                             Locomotive locomotive,
                             int sender)
  {
    this.controller = controller;
    this.locomotive = locomotive;
    this.sender = sender;
  }

  @Override
  public Controller getController()
  {
    return controller;
  }

  @Override
  public int getSenderAddress()
  {
    return sender;
  }

  @Override
  public int getAddress()
  {
    return locomotive.getAddress();
  }

  @Override
  public int getCurrentSpeed()
  {
    return locomotive.getCurrentSpeed();
  }

  @Override
  public Direction getDirection()
  {
    return locomotive.getDirection();
  }

  @Override
  public BitSet getFunctions()
  {
    return locomotive.getFunctions();
  }

  @Override
  public Lookup getLookup()
  {
    return Lookup.EMPTY;
  }

}
