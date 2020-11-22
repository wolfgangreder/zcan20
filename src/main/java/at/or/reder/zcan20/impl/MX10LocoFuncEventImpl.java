/*
 * Copyright 2020 Wolfgang Reder.
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
import at.or.reder.dcc.Locomotive;
import at.or.reder.dcc.LocomotiveFuncEvent;
import at.or.reder.dcc.impl.LocomotiveEventImpl;
import at.or.reder.zcan20.LocoFunc;
import org.openide.util.lookup.InstanceContent;

/**
 *
 * @author Wolfgang Reder
 */
public final class MX10LocoFuncEventImpl extends LocomotiveEventImpl implements LocomotiveFuncEvent
{

  private final short nr;
  private final short val;

  public MX10LocoFuncEventImpl(Controller controller,
                               Locomotive locomotive,
                               int sender,
                               LocoFunc packet)
  {
    super(controller,
          locomotive,
          sender,
          packet.getLocoID(),
          createInstanceContent(packet));
    nr = packet.getFxNumber();
    val = packet.getFxValue();
  }

  private static InstanceContent createInstanceContent(LocoFunc packet)
  {
    InstanceContent ic = new InstanceContent();
    ic.add(packet);
    return ic;
  }

  @Override
  public short getFuncNr()
  {
    return nr;
  }

  @Override
  public short getFuncValue()
  {
    return val;
  }

}
