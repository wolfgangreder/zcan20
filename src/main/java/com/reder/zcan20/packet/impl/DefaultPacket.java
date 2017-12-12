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
import com.reder.zcan20.packet.Packet;
import com.reder.zcan20.packet.SpecialisationFactory;
import com.reder.zcan20.util.Utils;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Objects;
import java.util.function.Function;
import javax.validation.constraints.NotNull;
import org.openide.util.Lookup;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author Wolfgang Reder
 */
public final class DefaultPacket implements Packet
{

  public static final int MAX_DLC = 12;

  public static final class Builder
  {

    private CommandGroup group;
    private CommandMode mode;
    private byte command;
    private short senderNID = -1;
    private byte[] data;
    private Function<Packet, ?> extensionFactory;

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

    public Builder copy(@NotNull Packet packet)
    {
      Objects.requireNonNull(packet,
                             "packet is null");
      final Function<String, RuntimeException> errorFactory = IllegalArgumentException::new;
      checkCommandGroup(packet.getCommandGroup(),
                        errorFactory);
      checkCommandMode(packet.getCommandMode(),
                       errorFactory);
      checkCommand(packet.getCommandGroup(),
                   packet.getCommand(),
                   false,
                   errorFactory);
      checkNID(packet.getSenderNID(),
               errorFactory);
      group = packet.getCommandGroup();
      mode = packet.getCommandMode();
      command = packet.getCommand();
      senderNID = packet.getSenderNID();
      ByteBuffer pd = packet.getData();
      if (pd.remaining() == 0) {
        data = null;
      } else {
        data = new byte[pd.remaining()];
        pd.get(data);
      }
      return this;
    }

    public Builder extensionFactory(Function<Packet, ?> extensionFactory)
    {
      this.extensionFactory = extensionFactory;
      return this;
    }

    public Builder commandGroup(CommandGroup grp)
    {
      this.group = checkCommandGroup(grp,
                                     IllegalArgumentException::new);
      return this;
    }

    public Builder commandMode(CommandMode mode)
    {
      this.mode = checkCommandMode(mode,
                                   IllegalArgumentException::new);
      return this;
    }

    public Builder command(byte cmd)
    {
      this.command = checkCommand(group,
                                  cmd,
                                  false,
                                  IllegalArgumentException::new);
      return this;
    }

    public Builder senderNID(short senderNID)
    {
      this.senderNID = checkNID(senderNID,
                                IllegalArgumentException::new);
      return this;
    }

    public Builder data(byte... data)
    {
      if (data != null) {
        this.data = new byte[data.length];
        System.arraycopy(data,
                         0,
                         this.data,
                         0,
                         data.length);
      } else {
        this.data = null;
      }
      return this;
    }

    private Function<Packet, ?> createExtensionFactory()
    {
      for (SpecialisationFactory ef : Lookups.forPath(LOOKUPPATH).lookupAll(SpecialisationFactory.class)) {
        if (ef.isValid(group,
                       command,
                       mode)) {
          return ef::createSpecialisation;
        }
      }
      return null;
    }

    private Packet build(boolean strict)
    {
      final Function<String, RuntimeException> errorFactory = IllegalStateException::new;
      if (extensionFactory == null) {
        extensionFactory = createExtensionFactory();
      }
      return new DefaultPacket(checkCommandGroup(group,
                                                 errorFactory),
                               checkCommandMode(mode,
                                                errorFactory),
                               checkCommand(group,
                                            command,
                                            strict,
                                            errorFactory),
                               checkNID(senderNID,
                                        errorFactory),
                               data,
                               extensionFactory);
    }

    public Packet build()
    {
      return build(false);
    }

    public Packet strictBuild()
    {
      return build(true);
    }

    public Packet buildTakeOwnership(short nid,
                                     short locoAddress)
    {
      commandGroup(CommandGroup.LOCO);
      command(CommandGroup.LOCO_ACTIVE);
      commandMode(CommandMode.COMMAND);
//      address(nid);
      ByteBuffer buffer = ByteBuffer.allocate(4);
      buffer.order(ByteOrder.LITTLE_ENDIAN);
      buffer.putShort((short) locoAddress);
      buffer.putShort((short) 0x10);
      data(buffer.array());
      return build(true);
    }

    public Packet buildOwnerShipEvent(short nid,
                                      short locoAddress)
    {
      commandGroup(CommandGroup.LOCO);
      command(CommandGroup.LOCO_ACTIVE);
      commandMode(CommandMode.EVENT);
      senderNID(nid);
      ByteBuffer buffer = ByteBuffer.allocate(3);
      buffer.order(ByteOrder.LITTLE_ENDIAN);
      buffer.putShort(locoAddress);
      buffer.put((byte) 1);
      data(buffer.array());
      return build(true);
    }

  }
  private final CommandGroup group;
  private final CommandMode mode;
  private final byte command;
  private final short address;
  private final ByteBuffer data;
  private final Lookup lookup;
  private String string;

  @SuppressWarnings("LeakingThisInConstructor")
  private DefaultPacket(@NotNull CommandGroup group,
                        @NotNull CommandMode mode,
                        byte command,
                        short address,
                        byte[] data,
                        Function<Packet, ?> extensionFactory)
  {
    this.group = group;
    this.mode = mode;
    this.command = command;
    this.address = address;
    final int dataLen = data != null ? data.length : 0;
    if (dataLen > 0 || (address != -1)) {
      ByteBuffer tmp;
      int bufferLen;
      if (dataLen > 0) {
        if (address != -1) {
          bufferLen = dataLen + 2;
        } else {
          bufferLen = dataLen;
        }
      } else {
        bufferLen = 2;
      }
      tmp = ByteBuffer.allocate(Math.min(bufferLen,
                                         MAX_DLC));
      tmp.order(ByteOrder.LITTLE_ENDIAN);
      if (address != -1) {
        tmp.putShort((short) address);
      }
      if (dataLen > 0) {
        tmp.put(data,
                0,
                Math.min(tmp.remaining(),
                         dataLen));
      }
      tmp.clear();
      this.data = tmp.asReadOnlyBuffer();
    } else {
      this.data = ByteBuffer.allocate(0).asReadOnlyBuffer();
    }
    if (extensionFactory != null) {
      lookup = Lookups.singleton(extensionFactory.apply(this));
    } else {
      lookup = Lookup.EMPTY;
    }
  }

  @Override
  public Lookup getLookup()
  {
    return lookup;
  }

  @Override
  public CommandGroup getCommandGroup()
  {
    return group;
  }

  @Override
  public CommandMode getCommandMode()
  {
    return mode;
  }

  @Override
  public byte getCommand()
  {
    return command;
  }

  @Override
  public short getSenderNID()
  {
    return address;
  }

  @Override
  public Packet getPacket()
  {
    return null;
  }

  @Override
  public ByteBuffer getData()
  {
    return data.duplicate().order(ByteOrder.LITTLE_ENDIAN);
  }

  @Override
  public synchronized String toString()
  {
    if (string == null) {
      StringBuilder tmp = new StringBuilder();
      tmp.append("0x");
      tmp.append(Integer.toHexString(group.getMagic()));
      tmp.append(", 0x");
      tmp.append(Integer.toHexString(command));
      tmp.append(", ");
      tmp.append(mode.name());
      tmp.append(", ");
      tmp.append(data.capacity());
      if (data.capacity() > 0) {
        tmp.append(", ");
        for (int i = 0; i < data.capacity(); ++i) {
          Utils.appendHexString(data.get(i) & 0xff,
                                tmp,
                                2);
          tmp.append(' ');
        }
      }
      string = tmp.toString();
    }
    return string;
  }

}
