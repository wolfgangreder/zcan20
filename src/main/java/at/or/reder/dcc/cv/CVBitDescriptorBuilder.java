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
public interface CVBitDescriptorBuilder
{

  public CVBitDescriptorBuilder copy(CVBitDescriptor descriptor);

  public CVBitDescriptorBuilder bitMask(int bitMask);

  public CVBitDescriptorBuilder defaultValue(int defaultValue);

  public CVBitDescriptorBuilder addAllowedValue(int value);

  public CVBitDescriptorBuilder addAllowedValues(@NotNull Collection< ? extends Number> values);

  public CVBitDescriptorBuilder removeAllowedValue(int value);

  public CVBitDescriptorBuilder clearAllowedValues();

  public CVBitDescriptorBuilder addValueDescription(Locale locale,
                                                    int value,
                                                    ResourceDescription desc);

  public CVBitDescriptorBuilder addValueDescriptions(Locale locale,
                                                     @NotNull Map<Integer, ResourceDescription> names);

  public CVBitDescriptorBuilder removeValueDescription(Locale locale,
                                                       int value);

  public CVBitDescriptorBuilder clearValueDescriptions();

  public CVBitDescriptor build();

}
