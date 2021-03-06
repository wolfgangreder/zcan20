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

import java.util.Locale;

/**
 *
 * @author Wolfgang Reder
 */
public interface Descripted
{

  public Localizable<ResourceDescription> getLocalized();

  public default String getName()
  {
    return getLocalized().getValue(Locale.getDefault().getLanguage()).getName();
  }

  public default String getName(String language)
  {
    return getLocalized().getValue(language).getName();
  }

  public default String getDescription(String lang)
  {
    return getLocalized().getValue(lang).getDescrption();
  }

  public default String getDescription()
  {
    return getDescription(Locale.getDefault().getLanguage());
  }

  public ResourceDescription getFallbackDescription();

}
