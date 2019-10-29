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
import at.or.reder.dcc.cv.ResourceDescription;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlList;
import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 *
 * @author Wolfgang Reder
 */
public final class XmlCVBitDescriptor
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

  public static final class MappedResourceDescriptor extends XmlResourceDescriptor
  {

    private int value;

    public MappedResourceDescriptor()
    {
    }

    public MappedResourceDescriptor(int value,
                                    Locale loc,
                                    String name,
                                    String desc)
    {
      super(loc,
            name,
            desc);
      this.value = value;
    }

    @XmlAttribute(name = "value", required = true)
    public int getValue()
    {
      return value;
    }

    public void setValue(int value)
    {
      this.value = value;
    }

  }
  private int bitMask;
  private int defaultValue;
  private Set<Integer> allowedValues = new HashSet<>();
  private List<MappedResourceDescriptor> descriptors = new ArrayList<>();

  public XmlCVBitDescriptor()
  {
  }

  public XmlCVBitDescriptor(CVBitDescriptor d)
  {
    bitMask = d.getBitMask() & 0xff;
    defaultValue = d.getDefaultValue() & bitMask;
    allowedValues.addAll(d.getAllowedValues());
    for (Map.Entry<Locale, Map<Integer, ResourceDescription>> le : d.getAllValueDescriptions().entrySet()) {
      Locale loc = le.getKey();
      for (Map.Entry<Integer, ResourceDescription> ve : le.getValue().entrySet()) {
        descriptors.add(new MappedResourceDescriptor(ve.getKey(),
                                                     loc,
                                                     ve.getValue().getName(),
                                                     ve.getValue().getDescrption()));
      }
    }
  }

  public CVBitDescriptor toBitDescriptor()
  {
    CVBitDescriptorBuilder builder = new CVBitDescriptorBuilderImpl();
    builder.addAllowedValues(allowedValues);
    builder.bitMask(bitMask);
    builder.defaultValue(defaultValue);
    for (MappedResourceDescriptor rd : descriptors) {
      Locale loc = null;
      if (rd.getLoc() != null) {
        loc = Locale.forLanguageTag(rd.getLoc());
      }
      builder.addValueDescription(loc,
                                  rd.getValue(),
                                  rd.toResourceDescription());
    }
    return builder.build();
  }

  @XmlAttribute(name = "bitmask")
  public int getBitMask()
  {
    return bitMask;
  }

  public void setBitMask(int bm)
  {
    this.bitMask = bm & 0xff;
  }

  @XmlAttribute(name = "default-value")
  public int getDefaultValue()
  {
    return defaultValue & bitMask;
  }

  public void setDefaultValue(int defaultValue)
  {
    this.defaultValue = defaultValue & 0xff;
  }

  @XmlElement(name = "allowed-values")
  @XmlList
  public Set<Integer> getAllowedValues()
  {
    return allowedValues;
  }

  @XmlElement(name = "value-descriptor")
  @XmlElementWrapper(name = "value-descriptors")
  public List<MappedResourceDescriptor> getDescriptors()
  {
    return descriptors;
  }

}
