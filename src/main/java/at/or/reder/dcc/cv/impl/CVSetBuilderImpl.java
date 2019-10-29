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
package at.or.reder.dcc.cv.impl;

import at.or.reder.dcc.cv.CVAddress;
import at.or.reder.dcc.cv.CVEntry;
import at.or.reder.dcc.cv.CVSet;
import at.or.reder.dcc.cv.CVSetBuilder;
import at.or.reder.dcc.cv.ResourceDescription;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.UUID;

/**
 *
 * @author Wolfgang Reder
 */
public final class CVSetBuilderImpl implements CVSetBuilder
{

  private UUID id;
  private final Map<Locale, ResourceDescription> descriptions = new HashMap<>();
  private final SortedSet<CVEntry> entries = new TreeSet<>(Comparator.comparing(CVEntry::getFlatAddress));

  @Override
  public CVSetBuilder copy(CVSet set)
  {
    this.id = Objects.requireNonNull(set,
                                     "set is null").getId();
    descriptions.clear();
    descriptions.putAll(set.getAllDescriptions());
    entries.clear();
    entries.addAll(set.getEntries());
    return this;
  }

  @Override
  public CVSetBuilder id(UUID id)
  {
    this.id = id;
    return this;
  }

  @Override
  public CVSetBuilder addDescription(Locale loc,
                                     ResourceDescription name)
  {
    if (name != null) {
      descriptions.put(loc,
                       name);
    } else {
      descriptions.remove(loc);
    }
    return this;
  }

  @Override
  public CVSetBuilder addDescriptions(Map<Locale, ResourceDescription> descriptions)
  {
    if (descriptions != null) {
      for (Map.Entry<Locale, ResourceDescription> e : descriptions.entrySet()) {
        addDescription(e.getKey(),
                       e.getValue());
      }
    }
    return this;
  }

  @Override
  public CVSetBuilder removeDescription(Locale loc)
  {
    descriptions.remove(loc);
    return this;
  }

  @Override
  public CVSetBuilder clearDescriptions()
  {
    descriptions.clear();
    return this;
  }

  @Override
  public CVSetBuilder addEntry(CVEntry entry)
  {
    if (entry != null) {
      entries.add(entry);
    }
    return this;
  }

  @Override
  public CVSetBuilder addEntries(Collection<? extends CVEntry> entries)
  {
    if (entries != null) {
      entries.stream().filter((e) -> e != null).forEach(this.entries::add);
    }
    return this;
  }

  @Override
  @SuppressWarnings("element-type-mismatch")
  public CVSetBuilder removeEntry(CVAddress address)
  {
    if (address != null) {
      entries.remove(address);
    }
    return this;
  }

  @Override
  public CVSetBuilder removeEntries(Collection<? extends CVAddress> adresses)
  {
    entries.removeAll(adresses);
    return this;
  }

  @Override
  public CVSetBuilder clearEntries()
  {
    entries.clear();
    return this;
  }

  @Override
  public CVSet build()
  {
    if (id == null) {
      id = UUID.randomUUID();
    }
    return new CVSetImpl(id,
                         descriptions,
                         entries);
  }

}
