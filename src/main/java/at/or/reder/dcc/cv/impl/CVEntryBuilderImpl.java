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
import at.or.reder.dcc.cv.CVEntryBuilder;
import at.or.reder.dcc.cv.CVFlag;
import at.or.reder.dcc.cv.CVType;
import at.or.reder.dcc.util.AbstractDescriptedBuilder;
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 *
 * @author Wolfgang Reder
 */
public final class CVEntryBuilderImpl extends AbstractDescriptedBuilder<CVEntryBuilder> implements CVEntryBuilder
{

  private CVType type = CVType.NUMERIC;
  private final Set<CVFlag> flags = EnumSet.noneOf(CVFlag.class);
  private int defaultValue = 0;
  private int rangeMin = 0;
  private int rangeMax = 255;
  private int valueMask = 0xff;
  private final Set<CVBitDescriptor> descriptors = new HashSet<>();
  private int address = 0;
  private final Map<CVType, Integer> bankAddresses = new HashMap<>();

  @Override
  public CVEntryBuilder copy(CVEntry entry)
  {
    type = Objects.requireNonNull(entry,
                                  "entry is null").getCVType();
    super.copy(entry);
    flags.clear();
    flags.addAll(entry.getFlags());
    defaultValue = entry.getDefaultValue();
    rangeMin = entry.getRangeMin();
    rangeMax = entry.getRangeMax();
    valueMask = entry.getValueMask();
    descriptors.clear();
    descriptors.addAll(entry.getBitDescriptors());
    address = entry.getAddress();
    bankAddresses.clear();
    bankAddresses.putAll(entry.getBankAddresses());
    return this;
  }

  @Override
  public CVEntryBuilder address(int address)
  {
    this.address = address;
    return this;
  }

  @Override
  public CVEntryBuilder bankAddress(int bank0,
                                    int bank1,
                                    int bank2,
                                    int bank3)
  {
    bankAddresses.clear();
    if (bank0 > 0) {
      bankAddresses.put(CVType.INDEX_0,
                        bank0);
      if (bank1 > 0) {
        bankAddresses.put(CVType.INDEX_1,
                          bank1);
        if (bank2 > 0) {
          bankAddresses.put(CVType.INDEX_2,
                            bank2);
          if (bank3 > 0) {
            bankAddresses.put(CVType.INDEX_3,
                              bank3);
          }
        }
      }
    }
    return this;
  }

  @Override
  public CVEntryBuilder type(CVType type)
  {
    this.type = type != null ? type : CVType.NUMERIC;
    return this;
  }

  @Override
  public CVEntryBuilder addFlag(CVFlag flag)
  {
    if (flag != null) {
      flags.add(flag);
    }
    return this;
  }

  @Override
  public CVEntryBuilder removeFlag(CVFlag flag)
  {
    if (flag != null) {
      flags.remove(flag);
    }
    return this;
  }

  @Override
  public CVEntryBuilder clearFlags()
  {
    flags.clear();
    return this;
  }

  @Override
  public CVEntryBuilder defaultValue(int defaultValue)
  {
    this.defaultValue = defaultValue;
    return this;
  }

  @Override
  public CVEntryBuilder rangeMin(int rangeMin)
  {
    this.rangeMin = rangeMin;
    return this;
  }

  @Override
  public CVEntryBuilder rangeMax(int rangeMax)
  {
    this.rangeMax = rangeMax;
    return this;
  }

  @Override
  public CVEntryBuilder valueMask(int mask)
  {
    this.valueMask = mask & 0xff;
    return this;
  }

  @Override
  public CVEntryBuilder addBitDescriptor(CVBitDescriptor descriptor)
  {
    if (descriptor != null && !descriptors.contains(descriptor)) {
      this.descriptors.add(descriptor);
    }
    return this;
  }

  @Override
  public CVEntryBuilder addBitDescriptors(Collection<? extends CVBitDescriptor> descriptors)
  {
    if (descriptors != null) {
      descriptors.stream().forEach(this::addBitDescriptor);
    }
    return this;
  }

  @Override
  public CVEntryBuilder removeBitDescriptor(CVBitDescriptor descriptor)
  {
    if (descriptor != null) {
      descriptors.remove(descriptor);
    }
    return this;
  }

  @Override
  public CVEntryBuilder clearBitDescriptors()
  {
    descriptors.clear();
    return this;
  }

  @Override
  public CVEntry build()
  {
    if (type == null) {
      type = CVType.NUMERIC;
    }
    return new CVEntryImpl(type,
                           descriptions,
                           flags,
                           defaultValue,
                           rangeMin,
                           rangeMax,
                           valueMask,
                           descriptors,
                           address,
                           bankAddresses);
  }

}
