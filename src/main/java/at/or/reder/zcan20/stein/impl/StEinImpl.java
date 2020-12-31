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
package at.or.reder.zcan20.stein.impl;

import at.or.reder.dcc.util.Utils;
import at.or.reder.zcan20.stein.StEin;
import at.or.reder.zcan20.stein.StEinObject;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

public final class StEinImpl implements StEin
{

  private final int address;
  private final int moduleNumber;
  private final List<StEinObject> objects;

  public StEinImpl(int address,
                   int moduleNumber,
                   Collection<? extends StEinObject> objects)
  {
    this.address = address;
    this.moduleNumber = moduleNumber;
    this.objects = Utils.copyToUnmodifiableList(objects,
                                                (i) -> i != null);
  }

  @Override
  public int getAddress()
  {
    return address;
  }

  @Override
  public int getModuleNumber()
  {
    return moduleNumber;
  }

  @Override
  public List<StEinObject> getObjects()
  {
    return objects;
  }

  @Override
  public int hashCode()
  {
    int hash = 7;
    hash = 97 * hash + this.moduleNumber;
    hash = 97 * hash + Objects.hashCode(this.objects);
    return hash;
  }

  @Override
  public boolean equals(Object obj)
  {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final StEinImpl other = (StEinImpl) obj;
    if (this.moduleNumber != other.moduleNumber) {
      return false;
    }
    return Objects.equals(this.objects,
                          other.objects);
  }

  @Override
  public String toString()
  {
    return "StEin #" + getModuleNumber();
  }

}
