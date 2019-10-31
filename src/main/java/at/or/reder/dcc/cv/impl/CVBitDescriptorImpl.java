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
import at.or.reder.dcc.cv.EnumeratedValue;
import at.or.reder.zcan20.util.AbstractDescripted;
import at.or.reder.zcan20.util.ResourceDescription;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
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

  public CVBitDescriptorImpl(int bitMask,
                             int defaultValue,
                             int min,
                             int max,
                             Collection<? extends EnumeratedValue> allowedValues,
                             Map<Locale, ResourceDescription> valueDescriptions)
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
              filter((v) -> v != null).
              forEach(tmp::add);
      this.allowedValues = Collections.unmodifiableList(new ArrayList<>(tmp));
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
