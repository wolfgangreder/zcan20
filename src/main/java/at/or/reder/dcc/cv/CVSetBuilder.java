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

import at.or.reder.dcc.util.Localizable;
import at.or.reder.dcc.util.ResourceDescription;
import java.util.Collection;
import java.util.UUID;
import javax.validation.constraints.NotNull;

/**
 *
 * @author Wolfgang Reder
 */
public interface CVSetBuilder
{

  public CVSetBuilder copy(CVSet set);

  public CVSetBuilder provider(CVSetProvider provider);

  public CVSetBuilder id(@NotNull UUID id);

  public CVSetBuilder addDescription(String loc,
                                     ResourceDescription name);

  public CVSetBuilder addDescriptions(Localizable<? extends ResourceDescription> descriptions);

  public CVSetBuilder removeDescription(String loc);

  public CVSetBuilder clearDescriptions();

  public CVSetBuilder addEntry(@NotNull CVEntry entry);

  public CVSetBuilder addEntries(@NotNull Collection<? extends CVEntry> entries);

  public CVSetBuilder removeEntry(@NotNull CVAddress address);

  public CVSetBuilder removeEntries(@NotNull Collection<? extends CVAddress> adresses);

  public CVSetBuilder clearEntries();

  public CVSet build();

}
