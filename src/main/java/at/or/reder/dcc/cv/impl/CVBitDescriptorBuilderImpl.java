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
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 *
 * @author Wolfgang Reder
 */
public final class CVBitDescriptorBuilderImpl implements CVBitDescriptorBuilder
{

  private int bitMask;
  private int defaultValue;
  private final Set<Integer> defaultValues = new HashSet<>();
  private final Map<Locale, Map<Integer, ResourceDescription>> descriptions = new HashMap<>();

  @Override
  public CVBitDescriptorBuilder copy(CVBitDescriptor descriptor)
  {
    this.bitMask = Objects.requireNonNull(descriptor,
                                          "descriptor is null").getBitMask() & 0xff;
    this.defaultValue = descriptor.getDefaultValue() & bitMask;
    this.defaultValues.clear();
    addAllowedValues(descriptor.getAllowedValues());
    this.descriptions.clear();
    this.descriptions.putAll(descriptor.getAllValueDescriptions());
    this.descriptions.put(null,
                          descriptor.getDefaultValueDescriptions());
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
  public CVBitDescriptorBuilder addAllowedValue(int value)
  {
    this.defaultValues.add(value & 0xff);
    return this;
  }

  @Override
  public CVBitDescriptorBuilder addAllowedValues(Collection<? extends Number> values)
  {
    if (values != null) {
      values.stream().filter((v) -> v != null).map((n) -> n.intValue() & 0xff).forEach(defaultValues::add);
    }
    return this;
  }

  @Override
  public CVBitDescriptorBuilder removeAllowedValue(int value)
  {
    defaultValues.remove(value & 0xff);
    return this;
  }

  @Override
  public CVBitDescriptorBuilder clearAllowedValues()
  {
    defaultValues.clear();
    return this;
  }

  @Override
  public CVBitDescriptorBuilder addValueDescription(Locale locale,
                                                    int value,
                                                    ResourceDescription desc)
  {
    Map<Integer, ResourceDescription> m = descriptions.computeIfAbsent(locale,
                                                                       (l) -> new HashMap<>());
    if (desc != null) {
      m.put(value & 0xff,
            desc);
    } else {
      m.remove(value & 0xff);
    }
    return this;
  }

  @Override
  public CVBitDescriptorBuilder addValueDescriptions(Locale locale,
                                                     Map<Integer, ResourceDescription> names)
  {
    for (Map.Entry<Integer, ResourceDescription> e : names.entrySet()) {
      addValueDescription(locale,
                          e.getKey(),
                          e.getValue());
    }
    return this;
  }

  @Override
  public CVBitDescriptorBuilder removeValueDescription(Locale locale,
                                                       int value)
  {
    Map<Integer, ResourceDescription> m = descriptions.get(locale);
    if (m != null) {
      m.remove(value & 0xff);
    }
    return this;
  }

  @Override
  public CVBitDescriptorBuilder clearValueDescriptions()
  {
    descriptions.clear();
    return this;
  }

  @Override
  public CVBitDescriptor build()
  {
    return new CVBitDescriptorImpl(bitMask,
                                   defaultValue & bitMask,
                                   defaultValues,
                                   descriptions);
  }

}
