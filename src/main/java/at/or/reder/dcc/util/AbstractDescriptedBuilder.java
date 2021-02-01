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

/**
 *
 * @author Wolfgang Reder
 * @param <V>
 */
public abstract class AbstractDescriptedBuilder<V>
{

  protected final Localizable<ResourceDescription> descriptions = new DescriptionLocalizable(true);
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
    descriptions.getValues().clear();
    descriptions.addValues(d.getLocalized());
  }

  public V addDescription(String locale,
                          ResourceDescription description)
  {
    if (description != null) {
      descriptions.addValue(locale,
                            description);
    }
    return subThis;
  }

  public V addDescriptions(Localizable<? extends ResourceDescription> descriptions)
  {
    if (descriptions != null) {
      this.descriptions.addValues(descriptions);
    }
    return subThis;
  }

  public V removeDescription(String locale)
  {
    descriptions.removeLanguage(locale);
    return subThis;
  }

  public V clearDescriptions()
  {
    descriptions.getValues().clear();
    return subThis;
  }

}
