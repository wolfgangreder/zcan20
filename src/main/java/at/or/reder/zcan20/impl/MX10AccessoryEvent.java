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
package at.or.reder.zcan20.impl;

import at.or.reder.dcc.impl.AccessoryEventImpl;
import at.or.reder.zcan20.packet.AccessoryPacketAdapter;
import at.or.reder.zcan20.packet.AccessoryPacketRequestAdapter;
import org.openide.util.Lookup;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author Wolfgang Reder
 */
final class MX10AccessoryEvent extends AccessoryEventImpl
{

  private final Lookup lookup;

  MX10AccessoryEvent(MX10Control controller,
                     AccessoryPacketAdapter apa)
  {
    super(controller,
          apa.getPacket().getSenderNID(),
          apa.getNID(),
          apa.getPort(),
          apa.getValue() & 0xff);
    lookup = Lookups.singleton(apa);
  }

  MX10AccessoryEvent(MX10Control controller,
                     AccessoryPacketRequestAdapter apa)
  {
    super(controller,
          apa.getPacket().getSenderNID(),
          apa.getNID(),
          apa.getPort(),
          -1);
    lookup = Lookups.singleton(apa);
  }

  @Override
  public Lookup getLookup()
  {
    return lookup;
  }

}
