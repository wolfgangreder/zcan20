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

import at.or.reder.zcan20.util.MockEnum;
import java.io.Serializable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 *
 * @author Wolfgang Reder
 */
public final class ModuleInfoType extends MockEnum implements Serializable
{

  public static final long serialVersionUID = 1L;
  private static final ConcurrentMap<Short, ModuleInfoType> INSTANCES = new ConcurrentHashMap<>();
  public static final ModuleInfoType HW_VERSION = valueOf(1);
  public static final ModuleInfoType SW_VERSION = valueOf(2);
  public static final ModuleInfoType SW_BUILD_DATE = valueOf(3);
  public static final ModuleInfoType SW_BUILD_TIME = valueOf(4);

  public static ModuleInfoType valueOf(int type)
  {
    return INSTANCES.computeIfAbsent((short) (type & 0xffff),
                                     ModuleInfoType::new);
  }

  private ModuleInfoType(short magic)
  {
    super(magic);
  }

  @Override
  protected String getDefaultToString()
  {
    return "MODULE_INFO_0x" + Integer.toHexString(Short.toUnsignedInt(getMagic())).toUpperCase();
  }

}
