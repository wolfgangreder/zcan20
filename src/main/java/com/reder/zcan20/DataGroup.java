/*
 * Copyright 2017 Wolfgang Reder.
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
package com.reder.zcan20;

import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 *
 * @author Wolfgang Reder
 */
public final class DataGroup implements Serializable
{

  private static final ConcurrentMap<Short, DataGroup> INSTANCES = new ConcurrentHashMap<>();
  public static final DataGroup LOCO = valueOf(0x0000);
  public static final DataGroup TRAINS = valueOf(0x2f00);
  public static final DataGroup ACCESSORY = valueOf(0x3000);
  public static final DataGroup ACCESSORY_EXT = valueOf(0x3200);
  public static final DataGroup MX8 = valueOf(0x5040);
  public static final DataGroup MX9 = valueOf(0x5080);

  private final short magic;

  public static DataGroup valueOf(int magic)
  {
    return INSTANCES.computeIfAbsent(((short) magic),
                                     DataGroup::new);
  }

  private DataGroup(short magic)
  {
    this.magic = magic;
  }

  public short getMagic()
  {
    return magic;
  }

  @Override
  public int hashCode()
  {
    int hash = 3;
    hash = 31 * hash + this.magic;
    return hash;
  }

  @Override
  @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
  public boolean equals(Object obj)
  {
    return this == obj;
  }

  private Object readResolve() throws ObjectStreamException
  {
    return valueOf(magic);
  }

}
