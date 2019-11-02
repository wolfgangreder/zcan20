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

import at.or.reder.dcc.cv.CVEntry;
import at.or.reder.dcc.cv.CVEntryBuilder;
import at.or.reder.dcc.cv.CVFlag;
import at.or.reder.dcc.cv.CVType;
import at.or.reder.dcc.cv.CVUtils;
import at.or.reder.dcc.util.XmlDescripted;
import at.or.reder.dcc.util.XmlIntAdapter;
import at.or.reder.dcc.util.XmlResourceDescriptor;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlList;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 *
 * @author Wolfgang Reder
 */
public final class XmlCVEntry extends XmlDescripted
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
                result.type = CVType.INDEX_0;
                break;
              case 1:
                result.type = CVType.INDEX_1;
                break;
              case 2:
                result.type = CVType.INDEX_2;
                break;
              case 3:
                result.type = CVType.INDEX_3;
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
          case INDEX_0:
            builder.append("0=");
            break;
          case INDEX_1:
            builder.append("1=");
            break;
          case INDEX_2:
            builder.append("2=");
            break;
          case INDEX_3:
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

  public static final class Adapter extends XmlAdapter<XmlCVEntry, CVEntry>
  {

    @Override
    public CVEntry unmarshal(XmlCVEntry v)
    {
      if (v != null) {
        return v.toCVEntry();
      }
      return null;
    }

    @Override
    public XmlCVEntry marshal(CVEntry v)
    {
      if (v != null) {
        return new XmlCVEntry(v);
      }
      return null;
    }

  }
  private CVType type;
  private final List<XmlResourceDescriptor> descriptors = new ArrayList<>();
  private final Set<CVFlag> flags = EnumSet.noneOf(CVFlag.class);
  private int defaultValue = 0;
  private int rangeMin = 0;
  private int rangeMax = 255;
  private int valueMask = 0xff;
  private final List<XmlCVBitDescriptor> bitDescriptors = new ArrayList<>();
  private int address = 0;
  private final List<XmlBankAddress> bankAddresses;

  public XmlCVEntry()
  {
    bankAddresses = new ArrayList<>();
  }

  public XmlCVEntry(CVEntry e)
  {
    super(e.getAllResourceDescriptions());
    type = e.getCVType();
    flags.addAll(e.getFlags());
    defaultValue = e.getDefaultValue();
    rangeMax = e.getRangeMax();
    rangeMin = e.getRangeMin();
    valueMask = e.getValueMask();
    e.getBitDescriptors().
            stream().
            filter((b) -> b != null).
            map(XmlCVBitDescriptor::new).
            forEach(bitDescriptors::add);
    address = e.getAddress();
    bankAddresses = e.getBankAddresses().entrySet().stream().
            filter((me) -> CVUtils.isBankAddress(me.getKey())).
            map(XmlBankAddress::new).
            collect(Collectors.toList());
  }

  public CVEntry toCVEntry()
  {
    CVEntryBuilder builder = new CVEntryBuilderImpl();
    builder.address(address);
    bitDescriptors.stream().
            filter((d) -> d != null).
            map(XmlCVBitDescriptor::toBitDescriptor).
            forEach(builder::addBitDescriptor);
    flags.stream().forEach(builder::addFlag);
    builder.defaultValue(defaultValue);
    builder.rangeMax(rangeMax);
    builder.rangeMin(rangeMin);
    builder.type(type);
    builder.valueMask(valueMask);
    builder.addDescriptions(toMap());
    return builder.build();
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

  @XmlElement(name = "flags")
  @XmlList
  public Set<CVFlag> getFlags()
  {
    return flags;
  }

  @XmlAttribute(name = "defaultvalue")
  @XmlJavaTypeAdapter(XmlIntAdapter.class)
  public Integer getDefaultValue()
  {
    return defaultValue & valueMask;
  }

  public void setDefaultValue(Integer defaultValue)
  {
    this.defaultValue = defaultValue != null ? defaultValue : 0;
  }

  @XmlAttribute(name = "range-min")
  @XmlJavaTypeAdapter(XmlIntAdapter.class)
  public Integer getRangeMin()
  {
    return rangeMin & valueMask;
  }

  public void setRangeMin(Integer rangeMin)
  {
    this.rangeMin = rangeMin != null ? rangeMin : 0;
  }

  @XmlAttribute(name = "range-max")
  @XmlJavaTypeAdapter(XmlIntAdapter.class)
  public Integer getRangeMax()
  {
    return rangeMax;
  }

  public void setRangeMax(Integer rangeMax)
  {
    this.rangeMax = rangeMax != null ? rangeMax : 0xff;
  }

  @XmlAttribute(name = "valuemask")
  @XmlJavaTypeAdapter(XmlIntAdapter.class)
  public Integer getValueMask()
  {
    return valueMask & 0xff;
  }

  public void setValueMask(Integer valueMask)
  {
    this.valueMask = valueMask != null ? valueMask & 0xff : 0xff;
  }

  @XmlElement(name = "bit-descriptor")
  public List<XmlCVBitDescriptor> getBitDescriptors()
  {
    return bitDescriptors;
  }

  @XmlAttribute(name = "address", required = true)
  @XmlJavaTypeAdapter(XmlIntAdapter.class)
  public Integer getAddress()
  {
    return address;
  }

  public void setAddress(Integer address)
  {
    this.address = address;
  }

  @XmlElement(name = "index-address")
  @XmlJavaTypeAdapter(XmlBankAddressAdapter.class)
  @XmlList
  public List<XmlBankAddress> getBankAddresses()
  {
    return bankAddresses;
  }

}
