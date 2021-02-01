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

import at.or.reder.dcc.util.DCCUtils;
import at.or.reder.zcan20.Loco;
import at.or.reder.zcan20.LocoControl;
import at.or.reder.zcan20.LocoMode;
import at.or.reder.zcan20.ZCAN;
import at.or.reder.zcan20.packet.LocoModePacketAdapter;
import at.or.reder.zcan20.packet.Packet;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.logging.Level;

/**
 *
 * @author Wolfgang Reder
 */
public final class LocoControlImpl implements LocoControl
{

  private final ZCANImpl zcan;

  public LocoControlImpl(ZCANImpl zcan)
  {
    this.zcan = zcan;
  }

  @Override
  public Loco getLoco(short loco,
                      boolean takeOwnership) throws IOException
  {
    ByteBuffer buffer = DCCUtils.allocateLEBuffer(8);
    buffer.putShort(loco);
    // 1. abfrage fahrzeug mode LOCO.0x01.COMMAND
    Packet packet = zcan.createPacketBuilder().buildLocoModePacket(loco);
    LocoMode locoMode = zcan.sendReceive(packet,
                                         LocoModePacketAdapter.SELECTOR::matches,
                                         LocoModePacketAdapter.class,
                                         500);
    if (locoMode != null) {
      ZCAN.LOGGER.log(Level.FINE,
                      "Controlling Loco {0} with mode {1}",
                      new Object[]{loco, locoMode});
    }
    return new LocoImpl(zcan,
                        locoMode,
                        loco,
                        takeOwnership);
  }

}
