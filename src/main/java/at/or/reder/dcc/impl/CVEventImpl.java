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
import at.or.reder.dcc.util.Predicates;
import java.util.Collection;
import org.openide.util.Lookup;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;

/**
 *
 * @author Wolfgang Reder
 */
public final class CVEventImpl implements CVEvent
{

  private final Controller controller;
  private final int senderAddress;
  private final DecoderClass decoderType;
  private final int decoderAddress;
  private final int cvIndex;
  private final int value;
  private final Lookup lookup;

  public CVEventImpl(Controller controller,
                     short senderAddress,
                     DecoderClass decoderType,
                     short decoderAddress,
                     int cvIndex,
                     int value,
                     Collection<? extends Object> lookupContent)
  {
    this.controller = controller;
    this.senderAddress = senderAddress & 0xffff;
    this.decoderType = decoderType;
    this.decoderAddress = decoderAddress & 0xffff;
    this.cvIndex = cvIndex;
    this.value = value;
    if (lookupContent != null && !lookupContent.isEmpty()) {
      InstanceContent ic = new InstanceContent();
      lookupContent.stream().filter(Predicates::isNotNull).forEach(ic::add);
      lookup = new AbstractLookup(ic);
    } else {
      lookup = Lookup.EMPTY;
    }
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
  public DecoderClass getDecoderType()
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

  @Override
  public Lookup getLookup()
  {
    return lookup;
  }

}
