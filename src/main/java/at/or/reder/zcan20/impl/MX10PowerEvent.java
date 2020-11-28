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
package at.or.reder.zcan20.impl;

import at.or.reder.dcc.Controller;
import at.or.reder.dcc.PowerEvent;
import at.or.reder.dcc.PowerMode;
import at.or.reder.dcc.PowerPort;
import at.or.reder.zcan20.packet.PowerInfoEx;
import org.openide.util.Lookup;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;

/**
 *
 * @author Wolfgang Reder
 */
public final class MX10PowerEvent implements PowerEvent
{

  private final PowerPort port;
  private final PowerMode mode;
  private final Controller controller;
  private final Lookup lookup;
  private final PowerInfoEx powerInfo;

  public MX10PowerEvent(PowerPort port,
                        PowerMode mode,
                        Controller controller,
                        PowerInfoEx powerInfo)
  {
    this.port = port;
    this.mode = mode;
    this.controller = controller;
    this.powerInfo = powerInfo;
    InstanceContent ic = new InstanceContent();
    ic.add(powerInfo);
    lookup = new AbstractLookup(ic);
  }

  @Override
  public PowerPort getPort()
  {
    return port;
  }

  @Override
  public PowerMode getMode()
  {
    return mode;
  }

  @Override
  public Controller getController()
  {
    return controller;
  }

  @Override
  public int getSenderAddress()
  {
    return powerInfo.getSenderNid() & 0xffff;
  }

  @Override
  public float getVoltage()
  {
    if (port != PowerPort.INPUT) {
      return powerInfo.getOutputVoltage(port);
    } else {
      return powerInfo.getInputVoltage();
    }
  }

  @Override
  public float getCurrent()
  {
    if (port != PowerPort.INPUT) {
      return powerInfo.getOutputCurrent(port);
    } else {
      return powerInfo.getInputCurrent();
    }
  }

  @Override
  public Lookup getLookup()
  {
    return lookup;
  }

}
