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
package at.or.reder.dcc.cv.impl;

import at.or.reder.dcc.cv.CVValue;
import at.or.reder.dcc.cv.CVValueState;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 *
 * @author Wolfgang Reder
 */
public final class XmlCVValue
{

  public static final class Adapter extends XmlAdapter<XmlCVValue, CVValue>
  {

    @Override
    public CVValue unmarshal(XmlCVValue v)
    {
      if (v != null) {
        return new CVValueImpl(v.state,
                               v.value,
                               null);
      } else {
        return null;
      }
    }

    @Override
    public XmlCVValue marshal(CVValue v)
    {
      if (v != null) {
        return new XmlCVValue(v);
      } else {
        return null;
      }
    }

  }
  private int value;
  private CVValueState state;

  public XmlCVValue()
  {
  }

  public XmlCVValue(CVValue v)
  {
    value = v.getValue();
    state = v.getState();
    if (state == null) {
      state = CVValueState.UNKNOWN;
    }
  }

  @XmlAttribute(name = "state", required = true)
  public CVValueState getState()
  {
    return state;
  }

  public void setState(CVValueState state)
  {
    this.state = state != null ? state : CVValueState.UNKNOWN;
  }

  @XmlAttribute(name = "value", required = true)
  public int getValue()
  {
    return value & 0xff;
  }

  public void setValue(int value)
  {
    this.value = value & 0xff;
  }

}
