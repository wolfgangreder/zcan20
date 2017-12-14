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
import com.reder.zcan20.InterfaceOptionType;
import com.reder.zcan20.LocoActive;
import com.reder.zcan20.ModuleInfoType;
import com.reder.zcan20.PowerOutput;
import com.reder.zcan20.Protocol;
import com.reder.zcan20.SpeedFlags;
import com.reder.zcan20.SpeedSteps;
import com.reder.zcan20.SpeedlimitMode;
import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.Set;
import java.util.function.Function;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
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

  public PacketBuilder command(@Min(0) @Max(0x3f) byte command);

  public PacketBuilder senderNID(short senderNID);

  public PacketBuilder data(ByteBuffer data);

  public PacketBuilder adapterFactory(Function<? super Packet, ? extends PacketAdapter> adpaterFactory);

  public Packet build();

  public Packet buildLoginPacket();

  public Packet buildLogoutPacket(short masterNID);

  public Packet buildInterfaceOptionPacket(short objectNID,
                                           @NotNull InterfaceOptionType type);

  public Packet buildGetPowerModePacket(short systemNID,
                                        @NotNull @NotEmpty Set<? extends PowerOutput> outputs);

  public Packet buildLocoStatePacket(@Min(0) @Max(0x27ff) short locoID);

  public Packet buildLocoModePacket(@Min(0) @Max(0x27ff) short locoID);

  public Packet buildLocoModePacket(@Min(0) @Max(0x27ff) short locoID,
                                    @NotNull SpeedSteps steps,
                                    @NotNull Protocol protocol,
                                    @Min(0) @Max(32) int numFunctions,
                                    @NotNull SpeedlimitMode limitMode,
                                    boolean pulseFx,
                                    boolean analogFx);

  public Packet buildLocoSpeedPacket(@Min(0) @Max(0x27ff) short locoID);

  public Packet buildLocoSpeedPacket(@Min(0) @Max(0x27ff) short locoID,
                                     @Min(0) @Max(0x3ff) short speed,
                                     @NotNull Collection<? extends SpeedFlags> speedFlags,
                                     @Min(1) short speedDivisor);

  public Packet buildLocoFunctionInfoPacket(@Min(0) @Max(0x27ff) short locoID);

  public Packet buildLocoFunctionPacket(@Min(0) @Max(0x27ff) short locoID);

  public Packet buildLocoFunctionPacket(@Min(0) @Max(0x27ff) short locoID,
                                        @Min(0) @Max(255) short fxNumber,
                                        short fxValue);

  public Packet buildLocoActivePacket(@Min(0) @Max(0x27ff) short locoID);

  public Packet buildLocoActivePacket(@Min(0) @Max(0x27ff) short locoID,
                                      @NotNull LocoActive mode);

  public Packet buildReadCVPacket(short systemID,
                                  short locoID,
                                  int cvNumber);

  public Packet buildWriteCVPacket(short systemID,
                                   short locoID,
                                   int cvNumber,
                                   short value);

  public Packet buildDataGroupCountPacket(short masterNID,
                                          @NotNull DataGroup dataGroup);

  public Packet buildDataPacket(short masterNID,
                                @NotNull DataGroup dataGroup,
                                short index);

  public Packet buildDataPacket(short masterNID,
                                short nid);

  public Packet buildDataNameExt(short masterNID,
                                 short objectNID,
                                 int subID,
                                 int val1,
                                 int val2);

  public default Packet buildLocoNameExt(short masterNID,
                                         short objectNID)
  {
    return buildDataNameExt(masterNID,
                            objectNID,
                            0,
                            0,
                            0);
  }

  public Packet buildModulePowerInfoPacket(short nid,
                                           @NotNull PowerOutput output);

  public Packet buildModuleInfoPacket(short nid,
                                      @NotNull ModuleInfoType type);

}
