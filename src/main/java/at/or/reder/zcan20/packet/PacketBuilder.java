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

import at.or.reder.zcan20.CommandGroup;
import at.or.reder.zcan20.CommandMode;
import at.or.reder.zcan20.DataGroup;
import at.or.reder.zcan20.InterfaceOptionType;
import at.or.reder.zcan20.LocoActive;
import at.or.reder.zcan20.ModuleInfoType;
import at.or.reder.zcan20.PowerMode;
import at.or.reder.dcc.PowerPort;
import at.or.reder.zcan20.PowerState;
import at.or.reder.zcan20.Protocol;
import at.or.reder.zcan20.SpeedFlags;
import at.or.reder.zcan20.SpeedSteps;
import at.or.reder.zcan20.SpeedlimitMode;
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

  public PacketBuilder adapterFactory(Function<Packet, PacketAdapter> adpaterFactory);

  public Packet build();

  public Packet buildLoginPacket();

  public default Packet buildPingPacket(short senderNID)
  {
    return commandMode(CommandMode.REQUEST).
            senderNID(senderNID).
            command(CommandGroup.NETWORK_PING).
            commandGroup(CommandGroup.NETWORK).
            build();
  }

  public Packet buildLogoutPacket(short masterNID);

  public Packet buildInterfaceOptionPacket(short objectNID,
                                           @NotNull InterfaceOptionType type);

  public Packet buildGetPowerModePacket(short systemNID,
                                        @NotNull @NotEmpty Set<? extends PowerPort> outputs);

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

  public Packet buildSystemPowerInfoPacket(short nid,
                                           @NotNull Collection<PowerPort> output);

  public Packet buildSystemPowerInfoPacket(short nid,
                                           @NotNull Collection<PowerPort> output,
                                           PowerMode mode);

  public Packet builderModulePowerInfoPacket(short nid,
                                             @NotNull PowerPort output,
                                             @NotNull PowerState state);

  public Packet buildModuleInfoPacket(short nid,
                                      @NotNull ModuleInfoType type);

}
