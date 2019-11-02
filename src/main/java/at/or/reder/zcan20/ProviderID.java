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
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public final class ProviderID extends MockEnum implements Serializable {

  public static final long serialVersionUID = 1L;
  private static final ConcurrentMap<Short, ProviderID> VALUES = new ConcurrentHashMap<>();

  public static final ProviderID ZIMO = valueOf(0);
  public static final ProviderID ESTWGJ = valueOf(0x10);
  public static final ProviderID STP = valueOf(0x20);
  public static final ProviderID PFUSCH = valueOf(0x21);
  public static final ProviderID TRAINCONTROLLER = valueOf(0x30);
  public static final ProviderID TRAINPROGRAMMER = valueOf(0x31);
  public static final ProviderID RAILMANAGER = valueOf(0x40);
  public static final ProviderID WINDIGIPAT = valueOf(0x50);
  public static final ProviderID ITRAIN = valueOf(0x70);

  public static ProviderID valueOf(int magic) {
    return VALUES.computeIfAbsent((short) magic,
                                  ProviderID::new);
  }

  private ProviderID(short magic) {
    super(magic);
  }

  @Override
  protected String getDefaultToString() {
    return "PROVIDER_0x" + Integer.toHexString(Short.toUnsignedInt(getMagic())).
           toUpperCase();
  }

}
