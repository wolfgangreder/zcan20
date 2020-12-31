/*
 * Copyright 2020 Wolfgang Reder.
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
package at.or.reder.zcan20.stein;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Wolfgang Reder
 */
public class StEinBuilder
{

  private final List<StEinObject> objectList = new ArrayList<>();

  public StEinBuilder addObject(StEinObject obj)
  {
    if (obj != null) {
      objectList.add(obj);
    }
    return this;
  }

}
