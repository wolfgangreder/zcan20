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

import at.or.reder.dcc.cv.EnumeratedValue;
import at.or.reder.dcc.util.AbstractDescripted;
import at.or.reder.dcc.util.ResourceDescription;
import java.util.Locale;
import java.util.Map;

/**
 *
 * @author Wolfgang Reder
 */
final class EnumeratedValueImpl extends AbstractDescripted implements EnumeratedValue
{

  private final int value;

  public EnumeratedValueImpl(int value,
                             Map<Locale, ? extends ResourceDescription> descriptions)
  {
    super(descriptions,
          null);
    this.value = value;
  }

  @Override
  public String getDefaultName()
  {
    return Integer.toString(getValue());
  }

  @Override
  public int getValue()
  {
    return value;
  }

  @Override
  public int hashCode()
  {
    int hash = 7;
    hash = 53 * hash + this.value;
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
    final EnumeratedValueImpl other = (EnumeratedValueImpl) obj;
    return this.value == other.value;
  }

  @Override
  public String toString()
  {
    return "EnumeratedValueImpl{" + getName() + '}';
  }

}
