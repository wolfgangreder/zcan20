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

import at.or.reder.dcc.cv.EnumeratedValue;
import at.or.reder.zcan20.util.XmlDescripted;
import at.or.reder.zcan20.util.XmlIntAdapter;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 *
 * @author Wolfgang Reder
 */
public final class XmlEnumeratedValue extends XmlDescripted
{

  public static final class Adapter extends XmlAdapter<XmlEnumeratedValue, EnumeratedValue>
  {

    @Override
    public EnumeratedValue unmarshal(XmlEnumeratedValue v)
    {
      return v != null ? v.toEnumeratedValue() : null;
    }

    @Override
    public XmlEnumeratedValue marshal(EnumeratedValue v)
    {
      return v != null ? new XmlEnumeratedValue(v) : null;
    }

  }
  private int value;

  XmlEnumeratedValue()
  {
  }

  XmlEnumeratedValue(EnumeratedValue v)
  {
    super(v.getAllResourceDescriptions());
  }

  public EnumeratedValue toEnumeratedValue()
  {
    return new EnumeratedValueImpl(value,
                                   toMap());
  }

  @XmlAttribute(name = "value")
  @XmlJavaTypeAdapter(XmlIntAdapter.class)
  public Integer getValue()
  {
    return value;
  }

  public void setValue(Integer value)
  {
    this.value = value != null ? value : 0;
  }

}
