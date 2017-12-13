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
import com.reder.zcan20.packet.DataNameExtRequestAdapter;
import com.reder.zcan20.packet.Packet;
import com.reder.zcan20.packet.PacketAdapterFactory;
import com.reder.zcan20.util.Utils;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Wolfgang Reder
 */
public final class DataNameExtRequestAdapterImpl extends AbstractPacketAdapter implements DataNameExtRequestAdapter
{

  @ServiceProvider(service = PacketAdapterFactory.class, path = Packet.LOOKUPPATH)
  public static final class Factory implements PacketAdapterFactory
  {

    @Override
    public boolean isValid(CommandGroup group,
                           int command,
                           CommandMode mode)
    {
      return group == CommandGroup.DATA && mode == CommandMode.REQUEST && command == CommandGroup.DATA_NAME_EXT;
    }

    @Override
    public DataNameExtRequestAdapter createAdapter(Packet packet)
    {
      return new DataNameExtRequestAdapterImpl(packet);
    }

  }

  public DataNameExtRequestAdapterImpl(Packet packet)
  {
    super(packet);
  }

  @Override
  public short getMasterNID()
  {
    return buffer.getShort(0);
  }

  @Override
  public short getObjectNID()
  {
    return buffer.getShort(2);
  }

  @Override
  public int getSubID()
  {
    return buffer.getInt(4);
  }

  @Override
  public int getVal1()
  {
    return buffer.getInt(8);
  }

  @Override
  public int getVal2()
  {
    return buffer.getInt(12);
  }

  @Override
  public String toString()
  {
    StringBuilder builder = new StringBuilder("NAME_EXT(0x");
    Utils.appendHexString(getMasterNID() & 0xffff,
                          builder,
                          4);
    builder.append(", 0x");
    Utils.appendHexString(getObjectNID() & 0xffff,
                          builder,
                          4);
    builder.append(", 0x");
    Utils.appendHexString(getSubID(),
                          builder,
                          8);
    builder.append(", 0x");
    Utils.appendHexString(getVal1(),
                          builder,
                          8);
    builder.append(", 0x");
    Utils.appendHexString(getVal2(),
                          builder,
                          8);
    return builder.append(')').toString();
  }

}
