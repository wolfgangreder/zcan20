/*
 * Copyright 2021 Wolfgang Reder.
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

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

/**
 *
 * @author Wolfgang Reder
 */
public class Localizable<C> implements Serializable
{

  private Map<String, C> values = new HashMap<>();
  private final boolean mutable;

  public Localizable(boolean mutable)
  {
    this.mutable = mutable;
  }

  public Localizable(C defaultValue)
  {
    this(defaultValue,
         false);
  }

  public Localizable(C defaultValue,
                     boolean mutable)
  {
    this.mutable = mutable;
    values.put("",
               defaultValue);
  }

  private Localizable(Localizable<? extends C> copy,
                      boolean mutable)
  {
    this.values.putAll(copy.values);
    this.mutable = mutable;
  }

  protected boolean isValueValid(C value)
  {
    return value != null;
  }

  public final C getValue()
  {
    return getValue("");
  }

  public final C getValue(String language)
  {
    if (language == null) {
      language = "";
    }
    return values.getOrDefault(language.trim(),
                               values.get(""));
  }

  public final Map<String, C> getValues()
  {
    if (mutable) {
      return new HashMap<>(values);
    } else {
      return Collections.unmodifiableMap(values);
    }
  }

  private final void checkMutable()
  {
    if (!mutable) {
      throw new UnsupportedOperationException("Localizable is imutable");
    }
  }

  public final Localizable<C> addValues(Localizable<? extends C> loc)
  {
    addValues(loc.values);
    return this;
  }

  public final Localizable<C> addValues(Map<String, ? extends C> values)
  {
    checkMutable();
    for (Map.Entry<String, ? extends C> e : values.entrySet()) {
      addValue(e.getKey(),
               e.getValue());
    }
    return this;
  }

  public final Localizable<C> addValue(String language,
                                       C value)
  {
    checkMutable();
    if (language == null) {
      language = "";
    }
    values.put(language.trim(),
               value);
    return this;
  }

  public final boolean normalize()
  {
    return normalize(Locale.getDefault().getLanguage());
  }

  public final boolean normalize(String defaultLang)
  {
    checkMutable();
    C val = values.get("");
    if (!isValueValid(val)) {
      val = values.get(defaultLang);
      if (isValueValid(val)) {
        values.put("",
                   val);
        values.remove(defaultLang);
        return true;
      }
      return false;
    } else {
      values.remove(defaultLang);
      return true;
    }
  }

  public final void removeLanguage(String language)
  {
    checkMutable();
    if (language != null && !language.isBlank()) {
      values.remove(language.trim());
    }
  }

  public final boolean isMutable()
  {
    return mutable;
  }

  @Override
  public final int hashCode()
  {
    int hash = 7;
    hash = 61 * hash + Objects.hashCode(this.values);
    hash = 61 * hash + (this.mutable ? 1 : 0);
    return hash;
  }

  @Override
  public final boolean equals(Object obj)
  {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final Localizable other = (Localizable) obj;
    if (this.mutable != other.mutable) {
      return false;
    }
    return Objects.equals(this.values,
                          other.values);
  }

  public final Localizable<C> toMutable()
  {
    return new Localizable<>(this,
                             true);
  }

  public final Localizable<C> toImutable()
  {
    if (!mutable) {
      return this;
    } else {
      return new Localizable<>(this,
                               false);
    }
  }

  @Override
  public final String toString()
  {
    return getValue("").toString();
  }

}
