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
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
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
  private final String name;
  private final String description;
  private final Set<CVFlag> flags;
  private final int defaultValue;
  private final int rangeMin;
  private final int rangeMax;
  private final Set<Integer> allowedValues;
  private final int valueMask;
  private final List<CVBitDescriptor> descriptors;
  private final CVValue value;
  private final int address;
  private final Map<CVType, Integer> bankAddresses;

  public CVEntryImpl(CVType type,
                     String name,
                     String description,
                     Set<CVFlag> flags,
                     int defaultValue,
                     int rangeMin,
                     int rangeMax,
                     Set<Integer> allowedValues,
                     int valueMask,
                     List<CVBitDescriptor> descriptors,
                     CVValue value,
                     int address,
                     Map<CVType, Integer> bankAddresses)
  {
    this.type = type;
    this.name = name;
    if (flags == null || flags.isEmpty()) {
      this.flags = Collections.emptySet();
    } else {
      this.flags = Collections.unmodifiableSet(EnumSet.copyOf(flags));
    }
    this.description = description;
    this.defaultValue = defaultValue;
    this.rangeMin = rangeMin;
    this.rangeMax = rangeMax;
    this.allowedValues = allowedValues;
    this.valueMask = valueMask;
    if (description.isEmpty()) {
      this.descriptors = Collections.emptyList();
      if (type == CVType.BITFIELD) {
        throw new IllegalArgumentException("CVEntry is a bitfield but contains no bitdescriptors");
      }
    } else {
      this.descriptors = Collections.unmodifiableList(new ArrayList<>(descriptors));
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
  }

  @Override
  public CVType getCVType()
  {
    return type;
  }

  @Override
  public String getName()
  {
    return name;
  }

  @Override
  public String getDescription()
  {
    return description;
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
    return descriptors;
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
    return "CVEntryImpl{" + "name=" + name + ", address=" + address + '}';
  }

}
