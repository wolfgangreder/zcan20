/*
 * Copyright 2017-2019 Wolfgang Reder.
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
package at.or.reder.zcan20.util;

import java.io.InvalidClassException;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.logging.Level;

/**
 *
 * @author Wolfgang Reder
 */
public abstract class MockEnum implements Serializable
{

  private final short magic;
  private transient String toString;

  protected MockEnum(short magic)
  {
    this.magic = magic;
  }

  public final short getMagic()
  {
    return magic;
  }

  @Override
  public final int hashCode()
  {
    int hash = 7;
    hash = 13 * hash + this.magic;
    return hash;
  }

  @Override
  @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
  public final boolean equals(Object obj)
  {
    return (this == obj);
  }

  private Object readResolve() throws ObjectStreamException
  {
    Class<?> clazz = getClass();
    try {
      Method method = clazz.getMethod("valueOf",
                                      int.class);
      int mod = method.getModifiers();
      if (Modifier.isStatic(mod)) {
        boolean wasAccessible = method.isAccessible();
        try {
          method.setAccessible(true);
          return method.invoke(null,
                               magic);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
        } finally {
          method.setAccessible(wasAccessible);
        }
      }
    } catch (NoSuchMethodException | SecurityException ex) {
    }
    throw new InvalidClassException(clazz.getName(),
                                    "no static method valueOf(short) found");
  }

  protected abstract String getDefaultToString();

  private String getToString()
  {
    Class<?> clazz = getClass();
    for (Field f : clazz.getDeclaredFields()) {
      int mod = f.getModifiers();
      try {
        if (Modifier.isStatic(mod) && Modifier.isPublic(mod) && f.get(null) == this) {
          return f.getName();
        }
      } catch (IllegalArgumentException | IllegalAccessException ex) {
        Utils.LOGGER.log(Level.SEVERE,
                         null,
                         ex);
      }
    }
    return getDefaultToString();
  }

  @Override
  public final synchronized String toString()
  {
    if (toString == null) {
      toString = getToString();
    }
    return toString;
  }

}
