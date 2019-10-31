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

import at.or.reder.dcc.cv.CVBitDescriptor;
import at.or.reder.dcc.cv.CVBitDescriptorBuilder;
import at.or.reder.zcan20.util.XmlDescripted;
import at.or.reder.zcan20.util.XmlIntAdapter;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 *
 * @author Wolfgang Reder
 */
public final class XmlCVBitDescriptor extends XmlDescripted
{

  public static final class Adapter extends XmlAdapter<XmlCVBitDescriptor, CVBitDescriptor>
  {

    @Override
    public CVBitDescriptor unmarshal(XmlCVBitDescriptor v)
    {
      if (v != null) {
        return v.toBitDescriptor();
      }
      return null;
    }

    @Override
    public XmlCVBitDescriptor marshal(CVBitDescriptor v)
    {
      if (v != null) {
        return new XmlCVBitDescriptor(v);
      }
      return null;
    }

  }

  private int bitMask = 0xff;
  private int defaultValue;
  private int min;
  private int max;
  private final List<XmlEnumeratedValue> allowedValues = new ArrayList<>();

  public XmlCVBitDescriptor()
  {
  }

  public XmlCVBitDescriptor(CVBitDescriptor d)
  {
    super(d.getAllResourceDescriptions());
    bitMask = d.getBitMask() & 0xff;
    defaultValue = d.getDefaultValue() & bitMask;
    d.getAllowedValues().stream().filter((f) -> f != null).map(XmlEnumeratedValue::new).forEach(allowedValues::add);
    min = d.getMinValue();
    max = d.getMaxValue();
  }

  public CVBitDescriptor toBitDescriptor()
  {
    CVBitDescriptorBuilder builder = new CVBitDescriptorBuilderImpl();
    allowedValues.stream().map(XmlEnumeratedValue::toEnumeratedValue).forEach(builder::addAllowedValue);
    builder.bitMask(bitMask);
    builder.defaultValue(defaultValue);
    builder.minValue(min);
    builder.maxValue(max);
    builder.addDescriptions(toMap());
    return builder.build();
  }

  @XmlAttribute(name = "bitmask")
  @XmlJavaTypeAdapter(XmlIntAdapter.class)
  public Integer getBitMask()
  {
    return bitMask;
  }

  public void setBitMask(Integer bm)
  {
    this.bitMask = bm != null ? bm & 0xff : 0xff;
  }

  @XmlAttribute(name = "defaultvalue")
  @XmlJavaTypeAdapter(XmlIntAdapter.class)
  public Integer getDefaultValue()
  {
    return defaultValue & bitMask;
  }

  public void setDefaultValue(Integer defaultValue)
  {
    this.defaultValue = defaultValue != null ? defaultValue & 0xff : 0;
  }

  @XmlAttribute(name = "range-min")
  @XmlJavaTypeAdapter(XmlIntAdapter.class)
  public Integer getMinValue()
  {
    return min;
  }

  public void setMinValue(Integer min)
  {
    this.min = min != null ? min & bitMask : 0;
  }

  @XmlAttribute(name = "range-max")
  @XmlJavaTypeAdapter(XmlIntAdapter.class)
  public Integer getMaxValue()
  {
    return max;
  }

  public void setMaxValue(Integer max)
  {
    this.max = (max != null ? max : 0xff) & bitMask;
  }

  @XmlElement(name = "allowed-values")
  public List<XmlEnumeratedValue> getAllowedValues()
  {
    return allowedValues;
  }

}
