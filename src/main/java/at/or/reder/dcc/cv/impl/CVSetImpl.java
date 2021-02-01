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
import at.or.reder.dcc.cv.CVSetProvider;
import at.or.reder.dcc.util.AbstractDescripted;
import at.or.reder.dcc.util.Localizable;
import at.or.reder.dcc.util.ResourceDescription;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.openide.util.Lookup;

/**
 *
 * @author Wolfgang Reder
 */
final class CVSetImpl extends AbstractDescripted implements CVSet
{

  private final CVSetProvider provider;
  private final UUID id;
  private final List<CVEntry> entries;
  private static final Comparator<CVAddress> entryComparator = Comparator.comparing(CVAddress::getFlatAddress);

  public CVSetImpl(UUID id,
                   CVSetProvider provider,
                   Localizable<ResourceDescription> descriptions,
                   Collection<? extends CVEntry> entries)
  {
    super(descriptions,
          null);
    this.provider = provider;
    this.id = id;
    if (entries.isEmpty()) {
      this.entries = Collections.emptyList();
    } else {
      this.entries = entries.stream().
              filter((f) -> f != null).
              sorted(entryComparator).
              collect(Collectors.toUnmodifiableList());
    }
  }

  @Override
  public String getDefaultName()
  {
    return "CV Set " + id.toString();
  }

  @Override
  public CVSetProvider getProvider()
  {
    return provider;
  }

  @Override
  public UUID getId()
  {
    return id;
  }

  @Override
  public List<CVEntry> getEntries()
  {
    return entries;
  }

  @Override
  public CVEntry getEntry(CVAddress address)
  {
    if (address == null) {
      return null;
    }
    int index = Collections.binarySearch(entries,
                                         address,
                                         entryComparator);
    if (index >= 0) {
      return entries.get(index);
    }
    return null;
  }

  @Override
  public Lookup getLookup()
  {
    return Lookup.EMPTY;
  }

}
