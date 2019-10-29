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
import at.or.reder.dcc.cv.CVUtils;
import at.or.reder.dcc.cv.CVValue;
import at.or.reder.dcc.cv.ResourceDescription;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import org.openide.util.NbBundle.Messages;

/**
 *
 * @author Wolfgang Reder
 */
@Messages({"# {0} - address",
           "CVEntryBuilderImpl_defaultName_simple=CV #{0,number,0}",
           "# {0} - address",
           "# {1} - bankAddress",
           "CVEntryBuilderImpl_defaultName_bank=CV #{1}:{0,number,0}"})
public final class CVEntryBuilderImpl implements CVEntryBuilder
{

  private CVType type = CVType.NUMERIC;
  private final Map<Locale, ResourceDescription> descriptions = new HashMap<>();
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
  private final Map<CVType, Integer> bankAddresses = new HashMap<>();

  @Override
  public CVEntryBuilder copy(CVEntry entry)
  {
    type = Objects.requireNonNull(entry,
                                  "entry is null").getCVType();
    descriptions.clear();
    descriptions.putAll(entry.getAllDescriptions());
    flags.clear();
    flags.addAll(entry.getFlags());
    defaultValue = entry.getDefaultValue();
    rangeMin = entry.getRangeMin();
    rangeMax = entry.getRangeMax();
    allowedValues.clear();
    allowedValues.addAll(entry.getAllowedValues());
    valueMask = entry.getValueMask();
    descriptors.clear();
    descriptors.addAll(entry.getBitDescriptors());
    value = entry.getValue();
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
      bankAddresses.put(CVType.BANKREGISTER_0,
                        bank0);
      if (bank1 > 0) {
        bankAddresses.put(CVType.BANKREGISTER_1,
                          bank1);
        if (bank2 > 0) {
          bankAddresses.put(CVType.BANKREGISTER_2,
                            bank2);
          if (bank3 > 0) {
            bankAddresses.put(CVType.BANKREGISTER_3,
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
  public CVEntryBuilder addDescription(Locale locale,
                                       ResourceDescription description)
  {
    if (description != null) {
      descriptions.put(locale,
                       description);
    } else {
      descriptions.remove(locale);
    }
    return this;
  }

  @Override
  public CVEntryBuilder addDescriptions(Map<Locale, ResourceDescription> description)
  {
    if (description != null) {
      for (Map.Entry<Locale, ResourceDescription> e : description.entrySet()) {
        if (e.getValue() != null) {
          descriptions.put(e.getKey(),
                           e.getValue());
        } else {
          descriptions.remove(e.getKey());
        }
      }
    }
    return this;
  }

  @Override
  public CVEntryBuilder removeDescription(Locale locale)
  {
    descriptions.remove(locale);
    return this;
  }

  @Override
  public CVEntryBuilder clearDescriptions()
  {
    descriptions.clear();
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
  public CVEntryBuilder addAllowedValue(int value)
  {
    allowedValues.add(value & 0xff);
    return this;
  }

  @Override
  public CVEntryBuilder addAllowedValues(Collection<? extends Number> values)
  {
    if (values != null) {
      values.stream().
              filter((v) -> v != null).
              map((n) -> n.intValue() & 0xff).
              forEach(this.allowedValues::add);
    }
    return this;
  }

  @Override
  public CVEntryBuilder removeAllowedValue(int value)
  {
    this.allowedValues.remove(value & 0xff);
    return this;
  }

  @Override
  public CVEntryBuilder clearAllowedValues()
  {
    this.allowedValues.clear();
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
  public CVEntryBuilder setValue(CVValue value)
  {
    this.value = value != null ? value : CVValue.NO_VALUE;
    return this;
  }

  @Override
  public CVEntry build()
  {
    if (type == null) {
      if (descriptors.isEmpty()) {
        type = CVType.NUMERIC;
      } else {
        type = CVType.BITFIELD;
      }
    }
    ResourceDescription tmp = descriptions.get(null);
    if (tmp == null) {
      String name;
      if (bankAddresses.isEmpty()) {
        name = Bundle.CVEntryBuilderImpl_defaultName_simple(address);
      } else {
        String bankAddress = bankAddresses.entrySet().stream().
                filter((f) -> f.getValue() != null && CVUtils.isBankAddress(f.getKey())).
                sorted(Comparator.comparing(Map.Entry::getKey)).
                map((e) -> Integer.toString(e.getValue())).
                collect(Collectors.joining(",",
                                           "[",
                                           "]"));
        name = Bundle.CVEntryBuilderImpl_defaultName_bank(address,
                                                          bankAddress);
      }
      descriptions.put(null,
                       new ResourceDescription(name,
                                               ""));
    }
    return new CVEntryImpl(type,
                           descriptions,
                           flags,
                           defaultValue,
                           rangeMin,
                           rangeMax,
                           allowedValues,
                           valueMask,
                           descriptors,
                           value,
                           address,
                           bankAddresses);
  }

}
