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
package at.or.reder.zcan20.packet.impl;

import at.or.reder.dcc.PowerPort;
import at.or.reder.zcan20.CommandGroup;
import at.or.reder.zcan20.CommandMode;
import at.or.reder.zcan20.PacketSelector;
import at.or.reder.zcan20.PowerStateEx;
import at.or.reder.zcan20.packet.Packet;
import at.or.reder.zcan20.packet.PacketAdapterFactory;
import at.or.reder.zcan20.packet.PowerInfoEx;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;
import javax.validation.constraints.NotNull;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Wolfgang Reder
 */
final class PowerInfoExImpl extends AbstractPacketAdapter implements PowerInfoEx
{

  @ServiceProvider(service = PacketAdapterFactory.class, path = Packet.LOOKUPPATH)
  public static final class Factory implements PacketAdapterFactory<PowerInfoEx>
  {

    @Override
    public boolean isValid(PacketSelector selector)
    {
      return SELECTOR.test(selector);
    }

    @Override
    public PowerInfoEx convert(Packet packet)
    {
      return new PowerInfoExImpl(packet);
    }

    @Override
    public Class<? extends PowerInfoEx> type(Packet obj)
    {
      return PowerInfoExImpl.class;
    }

  }

  private PowerInfoExImpl(@NotNull Packet packet)
  {
    super(packet);
    if (packet.getCommandGroup() != CommandGroup.CONFIG) {
      throw new IllegalArgumentException("illegal commandGroup");
    }
    if (packet.getCommand() != CommandGroup.CONFIG_POWER_INFO) {
      throw new IllegalArgumentException("illegal command");
    }
    if (packet.getCommandMode() != CommandMode.ACK && packet.getCommandMode() != CommandMode.EVENT) {
      throw new IllegalArgumentException("illegal commandMode");
    }
    if (buffer.capacity() < 22) {
      throw new IllegalArgumentException("illegal data size");
    }
  }

  @Override
  public short getSenderNid()
  {
    return buffer.getShort(0);
  }

  private int getOffset(PowerPort port)
  {
    switch (port) {
      case OUT_1:
        return 2;
      case OUT_2:
        return 8;
      default:
        return -1;
    }
  }

  @Override
  public Set<PowerStateEx> getState(PowerPort port)
  {
    int offset = getOffset(port);
    if (offset >= 0) {
      byte tmp = buffer.get(offset);
      return PowerStateEx.toSet(tmp & 0xff);
    }
    return Collections.emptySet();
  }

  @Override
  public float getOutputVoltage(PowerPort port)
  {
    int offset = getOffset(port);
    if (offset >= 0) {
      float tmp = buffer.getShort(offset + 2);
      return tmp / 1000f;
    }
    return Float.NaN;
  }

  @Override
  public float getOutputCurrent(PowerPort port)
  {
    int offset = getOffset(port);
    if (offset >= 0) {
      float tmp = buffer.getShort(offset + 4);
      return tmp / 1000f;
    }
    return Float.NaN;
  }

  @Override
  public float getInputVoltage()
  {
    float tmp = buffer.getShort(18);
    return tmp / 1000f;
  }

  @Override
  public float getInputCurrent()
  {
    float tmp = buffer.getShort(20);
    return tmp / 100f;
  }

  public int getUknown()
  {
    return buffer.getInt(14);
  }

  @Override
  public String toString()
  {
    StringBuilder builder = new StringBuilder("POWER_INFO(Track1:");
    builder.append(getState(PowerPort.OUT_1).stream().
            map(PowerStateEx::name).
            collect(Collectors.joining(",",
                                       "[",
                                       "]")));
    builder.append(getOutputVoltage(PowerPort.OUT_1));
    builder.append(" V, ");
    builder.append(getOutputCurrent(PowerPort.OUT_1));
    builder.append(" A, Track2:");
    builder.append(getState(PowerPort.OUT_2).stream().
            map(PowerStateEx::name).
            collect(Collectors.joining(",",
                                       "[",
                                       "]")));
    builder.append(getOutputVoltage(PowerPort.OUT_2));
    builder.append(" V, ");
    builder.append(getOutputCurrent(PowerPort.OUT_2));
    builder.append("A, Input:");
    builder.append(getInputVoltage());
    builder.append(" V, ");
    builder.append(getInputCurrent());
    builder.append("A, Unknown ");
    builder.append(Integer.toHexString(getUknown()));
    return builder.append(")").toString();
  }

}
