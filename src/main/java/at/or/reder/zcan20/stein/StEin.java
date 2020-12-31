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

import at.or.reder.dcc.util.Utils;
import java.util.List;
import java.util.stream.Collectors;

public interface StEin
{

  public int getAddress();

  public int getModuleNumber();

  public List<StEinObject> getObjects();

  public default List<StEinGA> getTracks()
  {
    return getObjects().stream().
            map((o) -> Utils.dynamicCast(o,
                                         StEinGA.class)).
            filter((o) -> o != null && o.getObjectClass() == ObjectClass.GA).
            collect(Collectors.toUnmodifiableList());
  }

  public default List<StEinGA> getTrackTemplates()
  {
    return getObjects().stream().
            map((o) -> Utils.dynamicCast(o,
                                         StEinGA.class)).
            filter((o) -> o != null && o.getObjectClass() == ObjectClass.GATYP).
            collect(Collectors.toUnmodifiableList());
  }

}
