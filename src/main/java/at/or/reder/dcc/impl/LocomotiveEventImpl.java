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
import org.openide.util.Lookup;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author Wolfgang Reder
 */
public abstract class LocomotiveEventImpl implements LocomotiveEvent
{

  private final Controller controller;
  private final int sender;
  private final Lookup lookup;
  private final short decoder;

  protected LocomotiveEventImpl(Controller controller,
                                Locomotive locomotive,
                                int sender,
                                short locoAddress,
                                InstanceContent ic)
  {
    this.controller = controller;
    this.sender = sender;
    this.decoder = locoAddress;
    if (ic != null) {
      if (locomotive != null) {
        ic.add(locomotive);
      }
      this.lookup = new AbstractLookup(ic);
    } else if (locomotive != null) {
      this.lookup = Lookups.singleton(locomotive);
    } else {
      this.lookup = Lookup.EMPTY;
    }
  }

  @Override
  public final Controller getController()
  {
    return controller;
  }

  @Override
  public final int getSenderAddress()
  {
    return sender;
  }

  @Override
  public short getDecoder()
  {
    return decoder;
  }

  @Override
  public final Lookup getLookup()
  {
    return lookup;
  }

}
