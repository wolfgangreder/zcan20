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
import at.or.reder.dcc.cv.CVFlag;
import at.or.reder.dcc.cv.EnumeratedValue;
import at.or.reder.dcc.util.AbstractDescripted;
import at.or.reder.dcc.util.Localizable;
import at.or.reder.dcc.util.Predicates;
import at.or.reder.dcc.util.ResourceDescription;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 *
 * @author Wolfgang Reder
 */
final class CVBitDescriptorImpl extends AbstractDescripted implements CVBitDescriptor
{

  private final int bitMask;
  private final int defaultValue;
  private final int minValue;
  private final int maxValue;
  private final List<EnumeratedValue> allowedValues;
  private final Set<CVFlag> flags;

  public CVBitDescriptorImpl(int bitMask,
                             int defaultValue,
                             int min,
                             int max,
                             Collection<? extends EnumeratedValue> allowedValues,
                             Collection<CVFlag> flags,
                             Localizable<ResourceDescription> valueDescriptions)
  {
    super(valueDescriptions,
          null);
    this.bitMask = bitMask;
    this.minValue = Math.min(min & bitMask,
                             max & bitMask);
    this.maxValue = Math.max(max & bitMask,
                             min & bitMask);
    this.defaultValue = Math.max(min,
                                 Math.min(max,
                                          defaultValue & bitMask));
    if (allowedValues == null || allowedValues.isEmpty()) {
      this.allowedValues = Collections.emptyList();
    } else {
      SortedSet<EnumeratedValue> tmp = new TreeSet<>(Comparator.comparing(EnumeratedValue::getValue));
      allowedValues.stream().
              filter(Predicates::isNotNull).
              forEach(tmp::add);
      this.allowedValues = Collections.unmodifiableList(new ArrayList<>(tmp));
    }
    if (flags == null || flags.isEmpty()) {
      this.flags = Collections.emptySet();
    } else {
      EnumSet<CVFlag> tmp = EnumSet.noneOf(CVFlag.class);
      flags.stream().filter(Predicates::isNotNull).forEach(tmp::add);
      if (tmp.isEmpty()) {
        this.flags = Collections.emptySet();
      } else {
        this.flags = Collections.unmodifiableSet(tmp);
      }
    }
  }

  @Override
  public int getBitMask()
  {
    return bitMask;
  }

  @Override
  public int getDefaultValue()
  {
    return defaultValue;
  }

  @Override
  public int getMinValue()
  {
    return minValue;
  }

  @Override
  public int getMaxValue()
  {
    return maxValue;
  }

  @Override
  public String getDefaultName()
  {
    return Integer.toString(defaultValue);
  }

  @Override
  public List<EnumeratedValue> getAllowedValues()
  {
    return allowedValues;
  }

  @Override
  public Set<CVFlag> getFlags()
  {
    return flags;
  }

  @Override
  public int hashCode()
  {
    int hash = 7;
    hash = 59 * hash + this.bitMask;
    return hash;
  }

  @Override
  public boolean equals(Object obj)
  {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final CVBitDescriptorImpl other = (CVBitDescriptorImpl) obj;
    return this.bitMask == other.bitMask;
  }

  @Override
  public String toString()
  {
    return "CVBitDescriptorImpl{" + getName() + '}';
  }

}
