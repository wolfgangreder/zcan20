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
package at.or.reder.zcan20.packet;

import at.or.reder.dcc.PowerPort;
import at.or.reder.zcan20.CommandGroup;
import at.or.reder.zcan20.CommandMode;
import at.or.reder.zcan20.PacketSelector;
import at.or.reder.zcan20.PowerStateEx;
import at.or.reder.zcan20.impl.PacketSelectorImpl;
import at.or.reder.zcan20.util.ProxyPacketSelector;
import java.util.Set;

/**
 *
 * @author Wolfgang Reder
 */
public interface PowerInfoEx extends PacketAdapter
{

  public static final PacketSelector SELECTOR = new ProxyPacketSelector(new PacketSelectorImpl(CommandGroup.CONFIG,
                                                                                               CommandGroup.CONFIG_POWER_INFO,
                                                                                               CommandMode.EVENT,
                                                                                               22),
                                                                        new PacketSelectorImpl(CommandGroup.CONFIG,
                                                                                               CommandGroup.CONFIG_POWER_INFO,
                                                                                               CommandMode.ACK,
                                                                                               22));

  public short getSenderNid();

  public Set<PowerStateEx> getState(PowerPort port);

  public float getOutputVoltage(PowerPort port);

  public float getOutputCurrent(PowerPort port);

  public float getInputVoltage();

  public float getInputCurrent();

}
