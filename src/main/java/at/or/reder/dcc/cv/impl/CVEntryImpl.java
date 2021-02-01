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
import at.or.reder.dcc.util.AbstractDescripted;
import at.or.reder.dcc.util.Localizable;
import at.or.reder.dcc.util.ResourceDescription;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;

/**
 *
 * @author Wolfgang Reder
 */
@NbBundle.Messages({"# {0} - address",
                    "CVEntryImpl_defaultName_simple=CV #{0,number,0}",
                    "# {0} - address",
                    "# {1} - bankAddress",
                    "CVEntryImpl_defaultName_bank=CV #{1}:{0,number,0}"})
final class CVEntryImpl extends AbstractDescripted implements CVEntry
{

  private final CVType type;
  private final Set<CVFlag> flags;
  private final int defaultValue;
  private final int rangeMin;
  private final int rangeMax;
  private final int valueMask;
  private final List<CVBitDescriptor> bitDescriptors;
  private final int address;
  private final long flatAddress;
  private final Map<CVType, Integer> bankAddresses;

  public CVEntryImpl(CVType type,
                     Localizable<ResourceDescription> descriptions,
                     Set<CVFlag> flags,
                     int defaultValue,
                     int rangeMin,
                     int rangeMax,
                     int valueMask,
                     Collection<? extends CVBitDescriptor> bitDescriptors,
                     int address,
                     Map<CVType, Integer> bankAddresses)
  {
    super(descriptions,
          null);
    this.type = type;
    if (flags == null || flags.isEmpty()) {
      this.flags = Collections.emptySet();
    } else {
      this.flags = Collections.unmodifiableSet(EnumSet.copyOf(flags));
    }
    this.valueMask = valueMask;
    this.rangeMin = Math.min(rangeMax & valueMask,
                             rangeMin & valueMask);
    this.rangeMax = Math.max(rangeMax & valueMask,
                             rangeMin & valueMask);
    this.defaultValue = Math.min(rangeMax,
                                 Math.max(rangeMin,
                                          defaultValue & valueMask));
    if (bitDescriptors.isEmpty()) {
      this.bitDescriptors = Collections.emptyList();
    } else {
      List<CVBitDescriptor> tmp = new ArrayList<>(bitDescriptors);
      tmp.sort(Comparator.comparing(CVBitDescriptor::getDefaultValue));
      this.bitDescriptors = Collections.unmodifiableList(tmp);
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
    flatAddress = buildFlatAddress();
  }

  @Override
  public String getDefaultName()
  {
    if (bankAddresses.isEmpty()) {
      return Bundle.CVEntryImpl_defaultName_simple(address);
    } else {
      String ba = bankAddresses.entrySet().stream().
              filter((f) -> f.getValue() != null && CVUtils.isBankAddress(f.getKey())).
              sorted(Comparator.comparing(Map.Entry::getKey)).
              map((e) -> Integer.toString(e.getValue())).
              collect(Collectors.joining(",",
                                         "[",
                                         "]"));
      return Bundle.CVEntryImpl_defaultName_bank(address,
                                                 ba);
    }
  }

  private long buildFlatAddress()
  {
    long result = 0;
    int b = getBankAddress(CVType.INDEX_3);
    if (b != -1) {
      result += b & 0xff;
    }
    result <<= 8;
    b = getBankAddress(CVType.INDEX_2);
    if (b != -1) {
      result += b & 0xff;
    }
    result <<= 8;
    b = getBankAddress(CVType.INDEX_1);
    if (b != -1) {
      result += b & 0xff;
    }
    result <<= 8;
    b = getBankAddress(CVType.INDEX_0);
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
    return "CVEntryImpl{" + "name=" + getName() + ", address=" + address + '}';
  }

}
