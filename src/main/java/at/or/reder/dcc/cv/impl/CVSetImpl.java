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
import at.or.reder.dcc.cv.ResourceDescription;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import org.openide.util.Lookup;

/**
 *
 * @author Wolfgang Reder
 */
final class CVSetImpl implements CVSet
{

  private final UUID id;
  private final Map<Locale, ResourceDescription> descriptions;
  private final List<CVEntry> entries;

  public CVSetImpl(UUID id,
                   Map<Locale, ResourceDescription> descriptions,
                   Collection<? extends CVEntry> entries)
  {
    this.id = id;
    Map<Locale, ResourceDescription> tmp = new HashMap<>(descriptions);
    if (tmp.get(null) == null) {
      tmp.put(null,
              new ResourceDescription("CV Set " + id.toString(),
                                      ""));
    }
    this.descriptions = Collections.unmodifiableMap(tmp);
    if (entries.isEmpty()) {
      this.entries = Collections.emptyList();
    } else {
      this.entries = Collections.unmodifiableList(new ArrayList<>(entries));
    }
  }

  @Override
  public UUID getId()
  {
    return id;
  }

  @Override
  public Map<Locale, ResourceDescription> getAllDescriptions()
  {
    return descriptions;
  }

  @Override
  public ResourceDescription getDefaultDescription()
  {
    return descriptions.get(null);
  }

  @Override
  public List<CVEntry> getEntries()
  {
    return entries;
  }

  @Override
  public CVEntry getEntry(CVAddress address)
  {
    int index = entries.indexOf(address);
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
