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

import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author Wolfgang Reder
 */
public interface CVBitDescriptor
{

  public default int getOffset()
  {
    return Integer.lowestOneBit(getBitMask());
  }

  public int getBitMask();

  public int getDefaultValue();

  public Set<Integer> getAllowedValues();

  public default ResourceDescription getValueDescription(int value)
  {
    return getValueDescription(Locale.getDefault(),
                               value);
  }

  public default ResourceDescription getValueDescription(Locale locale,
                                                         int value)
  {
    Map<Integer, ResourceDescription> map = getValueDescriptions(locale);
    ResourceDescription result = null;
    if (map != null) {
      result = map.get(value & getBitMask());
    }
    if (result == null) {
      result = new ResourceDescription(Integer.toString(value),
                                       "");
    }
    return result;
  }

  public default Map<Integer, ResourceDescription> getValueDescriptions(Locale locale)
  {
    return CVUtils.filterLocaleMap(getAllValueDescriptions(),
                                   locale,
                                   this::getDefaultValueDescriptions);
  }

  public Map<Locale, Map<Integer, ResourceDescription>> getAllValueDescriptions();

  public Map<Integer, ResourceDescription> getDefaultValueDescriptions();

}
