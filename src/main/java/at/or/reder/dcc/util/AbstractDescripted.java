/*
 * Copyright 2019-2021 Wolfgang Reder.
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
 */
public abstract class AbstractDescripted implements Descripted
{

  private final Localizable<ResourceDescription> descriptions;
  private ResourceDescription defaultDescription;

  protected AbstractDescripted(Localizable<ResourceDescription> descriptions,
                               ResourceDescription defaultDescription)
  {
    this.defaultDescription = defaultDescription;
    this.descriptions = descriptions.toImutable();
  }

  public abstract String getDefaultName();

  @Override
  public Localizable<ResourceDescription> getLocalized()
  {
    return descriptions;
  }

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
