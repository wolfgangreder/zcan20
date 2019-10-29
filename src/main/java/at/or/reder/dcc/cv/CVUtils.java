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

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Supplier;

/**
 *
 * @author Wolfgang Reder
 */
public final class CVUtils
{

  public static boolean isBankAddress(CVType type)
  {
    return type == CVType.BANKREGISTER_0 || type == CVType.BANKREGISTER_1 || type == CVType.BANKREGISTER_2 || type == CVType.BANKREGISTER_3;
  }

  private static final ConcurrentMap<Locale, List<Locale>> localeLists = new ConcurrentHashMap<>();

  public static <V> V filterLocaleMap(Map<Locale, ? extends V> map,
                                      Locale root,
                                      Supplier<? extends V> defaultValue)
  {
    List<Locale> localeList = getLocaleList(root);
    V result = null;
    for (Locale l : localeList) {
      result = map.get(l);
      if (result != null) {
        return result;
      }
    }

    if (!map.isEmpty()) {
      result = map.entrySet().iterator().next().getValue();
    }
    if (result == null && defaultValue != null) {
      return defaultValue.get();
    } else {
      return result;
    }
  }

  public static List<Locale> getLocaleList(Locale localeIn)
  {
    return localeLists.computeIfAbsent(localeIn,
                                       CVUtils::createLocaleList);
  }

  private static List<Locale> createLocaleList(Locale localeIn)
  {
    List<Locale> result = new ArrayList<>();
    if (localeIn == null) {
      localeIn = Locale.getDefault();
    }
    if (localeIn != Locale.getDefault()) {
      result.add(localeIn);
      Locale language = Locale.forLanguageTag(localeIn.getLanguage());
      if (language != localeIn) {
        result.add(language);
      }
      localeIn = Locale.getDefault();
    }
    result.add(localeIn);
    Locale language = Locale.forLanguageTag(localeIn.getLanguage());
    if (language != localeIn) {
      result.add(language);
    }
    return result;
  }

  private CVUtils()
  {
  }

}
