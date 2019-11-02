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
import at.or.reder.dcc.cv.CVFlag;
import at.or.reder.dcc.cv.EnumeratedValue;
import at.or.reder.dcc.util.AbstractDescriptedBuilder;
import at.or.reder.dcc.util.Predicates;
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 *
 * @author Wolfgang Reder
 */
public final class CVBitDescriptorBuilderImpl extends AbstractDescriptedBuilder<CVBitDescriptorBuilder> implements
        CVBitDescriptorBuilder
{

  private int bitMask = 0xff;
  private int defaultValue;
  private int minValue;
  private int maxValue = 0xff;
  private final Set<EnumeratedValue> validValues = new HashSet<>();
  private final Set<CVFlag> flags = EnumSet.noneOf(CVFlag.class);

  @SuppressWarnings("LeakingThisInConstructor")
  public CVBitDescriptorBuilderImpl()
  {
    setThis(this);
  }

  @Override
  public CVBitDescriptorBuilder copy(CVBitDescriptor descriptor)
  {
    this.bitMask = Objects.requireNonNull(descriptor,
                                          "descriptor is null").getBitMask() & 0xff;
    super.copy(descriptor);
    this.defaultValue = descriptor.getDefaultValue() & bitMask;
    this.maxValue = descriptor.getMaxValue();
    this.minValue = descriptor.getMinValue();
    this.validValues.clear();
    addAllowedValues(descriptor.getAllowedValues());
    descriptor.getFlags().stream().filter(Predicates::isNotNull).forEach(flags::add);
    return this;
  }

  @Override
  public CVBitDescriptorBuilder bitMask(int bitMask)
  {
    this.bitMask = bitMask & 0xff;
    return this;
  }

  @Override
  public CVBitDescriptorBuilder defaultValue(int defaultValue)
  {
    this.defaultValue = defaultValue & 0xff;
    return this;
  }

  @Override
  public CVBitDescriptorBuilder minValue(int minValue)
  {
    this.minValue = minValue;
    return this;
  }

  @Override
  public CVBitDescriptorBuilder maxValue(int maxValue)
  {
    this.maxValue = maxValue;
    return this;
  }

  @Override
  public CVBitDescriptorBuilder addAllowedValue(EnumeratedValue value)
  {
    if (value != null) {
      validValues.add(value);
    }
    return this;
  }

  @Override
  public CVBitDescriptorBuilder addAllowedValues(Collection<? extends EnumeratedValue> values)
  {
    if (values != null) {
      values.stream().filter((v) -> v != null).forEach(validValues::add);
    }
    return this;
  }

  @Override
  public CVBitDescriptorBuilder removeAllowedValue(EnumeratedValue value)
  {
    validValues.remove(value);
    return this;
  }

  @Override
  public CVBitDescriptorBuilder clearAllowedValues()
  {
    validValues.clear();
    return this;
  }

  @Override
  public CVBitDescriptorBuilder addFlag(CVFlag flag)
  {
    if (flag != null) {
      flags.add(flag);
    }
    return this;
  }

  @Override
  public CVBitDescriptorBuilder addFlags(Collection<CVFlag> flags)
  {
    if (flags != null) {
      flags.stream().filter(Predicates::isNotNull).forEach(this.flags::add);
    }
    return this;
  }

  @Override
  public CVBitDescriptorBuilder removeFlag(CVFlag flag)
  {
    if (flag != null) {
      flags.remove(flag);
    }
    return this;
  }

  @Override
  public CVBitDescriptorBuilder clearFlags()
  {
    flags.clear();
    return this;
  }

  @Override
  public CVBitDescriptor build()
  {
    return new CVBitDescriptorImpl(bitMask,
                                   defaultValue,
                                   minValue,
                                   maxValue,
                                   validValues,
                                   flags,
                                   descriptions);
  }

}
