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

import at.or.reder.dcc.Direction;
import at.or.reder.dcc.util.DCCUtils;
import at.or.reder.zcan20.LocoDirection;
import at.or.reder.zcan20.LocoSpeed;
import at.or.reder.zcan20.LocoVoltage;
import at.or.reder.zcan20.PacketSelector;
import at.or.reder.zcan20.packet.LocoTachoPacketAdapter;
import at.or.reder.zcan20.packet.Packet;
import at.or.reder.zcan20.packet.PacketAdapterFactory;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Wolfgang Reder
 */
abstract class LocoTachoPacketAdapterImpl extends AbstractPacketAdapter implements LocoTachoPacketAdapter
{

  @ServiceProvider(service = PacketAdapterFactory.class, path = Packet.LOOKUPPATH)
  public static final class Factory implements PacketAdapterFactory<LocoTachoPacketAdapter>
  {

    @Override
    public boolean isValid(PacketSelector selector)
    {
      return SELECTOR.test(selector);
    }

    @Override
    public LocoTachoPacketAdapter convert(Packet obj)
    {
      int selector = Byte.toUnsignedInt(obj.getData().get(3));
      switch (selector) {
        case 1:
          return new SpeedImpl(obj);
        case 8:
          return new FlagsImpl(obj);
        case 0x10:
          return new VoltageImpl(obj);
        default:
          return new DummyImpl(obj);
      }
    }

    @Override
    public Class<? extends LocoTachoPacketAdapter> type(Packet obj)
    {
      int selector = Byte.toUnsignedInt(obj.getData().get(3));
      switch (selector) {
        case 1:
          return SpeedImpl.class;
        case 8:
          return FlagsImpl.class;
        case 0x10:
          return VoltageImpl.class;
        default:
          return DummyImpl.class;
      }
    }

  }

  private static final class DummyImpl extends LocoTachoPacketAdapterImpl
  {

    public DummyImpl(Packet packet)
    {
      super(packet);
    }

  }

  private static final class FlagsImpl extends LocoTachoPacketAdapterImpl implements LocoDirection
  {

    private final int flags;

    public FlagsImpl(Packet packet)
    {
      super(packet);
      flags = buffer.get(4) & 0xff;
    }

    @Override
    public Direction getDirection()
    {
      if ((flags & 0x01) != 0) {
        return Direction.FORWARD;
      } else if ((flags & 0x02) != 0) {
        return Direction.REVERSE;
      } else {
        return null;
      }
    }

    @Override
    public boolean isDirectionPending()
    {
      return (flags & 0x08) != 0;
    }

    @Override
    public String toString()
    {
      StringBuilder builder = new StringBuilder("LOCO_TACHO_DIRECTION(0x");
      DCCUtils.appendHexString(getDecoderId(),
                            builder,
                            4);
      builder.append(", ");
      builder.append(getDirection());
      builder.append(", ");
      builder.append(isDirectionPending());
      return builder.append(')').toString();
    }

  }

  private static final class SpeedImpl extends LocoTachoPacketAdapterImpl implements LocoSpeed
  {

    public SpeedImpl(Packet packet)
    {
      super(packet);
    }

    @Override
    public short getSpeed()
    {
      return buffer.getShort(4);
    }

    @Override
    public short getDivisor()
    {
      return 1;
    }

    @Override
    public String toString()
    {
      StringBuilder builder = new StringBuilder("LOCO_TACHO_SPPED(0x");
      DCCUtils.appendHexString(getDecoderId(),
                            builder,
                            4);
      builder.append(", ");
      builder.append(getSpeed());
      return builder.append(')').toString();
    }

  }

  private static final class VoltageImpl extends LocoTachoPacketAdapterImpl implements LocoVoltage
  {

    private final float voltage;

    public VoltageImpl(Packet packet)
    {
      super(packet);
      float f = buffer.get(4) & 0xff;
      voltage = f / 10f;
    }

    @Override
    public float getVoltage()
    {
      return voltage;
    }

    @Override
    public String toString()
    {
      StringBuilder builder = new StringBuilder("LOCO_TACHO_VOLTAGE(0x");
      DCCUtils.appendHexString(getDecoderId(),
                            builder,
                            4);
      builder.append(", ");
      builder.append(getVoltage());
      return builder.append(')').toString();
    }

  }

  protected LocoTachoPacketAdapterImpl(Packet packet)
  {
    super(packet);
  }

  @Override
  public short getDecoderId()
  {
    return buffer.getShort(0);
  }

}
