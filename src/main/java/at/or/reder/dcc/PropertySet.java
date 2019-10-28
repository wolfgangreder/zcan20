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
package at.or.reder.dcc;

import java.util.List;
import java.util.Map;

/**
 *
 * @author Wolfgang Reder
 */
public interface PropertySet
{

  public List<String> getPropertyNames();

  public String getReadableName(String propertyName) throws IllegalArgumentException;

  public String getPropertyDescription(String propertyName) throws IllegalArgumentException;

  public String getDefaultValue(String propertyName) throws IllegalArgumentException;

  public boolean isValueValid(String propertyName,
                              String property);

  /**
   * Erzeuge ein Map mit allen Eigenschaften und deren Standardwert.
   *
   * @return das (veränderbare) Map
   */
  public Map<String, String> getDefaultProperties();

}
