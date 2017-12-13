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
import com.reder.zcan20.LocoActive;
import com.reder.zcan20.ModuleInfoType;
import com.reder.zcan20.PowerMode;
import com.reder.zcan20.PowerOutput;
import com.reder.zcan20.Protocol;
import com.reder.zcan20.SpeedFlags;
import com.reder.zcan20.SpeedSteps;
import com.reder.zcan20.SpeedlimitMode;
import com.reder.zcan20.packet.Packet;
import static com.reder.zcan20.packet.Packet.LOOKUPPATH;
import com.reder.zcan20.packet.PacketAdapter;
import com.reder.zcan20.packet.PacketAdapterFactory;
import com.reder.zcan20.packet.PacketBuilder;
import com.reder.zcan20.util.Utils;
import java.nio.ByteBuffer;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
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
    for (PacketAdapterFactory ef : Lookups.forPath(LOOKUPPATH).lookupAll(PacketAdapterFactory.class)) {
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
    try {
      return new DefaultPacket(checkCommandGroup(commandGroup,
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
    } finally {
      if (data != null) {
        data.clear();
      }
    }
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
  public Packet buildPowerModePacket(short systemNID,
                                     Set<? extends PowerOutput> outputs,
                                     PowerMode mode)
  {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public Packet buildLocoStatePacket(short locoID)
  {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public Packet buildLocoModePacket(short locoID)
  {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public Packet buildLocoSpeedPacket(short locoID,
                                     short speed,
                                     Set<? extends SpeedFlags> speedFlags,
                                     byte speedDivisor)
  {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public Packet buildLocoFunctionPacket(short locoID,
                                        short fxNumber,
                                        short fxValue)
  {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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
  public Packet buildReadCVPacket(short locoID,
                                  int cvNumber)
  {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public Packet buildWriteCVPacket(short locoID,
                                   int cvNumber,
                                   short value)
  {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public Packet buildConfigPacket(short nid,
                                  PowerOutput output)
  {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public Packet buildModuleInfoPacket(short nid,
                                      ModuleInfoType type)
  {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

}
