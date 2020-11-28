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
package at.or.reder.zcan20.packet;

import at.or.reder.zcan20.CommandGroup;
import at.or.reder.zcan20.CommandMode;
import at.or.reder.zcan20.ModuleInfoType;
import at.or.reder.zcan20.PacketSelector;
import at.or.reder.zcan20.impl.PacketSelectorImpl;

/**
 *
 * @author Wolfgang Reder
 */
public interface ModuleInfoPacketAdapter extends PacketAdapter
{

  public static final PacketSelector SELECTOR = new PacketSelectorImpl(CommandGroup.CONFIG_CAN,
                                                                       CommandGroup.CONFIG_CAN_UNKNOWN_2,
                                                                       CommandMode.ACK,
                                                                       8);

  public Class<?> getValueClass();

  public ModuleInfoType getInfoType();

  public int getRawValue();

  public <C> C getValue(Class<? extends C> clazz) throws IllegalArgumentException;

}
