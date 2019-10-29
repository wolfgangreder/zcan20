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
import at.or.reder.dcc.cv.CVValue;
import at.or.reder.dcc.cv.CVValueState;

/**
 *
 * @author Wolfgang Reder
 */
public final class CVValueImpl implements CVValue
{

  private final CVValueState state;
  private final int value;
  private final CVEntry entry;

  public CVValueImpl(CVValueState state,
                     int value,
                     CVEntry entry)
  {
    this.state = state != null ? state : CVValueState.UNKNOWN;
    this.value = value;
    this.entry = entry;
  }

  @Override
  public CVValueState getState()
  {
    return state;
  }

  @Override
  public int getValue()
  {
    if (entry != null) {
      int result = value & entry.getValueMask();
      if (!entry.getAllowedValues().isEmpty() && !entry.getAllowedValues().contains(result)) {
        return -1;
      }
      return result;
    } else {
      return value;
    }
  }

  @Override
  public int getValue(CVBitDescriptor descriptor)
  {
    int descriptorMask = descriptor.getBitMask();
    int entryMask = entry != null ? entry.getValueMask() : 0xff;
    int mask = descriptorMask & entryMask;
    if (mask == 0) {
      return -1;
    }
    int v = value & mask;
    return v;
  }

  @Override
  public int hashCode()
  {
    int hash = 7;
    hash = 41 * hash + this.value;
    return hash;
  }

  @Override
  public boolean equals(Object obj)
  {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof CVValue)) {
      return false;
    }
    return getValue() == ((CVValue) obj).getValue();
  }

  @Override
  public String toString()
  {
    return "CVValueImpl{" + "state=" + state + ", value=" + value + ", entry=" + entry + '}';
  }

}
