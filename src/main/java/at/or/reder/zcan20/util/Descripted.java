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
package at.or.reder.zcan20.util;

import at.or.reder.dcc.cv.CVUtils;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 *
 * @author Wolfgang Reder
 */
public interface Descripted
{

  public default String getName(Locale loc)
  {
    List<Locale> localeList = CVUtils.getLocaleList(loc);
    Map<Locale, ResourceDescription> map = getAllResourceDescriptions();
    for (Locale l : localeList) {
      ResourceDescription desc = map.get(l);
      if (desc != null && desc.getName() != null && !desc.getName().isBlank()) {
        return desc.getName();
      }
    }
    return getFallbackDescription().getName();
  }

  public default String getName()
  {
    return getName(Locale.getDefault());
  }

  public default String getDescription(Locale loc)
  {
    List<Locale> localeList = CVUtils.getLocaleList(loc);
    Map<Locale, ResourceDescription> map = getAllResourceDescriptions();
    for (Locale l : localeList) {
      ResourceDescription desc = map.get(l);
      if (desc != null && desc.getDescrption() != null && !desc.getDescrption().isBlank()) {
        return desc.getDescrption();
      }
    }
    return getFallbackDescription().getDescrption();

  }

  public default String getDescription()
  {
    return getDescription(Locale.getDefault());
  }

  public default ResourceDescription getResourceDescription(Locale loc)
  {
    List<Locale> localeList = CVUtils.getLocaleList(loc);
    Map<Locale, ResourceDescription> map = getAllResourceDescriptions();
    for (Locale l : localeList) {
      ResourceDescription desc = map.get(l);
      if (desc != null) {
        return desc;
      }
    }
    return getFallbackDescription();
  }

  public Map<Locale, ResourceDescription> getAllResourceDescriptions();

  public ResourceDescription getFallbackDescription();

}
