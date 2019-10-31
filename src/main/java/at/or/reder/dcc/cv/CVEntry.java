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

import at.or.reder.zcan20.util.Descripted;
import at.or.reder.zcan20.util.ResourceDescription;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import org.openide.util.Lookup;

/**
 *
 * @author Wolfgang Reder
 */
public interface CVEntry extends Lookup.Provider, CVAddress, Descripted
{

  /**
   * Die Art der CV.
   *
   * @return cvType
   */
  public CVType getCVType();

  @Override
  public default ResourceDescription getFallbackDescription()
  {
    return new ResourceDescription("CV " + Integer.toString(getAddress()),
                                   "");
  }

  public Set<CVFlag> getFlags();

  public default boolean isReadOnly()
  {
    return getFlags().contains(CVFlag.READ_ONLY);
  }

  public default int getDefaultValue()
  {
    return 0;
  }

  public default int getRangeMin()
  {
    return 0;
  }

  public default int getRangeMax()
  {
    return 255;
  }

  public default int getValueMask()
  {
    return 255;
  }

  public default int getOffset()
  {
    return Integer.lowestOneBit(getValueMask());
  }

  public default int getWidth()
  {
    return Integer.bitCount(-getValueMask());
  }

  public default List<CVBitDescriptor> getBitDescriptors()
  {
    return Collections.emptyList();
  }

}
