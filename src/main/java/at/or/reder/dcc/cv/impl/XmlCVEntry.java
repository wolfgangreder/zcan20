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
import at.or.reder.dcc.cv.CVEntry;
import at.or.reder.dcc.cv.CVFlag;
import at.or.reder.dcc.cv.CVType;
import at.or.reder.dcc.cv.CVUtils;
import at.or.reder.dcc.cv.CVValue;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlList;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 *
 * @author Wolfgang Reder
 */
public final class XmlCVEntry
{

  public static final class XmlBankAddress
  {

    @XmlAttribute(name = "bank")
    private CVType type;
    @XmlAttribute(name = "address")
    private int address;

    public XmlBankAddress()
    {
    }

    public XmlBankAddress(Map.Entry<CVType, Integer> e)
    {
      type = e.getKey();
      address = e.getValue();
    }

  }

  public static final class XmlBankAddressAdapter extends XmlAdapter<String, XmlBankAddress>
  {

    public static final Pattern pattern = Pattern.compile("\\A([0-3])=(\\d+)\\z");

    @Override
    @SuppressWarnings("UseSpecificCatch")
    public XmlBankAddress unmarshal(String v)
    {
      if (v != null) {
        Matcher m = pattern.matcher(v);
        if (m.matches()) {
          try {
            int index = Integer.parseInt(m.group(1));
            int address = Integer.parseInt(m.group(2));
            XmlBankAddress result = new XmlBankAddress();
            result.address = address;
            switch (index) {
              case 0:
                result.type = CVType.BANKREGISTER_0;
                break;
              case 1:
                result.type = CVType.BANKREGISTER_1;
                break;
              case 2:
                result.type = CVType.BANKREGISTER_2;
                break;
              case 3:
                result.type = CVType.BANKREGISTER_3;
                break;
              default:
                return null;
            }
            return result;
          } catch (Throwable th) {
            // Sollte eingenlich nicht passieren, aber vorsicht ...
          }
        }
      }
      return null;
    }

    @Override
    public String marshal(XmlBankAddress v)
    {
      if (v != null) {
        StringBuilder builder = new StringBuilder();
        switch (v.type) {
          case BANKREGISTER_0:
            builder.append("0=");
            break;
          case BANKREGISTER_1:
            builder.append("1=");
            break;
          case BANKREGISTER_2:
            builder.append("2=");
            break;
          case BANKREGISTER_3:
            builder.append("3=");
            break;
          default:
            return null;
        }
        builder.append(Integer.toString(v.address));
        return builder.toString();
      }
      return null;
    }

  }

  private CVType type;
  private String name;
  private String description;
  private final Set<CVFlag> flags = EnumSet.noneOf(CVFlag.class);
  private int defaultValue = 0;
  private int rangeMin = 0;
  private int rangeMax = 255;
  private final Set<Integer> allowedValues = new HashSet<>();
  private int valueMask = 0xff;
  private final List<CVBitDescriptor> descriptors = new ArrayList<>();
  private CVValue value = CVValue.NO_VALUE;
  private int address = 0;
  private final List<XmlBankAddress> bankAddresses;

  public XmlCVEntry()
  {
    bankAddresses = new ArrayList<>();
  }

  public XmlCVEntry(CVEntry e)
  {
    type = e.getCVType();
    name = e.getName();
    description = e.getDescription();
    flags.addAll(e.getFlags());
    defaultValue = e.getDefaultValue();
    rangeMax = e.getRangeMax();
    rangeMin = e.getRangeMin();
    allowedValues.addAll(e.getAllowedValues());
    valueMask = e.getValueMask();
    descriptors.addAll(e.getBitDescriptors());
    value = e.getValue();
    address = e.getAddress();
    bankAddresses = e.getBankAddresses().entrySet().stream().
            filter((me) -> CVUtils.isBankAddress(me.getKey())).
            map(XmlBankAddress::new).
            collect(Collectors.toList());
  }

  @XmlAttribute(name = "type", required = true)
  public CVType getCVType()
  {
    return type;
  }

  public void setCVType(CVType type)
  {
    this.type = type != null ? type : CVType.NUMERIC;
  }

  @XmlAttribute(name = "name")
  public String getName()
  {
    return name;
  }

  public void setName(String name)
  {
    this.name = name;
  }

  @XmlElement(name = "description")
  public String getDescription()
  {
    return description;
  }

  public void setDescription(String desc)
  {
    description = desc;
  }

  @XmlElement(name = "flags")
  @XmlList
  public Set<CVFlag> getFlags()
  {
    return flags;
  }

  @XmlAttribute(name = "default-value")
  public int getDefaultValue()
  {
    return defaultValue;
  }

  public void setDefaultValue(int defaultValue)
  {
    this.defaultValue = defaultValue;
  }

  @XmlAttribute(name = "range-min")
  public int getRangeMin()
  {
    return rangeMin;
  }

  public void setRangeMin(int rangeMin)
  {
    this.rangeMin = rangeMin;
  }

  @XmlAttribute(name = "range-max")
  public int getRangeMax()
  {
    return rangeMax;
  }

  public void setRangeMax(int rangeMax)
  {
    this.rangeMax = rangeMax;
  }

  @XmlElement(name = "allowed-values")
  @XmlList
  public Set<Integer> getAllowedValues()
  {
    return allowedValues;
  }

  @XmlAttribute(name = "valuemask")
  public int getValueMask()
  {
    return valueMask;
  }

  public void setValueMask(int valueMask)
  {
    this.valueMask = valueMask;
  }

  @XmlElement(name = "bit-descriptor")
  @XmlElementWrapper(name = "bit-descriptors")
  @XmlJavaTypeAdapter(XmlCVBitDescriptor.Adapter.class)
  public List<CVBitDescriptor> getBitDescriptors()
  {
    return descriptors;
  }

  @XmlElement(name = "value")
  @XmlJavaTypeAdapter(XmlCVValue.Adapter.class)
  public CVValue getValue()
  {
    return value;
  }

  public void setValue(CVValue value)
  {
    this.value = value != null ? value : CVValue.NO_VALUE;
  }

  @XmlElement(name = "address", required = true)
  public int getAddress()
  {
    return address;
  }

  public void setAddress(int address)
  {
    this.address = address;
  }

  @XmlElement(name = "bank-address")
  @XmlList
  public List<XmlBankAddress> getBankAddresses()
  {
    return bankAddresses;
  }

}
