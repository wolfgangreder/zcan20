/*
 * Copyright 2017-2020 Wolfgang Reder.
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
package at.or.reder.zcan20.packet.impl;

import at.or.reder.dcc.PowerPort;
import at.or.reder.dcc.util.DCCUtils;
import at.or.reder.zcan20.CommandGroup;
import at.or.reder.zcan20.CommandMode;
import at.or.reder.zcan20.DataGroup;
import at.or.reder.zcan20.InterfaceOptionType;
import at.or.reder.zcan20.LocoActive;
import at.or.reder.zcan20.ModuleInfoType;
import at.or.reder.zcan20.PacketSelector;
import at.or.reder.zcan20.PowerState;
import at.or.reder.zcan20.Protocol;
import at.or.reder.zcan20.SpeedFlags;
import at.or.reder.zcan20.SpeedSteps;
import at.or.reder.zcan20.SpeedlimitMode;
import at.or.reder.zcan20.ZCANFactory;
import at.or.reder.zcan20.ZimoPowerMode;
import at.or.reder.zcan20.impl.PacketSelectorImpl;
import at.or.reder.zcan20.packet.Packet;
import at.or.reder.zcan20.packet.PacketAdapter;
import at.or.reder.zcan20.packet.PacketAdapterFactory;
import at.or.reder.zcan20.packet.PacketBuilder;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import org.openide.util.lookup.InstanceContent;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author Wolfgang Reder
 */
public final class DefaultPacketBuilder implements PacketBuilder
{

  private static final class FactoryAdapter implements InstanceContent.Convertor<Packet, PacketAdapter>
  {

    private final Function<Packet, PacketAdapter> function;

    public FactoryAdapter(Function<Packet, PacketAdapter> function)
    {
      this.function = function;
    }

    @Override
    public PacketAdapter convert(Packet obj)
    {
      return function.apply(obj);
    }

    @Override
    public Class<? extends PacketAdapter> type(Packet obj)
    {
      return PacketAdapter.class;
    }

    @Override
    public String id(Packet obj)
    {
      return obj.toString();
    }

    @Override
    public String displayName(Packet obj)
    {
      return obj.toString();
    }

  }
  private CommandGroup commandGroup;
  private CommandMode commandMode;
  private byte command;
  private short senderNID;
  private ByteBuffer data;
  private Function<Packet, PacketAdapter> givenAdapterFactory;
  private InstanceContent.Convertor<Packet, PacketAdapter> adapterFactory;
  private final ConcurrentMap<PacketSelector, PacketAdapterFactory<PacketAdapter>> factoryCache = new ConcurrentHashMap<>();

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
  public PacketBuilder adapterFactory(Function<Packet, PacketAdapter> adpaterFactory)
  {
    this.givenAdapterFactory = adpaterFactory;
    return this;
  }

  private PacketAdapterFactory<PacketAdapter> lookupAdapterFactory(PacketSelector s)
  {
    @SuppressWarnings("unchecked")
    Collection<? extends PacketAdapterFactory<PacketAdapter>> coll = Lookups.forPath(Packet.LOOKUPPATH).lookupAll(
            PacketAdapterFactory.class);
    for (PacketAdapterFactory<PacketAdapter> ef : coll) {
      if (ef.isValid(s)) {
        return ef;
      }
    }
//    ZCAN.LOGGER.log(Level.WARNING,
//                    () -> "Found no packetadapter for " + s.toString());
    return null;
  }

  private InstanceContent.Convertor<Packet, PacketAdapter> createAdapterFactory()
  {
    PacketSelector selector = new PacketSelectorImpl(commandGroup,
                                                     command,
                                                     commandMode,
                                                     data != null ? data.limit() : 0);
    return factoryCache.computeIfAbsent(selector,
                                        this::lookupAdapterFactory);
  }

  @Override
  public Packet build()
  {
    final Function<String, RuntimeException> exFactory = IllegalStateException::new;
    if (givenAdapterFactory == null) {
      adapterFactory = createAdapterFactory();
    } else {
      adapterFactory = new FactoryAdapter(givenAdapterFactory);
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
                                                   false,
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
  public Packet buildLoginPacket(String appName)
  {
    if (appName == null || appName.isBlank()) {
      return buildLoginPacket();
    }
    commandGroup(CommandGroup.NETWORK_EXT);
    command(CommandGroup.NETWORK_PORT_OPEN);
    commandMode(CommandMode.COMMAND);
    adapterFactory(null);
    int dataLen = 8 + Math.min(appName.length(),
                               24);
    ByteBuffer buffer = DCCUtils.allocateLEBuffer(dataLen);
    buffer.putInt(0x0);
    buffer.putInt(0x100);
    String tmp = appName.substring(0,
                                   Math.min(appName.length(),
                                            24));
    buffer.put(tmp.getBytes(StandardCharsets.ISO_8859_1));
    buffer.clear();
    data(buffer);
    return build();
  }

  @Override
  public Packet buildLogoutPacket(short masterNID)
  {
    commandGroup(CommandGroup.NETWORK);
    command(CommandGroup.NETWORK_PORT_CLOSE);
    commandMode(CommandMode.COMMAND);
    adapterFactory(null);
    ByteBuffer buffer = DCCUtils.allocateLEBuffer(2);
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
    ByteBuffer buffer = DCCUtils.allocateLEBuffer(4);
    buffer.putShort(objectNID);
    buffer.putShort(type.getMagic());
    buffer.clear();
    data(buffer);
    return build();
  }

  @Override
  public Packet buildGetPowerModePacket(short systemNID,
                                        Set<? extends PowerPort> outputs)
  {
    Objects.requireNonNull(outputs,
                           "outputs is null");
    if (outputs.isEmpty()) {
      throw new IllegalArgumentException("outputs is empty");
    }
    if (outputs.contains(PowerPort.UNKNOWN)) {
      throw new IllegalArgumentException("ouputs contains unknown");
    }
    commandGroup(CommandGroup.SYSTEM);
    command(CommandGroup.SYSTEM_POWER);
    commandMode(CommandMode.REQUEST);
    adapterFactory(null);
    ByteBuffer buffer = DCCUtils.allocateLEBuffer(3);
    buffer.putShort(systemNID);
    buffer.put(PowerPort.toValue(outputs));
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
    ByteBuffer buffer = DCCUtils.allocateLEBuffer(2);
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
    ByteBuffer buffer = DCCUtils.allocateLEBuffer(2);
    buffer.putShort(locoID);
    buffer.clear();
    data(buffer);
    return build();
  }

  @Override
  public PacketBuilder buildLocoModePacket(short locoID,
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
    ByteBuffer buffer = DCCUtils.allocateLEBuffer(6);
    buffer.putShort(locoID);
    int m1 = (steps.getMagic() & 0x0f) + ((protocol.getMagic() & 0x0f) << 4);
    int m3 = (limitMode.getMagic() << 2) + (pulseFx ? 1 : 0) + (analogFx ? 2 : 0);
    buffer.put((byte) m1);
    buffer.put((byte) numFunctions);
    buffer.put((byte) m3);
    buffer.put((byte) 0); // unknown
    data(buffer.flip());
    return this;
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
    ByteBuffer buffer = DCCUtils.allocateLEBuffer(2);
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
    ByteBuffer buffer = DCCUtils.allocateLEBuffer(6);
    buffer.putShort(locoID);
    buffer.putShort(sf);
    buffer.put(DCCUtils.byte1(speedDivisor));
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
    ByteBuffer buffer = DCCUtils.allocateLEBuffer(2);
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
    ByteBuffer buffer = DCCUtils.allocateLEBuffer(2);
    buffer.putShort(locoID);
    buffer.clear();
    data(buffer);
    return build();
  }

  @Override
  public Packet buildLocoFunctionPacket(short locoID,
                                        short fxNumber)
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
    commandMode(CommandMode.REQUEST);
    command(CommandGroup.LOCO_FUNC_SWITCH);
    adapterFactory(null);
    ByteBuffer buffer = DCCUtils.allocateLEBuffer(4);
    buffer.putShort(locoID);
    buffer.putShort(fxNumber);
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
    ByteBuffer buffer = DCCUtils.allocateLEBuffer(6);
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
    return buildLocoActivePacket(locoID,
                                 LocoActive.ACTIVE).build();
  }

  @Override
  public PacketBuilder buildLocoActivePacket(short locoID,
                                             LocoActive mode)
  {
    commandGroup(CommandGroup.LOCO);
    command(CommandGroup.LOCO_ACTIVE);
    commandMode(CommandMode.COMMAND);
    adapterFactory(null);
    ByteBuffer buffer = DCCUtils.allocateLEBuffer(4);
    buffer.putShort(locoID);
    buffer.putShort((short) (mode.getMagic() & 0xff));
    buffer.flip();
    data(buffer);
    return this;
  }

  @Override
  public Packet buildQueryTSEPortModePacket(short systemID,
                                            PowerPort port)
  {
    commandGroup(CommandGroup.TRACK_CONFIG_PUBLIC);
    commandMode(CommandMode.REQUEST);
    command(CommandGroup.TSE_PROG_MODE);
    adapterFactory(null);
    ByteBuffer buffer = DCCUtils.allocateLEBuffer(3);
    buffer.putShort(systemID);
    buffer.put(port.getMagic());
    buffer.clear();
    data(buffer);
    return build();
  }

  @Override
  public Packet buildSetTSEPowerModePacket(short systemID,
                                           PowerPort port,
                                           byte mode)
  {
    commandGroup(CommandGroup.TRACK_CONFIG_PUBLIC);
    commandMode(CommandMode.COMMAND);
    command(CommandGroup.TSE_PROG_MODE);
    adapterFactory(null);
    ByteBuffer buffer = DCCUtils.allocateLEBuffer(4);
    buffer.putShort(systemID);
    buffer.put(port.getMagic());
    buffer.put(mode);
    buffer.clear();
    data(buffer);
    return build();
  }

  @Override
  public Packet buildReadCVPacket(short systemID,
                                  short locoID,
                                  int cvNumber)
  {
    commandGroup(CommandGroup.TRACK_CONFIG_PRIVATE);
    commandMode(CommandMode.COMMAND);
    command(CommandGroup.TSE_PROG_READ);
    adapterFactory(null);
    ByteBuffer buffer = DCCUtils.allocateLEBuffer(8);
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
    commandGroup(CommandGroup.TRACK_CONFIG_PRIVATE);
    commandMode(CommandMode.COMMAND);
    command(CommandGroup.TSE_PROG_WRITE);
    adapterFactory(null);
    ByteBuffer buffer = DCCUtils.allocateLEBuffer(10);
    buffer.putShort(systemID);
    buffer.putShort(locoID);
    buffer.putInt(cvNumber);
    buffer.putShort(value);
    buffer.clear();
    data(buffer);
    return build();
  }

  @Override
  public PacketBuilder buildClearCVPacket(short systemID,
                                          short locoID)
  {
    return commandGroup(CommandGroup.TRACK_CONFIG_PRIVATE).
            command(CommandGroup.TSE_PROG_CLEAR).
            commandMode(CommandMode.COMMAND).
            data(DCCUtils.allocateLEBuffer(2 * Short.BYTES).
                    putShort(systemID).
                    putShort(locoID)).
            adapterFactory(null);
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
    ByteBuffer buffer = DCCUtils.allocateLEBuffer(4);
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
    ByteBuffer buffer = DCCUtils.allocateLEBuffer(6);
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
    ByteBuffer buffer = DCCUtils.allocateLEBuffer(4);
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
    ByteBuffer buffer = DCCUtils.allocateLEBuffer(16);
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
  public Packet buildSystemPowerInfoPacket(short nid,
                                           Collection<PowerPort> output)
  {
    Objects.requireNonNull(output,
                           "output is null");
    commandGroup(CommandGroup.SYSTEM);
    commandMode(CommandMode.REQUEST);
    command(CommandGroup.SYSTEM_POWER);
    adapterFactory(null);
    ByteBuffer buffer = DCCUtils.allocateLEBuffer(3);
    buffer.putShort(nid);
    byte mask = PowerPort.toValue(output);
    buffer.put(mask);
    buffer.rewind();
    data(buffer);
    return build();
  }

  @Override
  public Packet buildSystemPowerInfoPacket(short nid,
                                           Collection<PowerPort> output,
                                           ZimoPowerMode mode)
  {
    Objects.requireNonNull(output,
                           "output is null");
    commandGroup(CommandGroup.SYSTEM);
    commandMode(CommandMode.COMMAND);
    command(CommandGroup.SYSTEM_POWER);
    adapterFactory(null);
    ByteBuffer buffer = DCCUtils.allocateLEBuffer(4);
    buffer.putShort(nid);
    byte mask = PowerPort.toValue(output);
    buffer.put(mask);
    buffer.put((byte) (mode.getMagic() & 0xff));
    buffer.rewind();
    data(buffer);
    return build();
  }

  @Override
  public Packet builderModulePowerInfoPacket(short nid,
                                             PowerPort output,
                                             PowerState state)
  {
    Objects.requireNonNull(output,
                           "output is null");
    if (!output.isValidInSet()) {
      throw new IllegalArgumentException("output is not set/command valid");
    }
    Objects.requireNonNull(state);
    commandGroup(CommandGroup.CONFIG);
    commandMode(CommandMode.COMMAND);
    command(CommandGroup.CONFIG_MODULE_POWER_INFO);
    adapterFactory(null);
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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
    ByteBuffer buffer = DCCUtils.allocateLEBuffer(4);
    buffer.putShort(nid);
    buffer.putShort(type.getMagic());
    buffer.rewind();
    data(buffer);
    return build();
  }

}
