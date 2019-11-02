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
package at.or.reder.dcc.util;

import at.or.reder.dcc.util.ResourceDescription;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 *
 * @author Wolfgang Reder
 * @param <V>
 */
public abstract class AbstractDescriptedBuilder<V>
{

  protected final Map<Locale, ResourceDescription> descriptions = new HashMap<>();
  private V subThis;

  protected AbstractDescriptedBuilder()
  {
  }

  protected void setThis(V subThis)
  {
    this.subThis = subThis;
  }

  protected void copy(Descripted d)
  {
    descriptions.clear();
    d.getAllResourceDescriptions().
            entrySet().
            stream().
            filter((e) -> e.getValue() != null).
            forEach((e) -> this.descriptions.put(e.getKey(),
                                                 e.getValue()));
  }

  public V addDescription(Locale locale,
                          ResourceDescription description)
  {
    if (description != null) {
      descriptions.put(locale,
                       description);
    }
    return subThis;
  }

  public V addDescriptions(Map<Locale, ResourceDescription> description)
  {
    if (description != null) {
      description.
              entrySet().
              stream().
              filter((e) -> e.getValue() != null).
              forEach((e) -> this.descriptions.put(e.getKey(),
                                                   e.getValue()));
    }
    return subThis;
  }

  public V removeDescription(Locale locale)
  {
    descriptions.remove(locale);
    return subThis;
  }

  public V clearDescriptions()
  {
    descriptions.clear();
    return subThis;
  }

}
