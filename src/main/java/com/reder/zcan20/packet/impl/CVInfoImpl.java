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
import com.reder.zcan20.packet.CVInfoAdapter;
import com.reder.zcan20.packet.Packet;
import com.reder.zcan20.packet.SpecialisationFactory;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Wolfgang Reder
 */
public final class CVInfoImpl extends AbstractPacketAdapter implements CVInfoAdapter
{

  @ServiceProvider(service = SpecialisationFactory.class, path = Packet.LOOKUPPATH)
  public static final class Factory implements SpecialisationFactory
  {

    @Override
    public boolean isValid(CommandGroup group,
                           int command,
                           CommandMode mode)
    {
      return group == CommandGroup.TRACK_CONFIG_PUBLIC && (mode == CommandMode.ACK || mode == CommandMode.EVENT);
    }

    @Override
    public Object createSpecialisation(Packet packet)
    {
      return new CVInfoImpl(packet);
    }

  }

  public CVInfoImpl(Packet packet)
  {
    super(packet);
  }

  @Override
  public int getNumber()
  {
    return buffer.getInt(6) & 0xffff_ffff;
  }

  @Override
  public int getValue()
  {
    return buffer.getShort(10) & 0xffff;
  }

  @Override
  public short getDecoderAddress()
  {
    return buffer.getShort(4);
  }

}
