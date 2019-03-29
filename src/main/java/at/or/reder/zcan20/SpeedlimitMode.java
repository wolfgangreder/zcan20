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
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

public final class SpeedlimitMode extends MockEnum implements Serializable
{

  public static final long serialVersionUID = 1L;

  private static final ConcurrentMap<Short, SpeedlimitMode> INSTANCES = new ConcurrentHashMap<>();

  public static final SpeedlimitMode NO_LIMIT = valueOf(0);
  public static final SpeedlimitMode NMRA = valueOf(1);
  public static final SpeedlimitMode ZIMO = valueOf(2);

  public static SpeedlimitMode valueOf(int magic)
  {
    return INSTANCES.computeIfAbsent((short) (magic & 0x03),
                                     SpeedlimitMode::new);
  }

  private SpeedlimitMode(short magic)
  {
    super(magic);
  }

  public static List<SpeedlimitMode> values()
  {
    return INSTANCES.values().stream().
            sorted(Comparator.comparing(SpeedlimitMode::getMagic)).
            collect(Collectors.toList());
  }

  @Override
  protected String getDefaultToString()
  {
    return "SPEED_LIMIT_0x" + Integer.toHexString(Short.toUnsignedInt(getMagic())).toUpperCase();
  }

}
