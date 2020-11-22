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
import at.or.reder.zcan20.PowerState;
import at.or.reder.zcan20.packet.Packet;
import at.or.reder.zcan20.packet.PacketAdapterFactory;
import at.or.reder.zcan20.packet.PowerInfo;
import java.util.Collections;
import java.util.Set;
import javax.validation.constraints.NotNull;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Wolfgang Reder
 */
final class PowerInfoImpl extends AbstractPacketAdapter implements PowerInfo
{

  @ServiceProvider(service = PacketAdapterFactory.class, path = Packet.LOOKUPPATH)
  public static final class Factory implements PacketAdapterFactory<PowerInfo>
  {

    @Override
    public boolean isValid(PacketSelector selector)
    {
      return SELECTOR.test(selector);
    }

    @Override
    public PowerInfo convert(Packet packet)
    {
      return new PowerInfoImpl(packet);
    }

    @Override
    public Class<? extends PowerInfo> type(Packet obj)
    {
      return PowerInfo.class;
    }

  }

  private PowerInfoImpl(@NotNull Packet packet)
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
    if (buffer.capacity() < 8) {
      throw new IllegalArgumentException("illegal data size");
    }
  }

  @Override
  public Set<PowerState> getState()
  {
    // TODO informationen von ZIMO anfordern
    return Collections.emptySet();
  }

  @Override
  public float getOutputVoltage(PowerPort out)
  {
    short f = 0;
    switch (out) {
      case OUT_1:
        f = buffer.getShort(4);
        break;
      case OUT_2:
        f = buffer.getShort(10);
        break;
      default:
        return Float.NaN;
    }
    return f / 1000f;
  }

  @Override
  public float getOutputCurrent(PowerPort out)
  {
    float f;
    switch (out) {
      case OUT_1:
        f = buffer.getShort(6);
        break;
      case OUT_2:
        f = buffer.getShort(12);
        break;
      default:
        return Float.NaN;
    }
    return f / 1000;
  }

  @Override
  public float getInputVoltage()
  {
    return buffer.getShort(18) / 1000f;
  }

  @Override
  public float getInputCurrent()
  {
    return buffer.getShort(20) / 100f;
  }

  @Override
  public String toString()
  {
    StringBuilder builder = new StringBuilder("POWER_INFO(Track1:");
    builder.append(getOutputVoltage(PowerPort.OUT_1));
    builder.append(" V, ");
    builder.append(getOutputCurrent(PowerPort.OUT_1));
    builder.append(" A, Track2:");
    builder.append(getOutputVoltage(PowerPort.OUT_2));
    builder.append(" V, ");
    builder.append(getOutputCurrent(PowerPort.OUT_2));
    return builder.append("A)").toString();

  }

}
