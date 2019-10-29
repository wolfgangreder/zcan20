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

import at.or.reder.dcc.cv.CVAddress;
import at.or.reder.dcc.cv.CVBitDescriptor;
import at.or.reder.dcc.cv.CVEntry;
import at.or.reder.dcc.cv.CVFlag;
import at.or.reder.dcc.cv.CVType;
import at.or.reder.dcc.cv.CVUtils;
import at.or.reder.dcc.cv.CVValue;
import at.or.reder.dcc.cv.ResourceDescription;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import org.openide.util.Lookup;

/**
 *
 * @author Wolfgang Reder
 */
final class CVEntryImpl implements CVEntry
{

  private final CVType type;
  private final Map<Locale, ResourceDescription> descriptions;
  private final Set<CVFlag> flags;
  private final int defaultValue;
  private final int rangeMin;
  private final int rangeMax;
  private final Set<Integer> allowedValues;
  private final int valueMask;
  private final List<CVBitDescriptor> bitDescriptors;
  private final CVValue value;
  private final int address;
  private final long flatAddress;
  private final Map<CVType, Integer> bankAddresses;

  public CVEntryImpl(CVType type,
                     Map<Locale, ResourceDescription> descriptions,
                     Set<CVFlag> flags,
                     int defaultValue,
                     int rangeMin,
                     int rangeMax,
                     Set<Integer> allowedValues,
                     int valueMask,
                     List<CVBitDescriptor> bitDescriptors,
                     CVValue value,
                     int address,
                     Map<CVType, Integer> bankAddresses)
  {
    this.type = type;
    if (flags == null || flags.isEmpty()) {
      this.flags = Collections.emptySet();
    } else {
      this.flags = Collections.unmodifiableSet(EnumSet.copyOf(flags));
    }
    this.defaultValue = defaultValue;
    this.rangeMin = rangeMin;
    this.rangeMax = rangeMax;
    this.allowedValues = allowedValues;
    this.valueMask = valueMask;
    if (bitDescriptors.isEmpty()) {
      this.bitDescriptors = Collections.emptyList();
      if (type == CVType.BITFIELD) {
        throw new IllegalArgumentException("CVEntry is a bitfield but contains no bitdescriptors");
      }
    } else {
      this.bitDescriptors = Collections.unmodifiableList(new ArrayList<>(bitDescriptors));
    }
    if (value != null) {
      this.value = new CVValueImpl(value.getState(),
                                   value.getValue(),
                                   this);
    } else {
      this.value = CVValue.NO_VALUE;
    }
    this.address = address;
    if (bankAddresses == null || bankAddresses.isEmpty()) {
      this.bankAddresses = Collections.emptyMap();
    } else {
      Map<CVType, Integer> tmp = bankAddresses.entrySet().stream().
              filter((e) -> CVUtils.isBankAddress(e.getKey())).
              collect(Collectors.toUnmodifiableMap(Map.Entry::getKey,
                                                   Map.Entry::getValue));
      if (tmp.isEmpty()) {
        this.bankAddresses = Collections.emptyMap();
      } else {
        this.bankAddresses = tmp;
      }
    }
    if (descriptions == null || descriptions.isEmpty()) {
      this.descriptions = Collections.emptyMap();
    } else {
      Map<Locale, ResourceDescription> tmp = new HashMap<>(descriptions);
      if (tmp.get(null) == null) {
        tmp.put(null,
                buildDefaultResourceDescription());
      }
      this.descriptions = Collections.unmodifiableMap(tmp);
    }
    flatAddress = buildFlatAddress();
  }

  private long buildFlatAddress()
  {
    long result = 0;
    int b = getBankAddress(CVType.BANKREGISTER_3);
    if (b != -1) {
      result += b & 0xff;
    }
    result <<= 8;
    b = getBankAddress(CVType.BANKREGISTER_2);
    if (b != -1) {
      result += b & 0xff;
    }
    result <<= 8;
    b = getBankAddress(CVType.BANKREGISTER_1);
    if (b != -1) {
      result += b & 0xff;
    }
    result <<= 8;
    b = getBankAddress(CVType.BANKREGISTER_0);
    if (b != -1) {
      result += b & 0xff;
    }
    result <<= 16;
    return result + (address & 0xffff);
  }

  @Override
  public long getFlatAddress()
  {
    return flatAddress;
  }

  @Override
  public CVType getCVType()
  {
    return type;
  }

  @Override
  public Map<Locale, ResourceDescription> getAllDescriptions()
  {
    return descriptions;
  }

  @Override
  public ResourceDescription getDefaultDescription()
  {
    return descriptions.get(null);
  }

  @Override
  public Set<CVFlag> getFlags()
  {
    return flags;
  }

  @Override
  public int getDefaultValue()
  {
    return defaultValue;
  }

  @Override
  public int getRangeMin()
  {
    return rangeMin;
  }

  @Override
  public int getRangeMax()
  {
    return rangeMax;
  }

  @Override
  public Set<Integer> getAllowedValues()
  {
    return allowedValues;
  }

  @Override
  public int getValueMask()
  {
    return valueMask;
  }

  @Override
  public List<CVBitDescriptor> getBitDescriptors()
  {
    return bitDescriptors;
  }

  @Override
  public CVValue getValue()
  {
    return value;
  }

  @Override
  public Lookup getLookup()
  {
    return Lookup.EMPTY;
  }

  @Override
  public int getAddress()
  {
    return address;
  }

  @Override
  public int getBankAddress(CVType type)
  {
    if (!CVUtils.isBankAddress(type)) {
      return -1;
    }
    return bankAddresses.getOrDefault(type,
                                      -1);
  }

  @Override
  public Map<CVType, Integer> getBankAddresses()
  {
    return bankAddresses;
  }

  @Override
  public int hashCode()
  {
    int hash = 5;
    hash = 89 * hash + this.address;
    hash = 89 * hash + Objects.hashCode(this.bankAddresses);
    return hash;
  }

  @Override
  public boolean equals(Object obj)
  {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof CVAddress)) {
      return false;
    }
    final CVAddress other = (CVAddress) obj;
    if (this.address != other.getAddress()) {
      return false;
    }
    return Objects.equals(this.bankAddresses,
                          other.getBankAddresses());
  }

  @Override
  public String toString()
  {
    return "CVEntryImpl{" + "name=" + getDefaultDescription().getName() + ", address=" + address + '}';
  }

}
