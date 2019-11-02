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
package at.or.reder.zcan20;

import at.or.reder.dcc.util.MockEnum;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.IntFunction;

/**
 *
 * @author Wolfgang Reder
 */
public final class ModuleInfoType extends MockEnum implements Serializable
{

  public static final long serialVersionUID = 1L;
  private static final ConcurrentMap<Short, ModuleInfoType> INSTANCES = new ConcurrentHashMap<>();
  public static final ModuleInfoType HW_VERSION = valueOf(1,
                                                          String.class,
                                                          Integer::toHexString);
  public static final ModuleInfoType SW_VERSION = valueOf(2,
                                                          String.class,
                                                          Integer::toHexString);
  public static final ModuleInfoType SW_BUILD_DATE = valueOf(3,
                                                             LocalDate.class,
                                                             ModuleInfoType::convertToLocalDate);
  public static final ModuleInfoType SW_BUILD_TIME = valueOf(4,
                                                             LocalTime.class,
                                                             ModuleInfoType::convertToLocalTime);
  public static final ModuleInfoType RT_DATE = valueOf(5,
                                                       LocalDate.class,
                                                       ModuleInfoType::convertToLocalDate);
  public static final ModuleInfoType RT_TIME = valueOf(6,
                                                       LocalTime.class,
                                                       ModuleInfoType::convertToLocalTime);
  public static final ModuleInfoType MIWI_HW_VERSION = valueOf(8,
                                                               String.class,
                                                               Integer::toHexString);
  public static final ModuleInfoType MIWI_SW_VERSION = valueOf(9,
                                                               String.class,
                                                               Integer::toHexString);
  public static final ModuleInfoType MIWI_CHANNEL = valueOf(10,
                                                            Integer.class,
                                                            null);
  public static final ModuleInfoType MODULE_NUMBER = valueOf(20,
                                                             Integer.class,
                                                             null);
  public static final ModuleInfoType MODULE_KIND = valueOf(100,
                                                           Integer.class,
                                                           null);

  private static <V> ModuleInfoType valueOf(int type,
                                            Class<? extends V> valueClass,
                                            IntFunction<? super V> converter)
  {
    return INSTANCES.computeIfAbsent((short) (type & 0xffff),
                                     (m) -> new ModuleInfoType(m,
                                                               valueClass,
                                                               converter));
  }

  public static ModuleInfoType valueOf(int type)
  {
    return INSTANCES.computeIfAbsent((short) (type & 0xffff),
                                     ModuleInfoType::new);
  }

  private final Class<?> valueClass;
  private final IntFunction<?> converter;

  private ModuleInfoType(short magic)
  {
    this(magic,
         null,
         null);
  }

  private <V> ModuleInfoType(short magic,
                             Class<? extends V> valueClass,
                             IntFunction<? super V> converter)
  {
    super(magic);
    this.valueClass = valueClass != null ? valueClass : Integer.class;
    if (converter != null) {
      this.converter = converter;
    } else {
      this.converter = Integer::valueOf;
    }
  }

  public Class<?> getValueClass()
  {
    return valueClass;
  }

  public Object convertValue(int rawValue)
  {
    return converter.apply(rawValue);
  }

  private static LocalDate convertToLocalDate(int i)
  {
    int day = i & 0xff;
    int month = (i & 0xff00) >> 8;
    int year = (i & 0xffff0000) >> 16;
    return LocalDate.of(year,
                        month,
                        day);
  }

  private static LocalTime convertToLocalTime(int i)
  {
    int second = (i & 0xff00) >> 8;
    int minute = (i & 0xff0000) >> 16;
    int hour = (i & 0xff000000) >> 24;
    return LocalTime.of(hour,
                        minute,
                        second);
  }

  @Override
  protected String getDefaultToString()
  {
    return "MODULE_INFO_0x" + Integer.toHexString(Short.toUnsignedInt(getMagic())).toUpperCase();
  }

}
