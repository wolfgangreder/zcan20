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
package at.or.reder.zcan20.packet;

import at.or.reder.zcan20.CVReadState;
import at.or.reder.zcan20.CommandGroup;
import at.or.reder.zcan20.CommandMode;
import at.or.reder.zcan20.PacketSelector;
import at.or.reder.zcan20.impl.PacketSelectorImpl;
import at.or.reder.zcan20.util.ProxyPacketSelector;

/**
 *
 * @author Wolfgang Reder
 */
public interface CVInfoAdapter extends PacketAdapter
{

  public static final PacketSelector SELECTOR = new ProxyPacketSelector(new PacketSelectorImpl(CommandGroup.TRACK_CONFIG_PUBLIC,
                                                                                               CommandGroup.TSE_PROG_READ,
                                                                                               CommandMode.EVENT,
                                                                                               10),
                                                                        new PacketSelectorImpl(CommandGroup.TRACK_CONFIG_PUBLIC,
                                                                                               CommandGroup.TSE_PROG_READ,
                                                                                               CommandMode.ACK,
                                                                                               10),
                                                                        new PacketSelectorImpl(CommandGroup.TRACK_CONFIG_PRIVATE,
                                                                                               CommandGroup.TSE_PROG_READ,
                                                                                               CommandMode.EVENT,
                                                                                               10),
                                                                        new PacketSelectorImpl(CommandGroup.TRACK_CONFIG_PRIVATE,
                                                                                               CommandGroup.TSE_PROG_READ,
                                                                                               CommandMode.ACK,
                                                                                               10));

  public short getSystemID();

  public short getDecoderID();

  public int getNumber();

  public short getValue();

  public CVReadState getReadState();

}
