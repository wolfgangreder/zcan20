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

/**
 *
 * @author Wolfgang Reder
 */
public final class StringLocalizable extends Localizable<String>
{

  public StringLocalizable(boolean mutable)
  {
    super(mutable);
  }

  public StringLocalizable(String defaultValue)
  {
    super(defaultValue);
  }

  public StringLocalizable(String defaultValue,
                           boolean mutable)
  {
    super(defaultValue,
          mutable);
  }

  @Override
  protected boolean isValueValid(String value)
  {
    return value != null && !value.isBlank();
  }

}