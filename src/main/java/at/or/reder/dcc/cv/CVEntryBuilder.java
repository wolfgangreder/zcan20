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

import java.util.Collection;
import java.util.Locale;
import java.util.Map;
import javax.validation.constraints.NotNull;

/**
 *
 * @author Wolfgang Reder
 */
public interface CVEntryBuilder
{

  public CVEntryBuilder copy(@NotNull CVEntry entry);

  public CVEntryBuilder type(@NotNull CVType type);

  public CVEntryBuilder addDescription(Locale locale,
                                       @NotNull ResourceDescription description);

  public CVEntryBuilder addDescriptions(@NotNull Map<Locale, ResourceDescription> description);

  public CVEntryBuilder removeDescription(Locale locale);

  public CVEntryBuilder clearDescriptions();

  public CVEntryBuilder addFlag(@NotNull CVFlag flag);

  public CVEntryBuilder removeFlag(@NotNull CVFlag flag);

  public CVEntryBuilder clearFlags();

  public CVEntryBuilder defaultValue(int defaultValue);

  public CVEntryBuilder rangeMin(int rangeMin);

  public CVEntryBuilder rangeMax(int rangeMax);

  public CVEntryBuilder addAllowedValue(int value);

  public CVEntryBuilder addAllowedValues(@NotNull Collection<? extends Number> values);

  public CVEntryBuilder removeAllowedValue(int value);

  public CVEntryBuilder clearAllowedValues();

  public CVEntryBuilder valueMask(int mask);

  public CVEntryBuilder addBitDescriptor(@NotNull CVBitDescriptor descriptor);

  public CVEntryBuilder addBitDescriptors(@NotNull Collection<? extends CVBitDescriptor> descriptors);

  public CVEntryBuilder removeBitDescriptor(@NotNull CVBitDescriptor descriptor);

  public CVEntryBuilder clearBitDescriptors();

  public CVEntryBuilder setValue(CVValue value);

  public CVEntry build();

}
