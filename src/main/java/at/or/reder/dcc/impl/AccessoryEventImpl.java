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

import at.or.reder.dcc.AccessoryEvent;
import at.or.reder.dcc.Controller;
import org.openide.util.Lookup;

/**
 *
 * @author Wolfgang Reder
 */
public class AccessoryEventImpl implements AccessoryEvent
{

  private final Controller controller;
  private final int senderAddress;
  private final byte port;
  private final short decoder;
  private final int value;

  public AccessoryEventImpl(Controller controller,
                            int senderAddress,
                            short decoder,
                            byte port,
                            int value)
  {
    this.controller = controller;
    this.senderAddress = senderAddress;
    this.port = port;
    this.decoder = decoder;
    this.value = value;
  }

  @Override
  public final short getDeocder()
  {
    return decoder;
  }

  @Override
  public final byte getPort()
  {
    return port;
  }

  @Override
  public final int getValue()
  {
    return value;
  }

  @Override
  public final Controller getController()
  {
    return controller;
  }

  @Override
  public final int getSenderAddress()
  {
    return senderAddress;
  }

  @Override
  public Lookup getLookup()
  {
    return Lookup.EMPTY;
  }

}
