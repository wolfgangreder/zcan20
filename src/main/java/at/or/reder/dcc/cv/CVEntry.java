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

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import org.openide.util.Lookup;

/**
 *
 * @author Wolfgang Reder
 */
public interface CVEntry extends Lookup.Provider, CVAddress
{

  /**
   * Die Art der CV.
   *
   * @return cvType
   */
  public CVType getCVType();

  public default ResourceDescription getDescription()
  {
    return getDescription(Locale.getDefault());
  }

  public default ResourceDescription getDescription(Locale locale)
  {
    return CVUtils.filterLocaleMap(getAllDescriptions(),
                                   locale,
                                   this::getDefaultDescription);
  }

  public default ResourceDescription buildDefaultResourceDescription()
  {
    return new ResourceDescription("CV " + Integer.toString(getAddress()),
                                   "");
  }

  public Map<Locale, ResourceDescription> getAllDescriptions();

  public default ResourceDescription getDefaultDescription()
  {
    return buildDefaultResourceDescription();
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

  public default Set<Integer> getAllowedValues()
  {
    return Collections.emptySet();
  }

  public default int getValueMask()
  {
    return 255;
  }

  public default List<CVBitDescriptor> getBitDescriptors()
  {
    return Collections.emptyList();
  }

  public default CVValue getValue()
  {
    return CVValue.NO_VALUE;
  }

}
