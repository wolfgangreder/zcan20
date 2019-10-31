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

import at.or.reder.zcan20.util.ResourceDescription;
import java.util.Locale;
import java.util.Map;
import javax.validation.constraints.NotNull;

/**
 *
 * @author Wolfgang Reder
 */
public interface EnumeratedValueBuilder
{

  public EnumeratedValueBuilder copy(EnumeratedValue value);

  public EnumeratedValueBuilder value(int value);

  public EnumeratedValueBuilder addDescription(Locale locale,
                                               @NotNull ResourceDescription description);

  public EnumeratedValueBuilder addDescriptions(@NotNull Map<Locale, ResourceDescription> description);

  public EnumeratedValueBuilder removeDescription(Locale locale);

  public EnumeratedValueBuilder clearDescriptions();

  public EnumeratedValue build();

}
