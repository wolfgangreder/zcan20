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
package com.reder.zcan20.packet.impl;

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
import com.reder.zcan20.ZCANFactory;
import com.reder.zcan20.packet.Packet;
import com.reder.zcan20.packet.PacketAdapter;
import com.reder.zcan20.packet.PacketAdapterFactory;
import com.reder.zcan20.packet.PacketBuilder;
import com.reder.zcan20.util.Utils;
import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author Wolfgang Reder
 */
public final class DefaultPacketBuilder implements PacketBuilder
{

  private CommandGroup commandGroup;
  private CommandMode commandMode;
  private byte command;
  private short senderNID;
  private ByteBuffer data;
  private Function<? super Packet, ? extends PacketAdapter> adapterFactory;

  public DefaultPacketBuilder(short senderNID)
  {
    this.senderNID = checkNID(senderNID,
                              IllegalArgumentException::new);
  }

  private CommandGroup checkCommandGroup(CommandGroup grp,
                                         Function<String, ? extends RuntimeException> errorFactory)
  {
    Objects.requireNonNull(grp,
                           "CommandGroup is null");
    return grp;
  }

  private CommandMode checkCommandMode(CommandMode mode,
                                       Function<String, ? extends RuntimeException> errorFactory)
  {
    Objects.requireNonNull(mode,
                           "CommandMode is null");
    return mode;
  }

  private byte checkCommand(CommandGroup grp,
                            byte command,
                            boolean strictCheck,
                            Function<String, ? extends RuntimeException> errorFactory)
  {
    if (strictCheck) {
      if (!grp.isCommandAllowed(command)) {
        throw errorFactory.apply("Command 0x" + Integer.toHexString(command) + " is not allowed");
      }
    } else {
      if ((command & 0x3f) != command) {
        throw errorFactory.apply("Command greater than 0x3f");
      }
    }
    return command;
  }

  private short checkNID(short nid,
                         Function<String, ? extends RuntimeException> errorFactory)
  {
    return nid;
  }

  @Override
  public PacketBuilder commandGroup(CommandGroup commandGroup)
  {
    this.commandGroup = checkCommandGroup(commandGroup,
                                          IllegalArgumentException::new);
    return this;
  }

  @Override
  public PacketBuilder commandMode(CommandMode commandMode)
  {
    this.commandMode = checkCommandMode(commandMode,
                                        IllegalArgumentException::new);
    return this;
  }

  @Override
  public PacketBuilder command(byte command)
  {
    this.command = checkCommand(commandGroup,
                                command,
                                false,
                                IllegalArgumentException::new);
    return this;
  }

  @Override
  public PacketBuilder senderNID(short senderNID)
  {
    this.senderNID = checkNID(senderNID,
                              IllegalArgumentException::new);
    return this;
  }

  @Override
  public PacketBuilder data(ByteBuffer data)
  {
    if (data != null && data.remaining() > 0) {
      this.data = ByteBuffer.allocate(data.remaining());
      this.data.put(data);
    } else {
      this.data = null;
    }
    return this;
  }

  @Override
  public PacketBuilder adapterFactory(Function<? super Packet, ? extends PacketAdapter> adpaterFactory)
  {
    this.adapterFactory = adpaterFactory;
    return this;
  }

  private Function<Packet, ? extends PacketAdapter> createAdapterFactory()
  {
    for (PacketAdapterFactory ef : Lookups.forPath(Packet.LOOKUPPATH).lookupAll(PacketAdapterFactory.class)) {
      if (ef.isValid(commandGroup,
                     command,
                     commandMode)) {
        return ef::createAdapter;
      }
    }
    return null;
  }

  @Override
  public Packet build()
  {
    final Function<String, RuntimeException> exFactory = IllegalStateException::new;
    if (adapterFactory == null) {
      adapterFactory = createAdapterFactory();
    }
    if (data != null) {
      data.rewind();
    }
    Packet result = new DefaultPacket(checkCommandGroup(commandGroup,
                                                        exFactory),
                                      checkCommandMode(commandMode,
                                                       exFactory),
                                      checkCommand(commandGroup,
                                                   command,
                                                   true,
                                                   exFactory),
                                      checkNID(senderNID,
                                               exFactory),
                                      data,
                                      adapterFactory);
    if (data != null) {
      data.clear();
    }
    return result;
  }

  @Override
  public Packet buildLoginPacket()
  {
    commandGroup(CommandGroup.NETWORK);
    command(CommandGroup.NETWORK_PORT_OPEN);
    commandMode(CommandMode.COMMAND);
    adapterFactory(null);
    data(null);
    return build();
  }

  @Override
  public Packet buildLogoutPacket(short masterNID)
  {
    commandGroup(CommandGroup.NETWORK);
    command(CommandGroup.NETWORK_PORT_CLOSE);
    commandMode(CommandMode.COMMAND);
    adapterFactory(null);
    ByteBuffer buffer = Utils.allocateLEBuffer(2);
    buffer.putShort(masterNID);
    buffer.clear();
    data(buffer);
    return build();
  }

  @Override
  public Packet buildInterfaceOptionPacket(short objectNID,
                                           InterfaceOptionType type)
  {
    Objects.requireNonNull(type,
                           "type is null");
    commandGroup(CommandGroup.NETWORK);
    command(CommandGroup.NETWORK_INTERFACE_OPTION);
    commandMode(CommandMode.REQUEST);
    adapterFactory(null);
    ByteBuffer buffer = Utils.allocateLEBuffer(4);
    buffer.putShort(objectNID);
    buffer.putShort(type.getMagic());
    buffer.clear();
    data(buffer);
    return build();
  }

  @Override
  public Packet buildGetPowerModePacket(short systemNID,
                                        Set<? extends PowerOutput> outputs)
  {
    Objects.requireNonNull(outputs,
                           "outputs is null");
    if (outputs.isEmpty()) {
      throw new IllegalArgumentException("outputs is empty");
    }
    if (outputs.contains(PowerOutput.UNKNOWN)) {
      throw new IllegalArgumentException("ouputs contains unknown");
    }
    commandGroup(CommandGroup.SYSTEM);
    command(CommandGroup.SYSTEM_POWER);
    commandMode(CommandMode.REQUEST);
    adapterFactory(null);
    ByteBuffer buffer = Utils.allocateLEBuffer(3);
    buffer.putShort(systemNID);
    buffer.put(PowerOutput.toValue(outputs));
    buffer.clear();
    data(buffer);
    return build();
  }

  @Override
  public Packet buildLocoStatePacket(short locoID)
  {
    int li = Short.toUnsignedInt(locoID);
    if (li > ZCANFactory.LOCO_MAX) {
      throw new IllegalArgumentException("locoID out of range");
    }
    commandGroup(CommandGroup.LOCO);
    commandMode(CommandMode.REQUEST);
    command(CommandGroup.LOCO_STATE);
    adapterFactory(null);
    ByteBuffer buffer = Utils.allocateLEBuffer(2);
    buffer.putShort(locoID);
    buffer.clear();
    data(buffer);
    return build();
  }

  @Override
  public Packet buildLocoModePacket(short locoID)
  {
    int li = Short.toUnsignedInt(locoID);
    if (li > ZCANFactory.LOCO_MAX) {
      throw new IllegalArgumentException("locoID out of range");
    }
    commandGroup(CommandGroup.LOCO);
    commandMode(CommandMode.REQUEST);
    command(CommandGroup.LOCO_MODE);
    adapterFactory(null);
    ByteBuffer buffer = Utils.allocateLEBuffer(2);
    buffer.putShort(locoID);
    buffer.clear();
    data(buffer);
    return build();
  }

  @Override
  public Packet buildLocoModePacket(short locoID,
                                    SpeedSteps steps,
                                    Protocol protocol,
                                    int numFunctions,
                                    SpeedlimitMode limitMode,
                                    boolean pulseFx,
                                    boolean analogFx)
  {
    int li = Short.toUnsignedInt(locoID);
    if (li > ZCANFactory.LOCO_MAX) {
      throw new IllegalArgumentException("locoID out of range");
    }
    Objects.requireNonNull(steps,
                           "steps is null");
    if (!steps.isValidInSet()) {
      throw new IllegalArgumentException("steps is unknown");
    }
    Objects.requireNonNull(protocol,
                           "protocol is null");
    if (!protocol.isValidInSet()) {
      throw new IllegalArgumentException("protocol is unknown or undefined");
    }
    if (numFunctions < 0 || numFunctions > 32) {
      throw new IllegalArgumentException("numFunctions out of range");
    }
    Objects.requireNonNull(limitMode,
                           "limitMode is null");
    commandGroup(CommandGroup.LOCO);
    commandMode(CommandMode.COMMAND);
    command(CommandGroup.LOCO_MODE);
    adapterFactory(null);
    ByteBuffer buffer = Utils.allocateLEBuffer(5);
    buffer.putShort(locoID);
    int m1 = (steps.getMagic() & 0x0f) + ((protocol.getMagic() & 0x0f) << 4);
    int m3 = (limitMode.getMagic() << 2) + (pulseFx ? 1 : 0) + (analogFx ? 2 : 0);
    buffer.put((byte) m1);
    buffer.put((byte) numFunctions);
    buffer.put((byte) m3);
    buffer.clear();
    data(buffer);
    return build();
  }

  @Override
  public Packet buildLocoSpeedPacket(short locoID)
  {
    int li = Short.toUnsignedInt(locoID);
    if (li > ZCANFactory.LOCO_MAX) {
      throw new IllegalArgumentException("locoID out of range");
    }
    commandGroup(CommandGroup.LOCO);
    commandMode(CommandMode.REQUEST);
    command(CommandGroup.LOCO_SPEED);
    adapterFactory(null);
    ByteBuffer buffer = Utils.allocateLEBuffer(2);
    buffer.putShort(locoID);
    buffer.clear();
    data(buffer);
    return build();
  }

  @Override
  public Packet buildLocoSpeedPacket(@Min(0) @Max(0x27ff) short locoID,
                                     @Min(0) @Max(0x3ff) short speed,
                                     @NotNull Collection<? extends SpeedFlags> speedFlags,
                                     @Min(1) short speedDivisor)
  {
    int li = Short.toUnsignedInt(locoID);
    if (li > ZCANFactory.LOCO_MAX) {
      throw new IllegalArgumentException("locoID out of range");
    }
    li = Short.toUnsignedInt(speed);
    if (li > 0x3ff) {
      throw new IllegalArgumentException("speed aout of range");
    }
    Objects.requireNonNull(speedFlags,
                           "speedFlags is null");
    short sf = (short) (speed & 0x3ff);
    sf += SpeedFlags.maskOfSet(speedFlags);
    if (speedDivisor < 1) {
      throw new IllegalArgumentException("speedDivisor smaller than 1");
    }
    commandGroup(CommandGroup.LOCO);
    commandMode(CommandMode.COMMAND);
    command(CommandGroup.LOCO_SPEED);
    adapterFactory(null);
    ByteBuffer buffer = Utils.allocateLEBuffer(6);
    buffer.putShort(locoID);
    buffer.putShort(sf);
    buffer.put(Utils.byte1(speedDivisor));
    buffer.put((byte) 0);
    buffer.clear();
    data(buffer);
    return build();
  }

  @Override
  public Packet buildLocoFunctionInfoPacket(short locoID)
  {
    int li = Short.toUnsignedInt(locoID);
    if (li > ZCANFactory.LOCO_MAX) {
      throw new IllegalArgumentException("locoID out of range");
    }
    commandGroup(CommandGroup.LOCO);
    commandMode(CommandMode.REQUEST);
    command(CommandGroup.LOCO_FUNC_INFO);
    adapterFactory(null);
    ByteBuffer buffer = Utils.allocateLEBuffer(2);
    buffer.putShort(locoID);
    buffer.clear();
    data(buffer);
    return build();
  }

  @Override
  public Packet buildLocoFunctionPacket(short locoID)
  {
    int li = Short.toUnsignedInt(locoID);
    if (li > ZCANFactory.LOCO_MAX) {
      throw new IllegalArgumentException("locoID out of range");
    }
    commandGroup(CommandGroup.LOCO);
    commandMode(CommandMode.REQUEST);
    command(CommandGroup.LOCO_FUNC_SWITCH);
    adapterFactory(null);
    ByteBuffer buffer = Utils.allocateLEBuffer(2);
    buffer.putShort(locoID);
    buffer.clear();
    data(buffer);
    return build();
  }

  @Override
  public Packet buildLocoFunctionPacket(short locoID,
                                        short fxNumber,
                                        short fxValue)
  {
    int li = Short.toUnsignedInt(locoID);
    if (li > ZCANFactory.LOCO_MAX) {
      throw new IllegalArgumentException("locoID out of range");
    }
    li = Short.toUnsignedInt(fxNumber);
    if (li > 255) {
      throw new IllegalArgumentException("fxNumber out ofRange");
    }
    commandGroup(CommandGroup.LOCO);
    commandMode(CommandMode.COMMAND);
    command(CommandGroup.LOCO_FUNC_SWITCH);
    adapterFactory(null);
    ByteBuffer buffer = Utils.allocateLEBuffer(6);
    buffer.putShort(locoID);
    buffer.putShort(fxNumber);
    buffer.putShort(fxValue);
    buffer.clear();
    data(buffer);
    return build();
  }

  @Override
  public Packet buildLocoActivePacket(short locoID)
  {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public Packet buildLocoActivePacket(short locoID,
                                      LocoActive mode)
  {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public Packet buildReadCVPacket(short systemID,
                                  short locoID,
                                  int cvNumber)
  {
    commandGroup(CommandGroup.TRACK_CONFIG_PUBLIC);
    commandMode(CommandMode.REQUEST);
    command(CommandGroup.TSE_PROG_READ);
    adapterFactory(null);
    ByteBuffer buffer = Utils.allocateLEBuffer(8);
    buffer.putShort(systemID);
    buffer.putShort(locoID);
    buffer.putInt(cvNumber);
    buffer.clear();
    data(buffer);
    return build();
  }

  @Override
  public Packet buildWriteCVPacket(short systemID,
                                   short locoID,
                                   int cvNumber,
                                   short value)
  {
    commandGroup(CommandGroup.TRACK_CONFIG_PUBLIC);
    commandMode(CommandMode.COMMAND);
    command(CommandGroup.TSE_PROG_WRITE);
    adapterFactory(null);
    ByteBuffer buffer = Utils.allocateLEBuffer(10);
    buffer.putShort(systemID);
    buffer.putShort(locoID);
    buffer.putInt(cvNumber);
    buffer.putShort(value);
    buffer.clear();
    data(buffer);
    return build();
  }

  @Override
  public Packet buildDataGroupCountPacket(short masterNID,
                                          DataGroup dataGroup)
  {
    Objects.requireNonNull(dataGroup,
                           "dataGroup is null");
    commandGroup(CommandGroup.DATA);
    commandMode(CommandMode.REQUEST);
    command(CommandGroup.DATA_GROUP_COUNT);
    adapterFactory(null);
    ByteBuffer buffer = Utils.allocateLEBuffer(4);
    buffer.putShort(masterNID);
    buffer.putShort(dataGroup.getMagic());
    buffer.rewind();
    data(buffer);
    return build();
  }

  @Override
  public Packet buildDataPacket(short masterNID,
                                DataGroup dataGroup,
                                short index)
  {
    Objects.requireNonNull(dataGroup,
                           "dataGroup is null");
    commandGroup(CommandGroup.DATA);
    commandMode(CommandMode.REQUEST);
    command(CommandGroup.DATA_ITEMLIST_INDEX);
    adapterFactory(null);
    ByteBuffer buffer = Utils.allocateLEBuffer(6);
    buffer.putShort(masterNID);
    buffer.putShort(dataGroup.getMagic());
    buffer.putShort(index);
    buffer.rewind();
    data(buffer);
    return build();
  }

  @Override
  public Packet buildDataPacket(short masterNID,
                                short nid)
  {
    commandGroup(CommandGroup.DATA);
    commandMode(CommandMode.REQUEST);
    command(CommandGroup.DATA_ITEMLIST_NID);
    adapterFactory(null);
    ByteBuffer buffer = Utils.allocateLEBuffer(4);
    buffer.putShort(masterNID);
    buffer.putShort(nid);
    buffer.rewind();
    data(buffer);
    return build();
  }

  @Override
  public Packet buildDataNameExt(short masterNID,
                                 short objectNID,
                                 int subID,
                                 int val1,
                                 int val2)
  {
    commandGroup(CommandGroup.DATA);
    commandMode(CommandMode.REQUEST);
    command(CommandGroup.DATA_NAME_EXT);
    adapterFactory(null);
    ByteBuffer buffer = Utils.allocateLEBuffer(16);
    buffer.putShort(masterNID);
    buffer.putShort(objectNID);
    buffer.putInt(subID);
    buffer.putInt(val1);
    buffer.putInt(val2);
    buffer.rewind();
    data(buffer);
    return build();
  }

  @Override
  public Packet buildModulePowerInfoPacket(short nid,
                                           PowerOutput output)
  {
    Objects.requireNonNull(output,
                           "output is null");
    commandGroup(CommandGroup.CONFIG);
    commandMode(CommandMode.REQUEST);
    command(CommandGroup.CONFIG_POWER_INFO);
    adapterFactory(null);
    ByteBuffer buffer = Utils.allocateLEBuffer(3);
    buffer.putShort(nid);
    switch (output) {
      case OUT_1:
        buffer.put((byte) 0);
        break;
      case OUT_2:
        buffer.put((byte) 1);
        break;
      case BOOSTER:
        buffer.put((byte) 2);
        break;
      default:
        throw new IllegalArgumentException("Illegal power output");
    }
    buffer.rewind();
    data(buffer);
    return build();
  }

  @Override
  public Packet buildModuleInfoPacket(short nid,
                                      ModuleInfoType type)
  {
    Objects.requireNonNull(type,
                           "type is null");
    commandGroup(CommandGroup.CONFIG);
    commandMode(CommandMode.REQUEST);
    command(CommandGroup.CONFIG_MODULE_INFO);
    adapterFactory(null);
    ByteBuffer buffer = Utils.allocateLEBuffer(4);
    buffer.putShort(nid);
    buffer.putShort(type.getMagic());
    buffer.rewind();
    data(buffer);
    return build();
  }

}
