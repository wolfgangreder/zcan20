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

import com.reder.zcan20.LocoMode;
import com.reder.zcan20.Protocol;
import com.reder.zcan20.SpeedSteps;
import com.reder.zcan20.SpeedlimitMode;
import com.reder.zcan20.packet.Packet;
import com.reder.zcan20.packet.PacketBuilder;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Objects;
import javax.validation.constraints.NotNull;

/**
 *
 * @author Wolfgang Reder
 */
public final class LocoModeImpl extends AbstractPacketAdapter implements LocoMode
{

  private final boolean used;

  public static final class Builder
  {

    private final PacketBuilder packetBuilder;
    private SpeedSteps steps = SpeedSteps.STEP_128;
    private Protocol protocol = Protocol.DCC;
    private int funcCount = 28;
    private boolean pulseFx = false;
    private boolean analogFunc = false;
    private SpeedlimitMode limitMode = SpeedlimitMode.NO_LIMIT;

    public Builder(@NotNull PacketBuilder packetBuilder)
    {
      this.packetBuilder = Objects.requireNonNull(packetBuilder,
                                                  "packetBuilder is null");
    }

    public Builder speedSteps(@NotNull SpeedSteps steps)
    {
      Objects.requireNonNull(steps,
                             "steps is null");
      if (steps == SpeedSteps.UNKNOWN) {
        throw new IllegalArgumentException("invalid speedsteps");
      }
      this.steps = steps;
      return this;
    }

    public Builder protocol(@NotNull Protocol prot)
    {
      Objects.requireNonNull(prot,
                             "prot is null");
      if (prot == Protocol.UNKNOWN || prot == Protocol.NOT_DEFINED) {
        throw new IllegalArgumentException("invalid protocol");
      }
      this.protocol = prot;
      return this;
    }

    public Builder function(int numFunc)
    {
      if (numFunc < 0 || numFunc > 255) {
        throw new IllegalArgumentException("invalid function count");
      }
      this.funcCount = numFunc;
      return this;
    }

    public Builder pulseFx(boolean pfx)
    {
      this.pulseFx = pfx;
      return this;
    }

    public Builder analogFx(boolean afx)
    {
      this.analogFunc = afx;
      return this;
    }

    public Builder speedlimitMode(SpeedlimitMode sl)
    {
      Objects.requireNonNull(sl,
                             "speedLimitMode is null");
      this.limitMode = sl;
      return this;
    }

    public LocoModeImpl build(int locoAddress,
                              boolean used)
    {
      int m1 = (protocol.getMagic() << 4) + (steps.getMagic() & 0x0f);
      int m3 = (limitMode.getMagic() << 2) + (pulseFx ? 1 : 0) + (analogFunc ? 2 : 0);
      ByteBuffer buffer = ByteBuffer.allocate(5);
      buffer.order(ByteOrder.LITTLE_ENDIAN);
      buffer.putShort((short) locoAddress);
      buffer.put((byte) m1);
      buffer.put((byte) funcCount);
      buffer.put((byte) m3);
      buffer.limit(buffer.position());
      buffer.rewind();
      packetBuilder.data(buffer);
      return new LocoModeImpl(packetBuilder.build(),
                              used);
    }

  }

  public LocoModeImpl(Packet packet,
                      boolean used)
  {
    super(packet);
    this.used = used;
  }

  @Override
  public boolean isUsed()
  {
    return used;
  }

  @Override
  public int getLocoAddress()
  {
    return buffer.getShort(0) & 0xffff;
  }

  @Override
  public SpeedSteps getSpeedSteps()
  {
    int m1 = buffer.get(2) & 0xff;
    return SpeedSteps.valueOfMagic(m1 & 0x0f);
  }

  @Override
  public Protocol getProtocol()
  {
    int m1 = buffer.get(2) & 0xff;
    return Protocol.valueOfMagic((m1 & 0xf0) >> 4);
  }

  @Override
  public int getFunctionCount()
  {
    return buffer.get(3) & 0xff;
  }

  @Override
  public boolean isPulsFx()
  {
    int m3 = buffer.get(4) & 0xff;
    return (m3 & 0x01) != 0;
  }

  @Override
  public boolean isAnalogFx()
  {
    int m3 = buffer.get(4) & 0xff;
    return (m3 & 0x02) != 0;
  }

  @Override
  public SpeedlimitMode getSpeedLimitMode()
  {
    int m3 = buffer.get(4) & 0xff;
    return SpeedlimitMode.valueOf((m3 & 0xc) >> 4);
  }

  @Override
  public String toString()
  {
    StringBuilder b = new StringBuilder("LocoMode{");
    if (!isUsed()) {
      b.append("not ");
    }
    b.append("used, steps=");
    b.append(getSpeedSteps());
    b.append(", prot=");
    b.append(getProtocol());
    b.append(", func=");
    b.append(getFunctionCount());
    b.append(", pfx=");
    b.append(isPulsFx());
    b.append(", analogFx=");
    b.append(isAnalogFx());
    b.append("}");
    return b.toString();
  }

}
