/*
 * Copyright 2019 Wolfgang Reder.
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

import at.or.reder.dcc.util.Utils;
import at.or.reder.zcan20.ModuleInfoType;
import at.or.reder.zcan20.PacketSelector;
import at.or.reder.zcan20.packet.ModuleInfoPacketAdapter;
import at.or.reder.zcan20.packet.Packet;
import at.or.reder.zcan20.packet.PacketAdapterFactory;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Wolfgang Reder
 */
final class ModuleInfoPacketAdapterImpl extends AbstractPacketAdapter implements ModuleInfoPacketAdapter
{

  @ServiceProvider(service = PacketAdapterFactory.class, path = Packet.LOOKUPPATH)
  public static final class Factory implements PacketAdapterFactory<ModuleInfoPacketAdapter>
  {

    @Override
    public boolean isValid(PacketSelector selector)
    {
      return SELECTOR.test(selector);
    }

    @Override
    public Class<? extends ModuleInfoPacketAdapter> type(Packet obj)
    {
      return ModuleInfoPacketAdapter.class;
    }

    @Override
    public ModuleInfoPacketAdapter convert(Packet packet)
    {
      return new ModuleInfoPacketAdapterImpl(packet);
    }

  }

  ModuleInfoPacketAdapterImpl(Packet packet)
  {
    super(packet);
  }

  @Override
  public Class<?> getValueClass()
  {
    return getInfoType().getValueClass();
  }

  @Override
  public ModuleInfoType getInfoType()
  {
    return ModuleInfoType.valueOf(buffer.getShort(2));
  }

  @Override
  public int getRawValue()
  {
    return buffer.getInt(4);
  }

  @Override
  public <C> C getValue(Class<? extends C> clazz) throws IllegalArgumentException
  {
    ModuleInfoType infoType = getInfoType();
    if (clazz != infoType.getValueClass() && clazz != Integer.class) {
      throw new IllegalArgumentException("Invalid value class");
    }
    int rawValue = getRawValue();
    return clazz.cast(infoType.convertValue(rawValue));
  }

  @Override
  public String toString()
  {
    StringBuilder builder = new StringBuilder("ModuleInfo(type=");
    builder.append(getInfoType().toString());
    builder.append(", rawValue=0x");
    Utils.appendHexString(getRawValue(),
                          builder,
                          8);
    builder.append(')');
    return builder.toString();
  }

}
