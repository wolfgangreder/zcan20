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
package com.reder.zcan20.packet;

import com.reder.zcan20.CommandGroup;
import com.reder.zcan20.CommandMode;
import com.reder.zcan20.DataGroup;
import com.reder.zcan20.LocoActive;
import com.reder.zcan20.ModuleInfoType;
import com.reder.zcan20.PowerMode;
import com.reder.zcan20.PowerOutput;
import com.reder.zcan20.Protocol;
import com.reder.zcan20.SpeedFlags;
import com.reder.zcan20.SpeedSteps;
import com.reder.zcan20.SpeedlimitMode;
import java.nio.ByteBuffer;
import java.util.Set;
import javax.validation.constraints.NotNull;

/**
 * Factory class for building Packets.
 *
 * @author Wolfgang Reder
 */
public interface PacketBuilder
{

  public PacketBuilder commandGroup(@NotNull CommandGroup commandGroup);

  public PacketBuilder commandMode(@NotNull CommandMode commandMode);

  public PacketBuilder command(byte command);

  public PacketBuilder senderNID(short senderNID);

  public PacketBuilder data(@NotNull ByteBuffer data);

  public Packet build();

  public Packet buildLoginPacket();

  public Packet buildLogoutPacket(short masterNID);

  public Packet buildPowerModePacket(short systemNID,
                                     @NotNull Set<? extends PowerOutput> outputs,
                                     @NotNull PowerMode mode);

  public Packet buildLocoStatePacket(short locoID);

  public Packet buildLocoModePacket(short locoID);

  public Packet buildLocoModePacket(short locoID,
                                    @NotNull SpeedSteps steps,
                                    @NotNull Protocol protocol,
                                    int numFunctions,
                                    @NotNull SpeedlimitMode limitMode,
                                    boolean pulseFx,
                                    boolean analogFx);

  public Packet buildLocoSpeedPacket(short locoID,
                                     short speed,
                                     @NotNull Set<? extends SpeedFlags> speedFlags,
                                     byte speedDivisor);

  public Packet buildLocoFunctionPacket(short locoID,
                                        short fxNumber,
                                        short fxValue);

  public Packet buildLocoActivePacket(short locoID);

  public Packet buildLocoActivePacket(short locoID,
                                      @NotNull LocoActive mode);

  public Packet buildReadCVPacket(short locoID,
                                  int cvNumber);

  public Packet buildWriteCVPacket(short locoID,
                                   int cvNumber,
                                   short value);

  public Packet buildDataGroupCountPacket(short masterNID,
                                          @NotNull DataGroup dataGroup);

  public Packet buildDataPacket(short masterNID,
                                @NotNull DataGroup dataGroup,
                                short index);

  public Packet buildDataPacket(short masterNID,
                                short nid);

  public Packet buildConfigPacket(short nid,
                                  @NotNull PowerOutput output);

  public Packet buildModuleInfoPacket(short nid,
                                      @NotNull ModuleInfoType type);

}
