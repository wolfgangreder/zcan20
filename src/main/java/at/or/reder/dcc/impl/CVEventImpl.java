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

/**
 *
 * @author Wolfgang Reder
 */
public final class CVEventImpl implements CVEvent
{

  private final Controller controller;
  private final int senderAddress;
  private final DecoderType decoderType;
  private final int decoderAddress;
  private final int cvIndex;
  private final int value;

  public CVEventImpl(Controller controller,
                     int senderAddress,
                     DecoderType decoderType,
                     int decoderAddress,
                     int cvIndex,
                     int value)
  {
    this.controller = controller;
    this.senderAddress = senderAddress;
    this.decoderType = decoderType;
    this.decoderAddress = decoderAddress;
    this.cvIndex = cvIndex;
    this.value = value;
  }

  @Override
  public Controller getController()
  {
    return controller;
  }

  @Override
  public int getSenderAddress()
  {
    return senderAddress;
  }

  @Override
  public DecoderType getDecoderType()
  {
    return decoderType;
  }

  @Override
  public int getDecoderAddress()
  {
    return decoderAddress;
  }

  @Override
  public int getCVIndex()
  {
    return cvIndex;
  }

  @Override
  public int getValue()
  {
    return value;
  }

}
