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

import at.or.reder.dcc.cv.impl.CVValueImpl;
import javax.validation.constraints.NotNull;

/**
 *
 * @author Wolfgang Reder
 */
public final class CVSetFactory
{

  public static CVValue createValue(@NotNull CVValueState state,
                                    int value,
                                    CVEntry entry)
  {
    return new CVValueImpl(state,
                           value,
                           entry);
  }

}
