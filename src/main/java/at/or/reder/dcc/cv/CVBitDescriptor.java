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
package at.or.reder.dcc.cv;

import at.or.reder.dcc.util.Descripted;
import java.util.List;
import java.util.Set;

/**
 *
 * @author Wolfgang Reder
 */
public interface CVBitDescriptor extends Descripted
{

  public default int getMinValue()
  {
    return 0;
  }

  public default int getMaxValue()
  {
    return 0xff & getBitMask();
  }

  public List<EnumeratedValue> getAllowedValues();

  public default int getOffset()
  {
    return Integer.lowestOneBit(getBitMask());
  }

  public default int getWidth()
  {
    return Integer.bitCount(-getBitMask());
  }

  public int getBitMask();

  public int getDefaultValue();

  public default int normalizeValue(int value)
  {
    return (value & getBitMask()) >> getOffset();
  }

  public Set<CVFlag> getFlags();

}
