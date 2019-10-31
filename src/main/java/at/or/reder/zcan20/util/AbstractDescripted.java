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

import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

/**
 *
 * @author Wolfgang Reder
 */
public abstract class AbstractDescripted implements Descripted
{

  private final Map<Locale, ResourceDescription> descriptions;
  private ResourceDescription defaultDescription;

  protected AbstractDescripted(Map<Locale, ? extends ResourceDescription> descriptions,
                               ResourceDescription defaultDescription)
  {
    this.defaultDescription = defaultDescription;
    this.descriptions = descriptions.entrySet().
            stream().
            filter((e) -> e.getValue() != null).
            collect(Collectors.toUnmodifiableMap(Map.Entry::getKey,
                                                 Map.Entry::getValue));
  }

  @Override
  public Map<Locale, ResourceDescription> getAllResourceDescriptions()
  {
    return descriptions;
  }

  public abstract String getDefaultName();

  public String getDefaultDescription()
  {
    return "";
  }

  @Override
  public ResourceDescription getFallbackDescription()
  {
    if (defaultDescription == null) {
      defaultDescription = new ResourceDescription(getDefaultName(),
                                                   getDefaultDescription());
    }
    return defaultDescription;
  }

}
