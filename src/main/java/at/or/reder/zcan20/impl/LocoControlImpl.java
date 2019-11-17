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

import at.or.reder.dcc.util.Utils;
import at.or.reder.zcan20.CommandGroup;
import at.or.reder.zcan20.CommandMode;
import at.or.reder.zcan20.Loco;
import at.or.reder.zcan20.LocoActive;
import at.or.reder.zcan20.LocoControl;
import at.or.reder.zcan20.Protocol;
import at.or.reder.zcan20.SpeedSteps;
import at.or.reder.zcan20.SpeedlimitMode;
import at.or.reder.zcan20.ZCANError;
import at.or.reder.zcan20.packet.LocoActivePacketAdapter;
import at.or.reder.zcan20.packet.LocoModePacketAdapter;
import at.or.reder.zcan20.packet.Packet;
import java.io.IOException;
import java.nio.ByteBuffer;

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
  public Loco takeOwnership(short loco) throws IOException
  {
    ByteBuffer buffer = Utils.allocateLEBuffer(8);
    buffer.putShort(loco);
    // 1. status abfragen LOCO.0x10.REQUEST
    Packet packet = zcan.createPacketBuilder().
            commandGroup(CommandGroup.LOCO).
            command(CommandGroup.LOCO_ACTIVE).
            commandMode(CommandMode.REQUEST).data(buffer.flip()).
            build();
    // 2. max.500ms auf antwort warten
    LocoActivePacketAdapter locoActive = zcan.sendReceive(packet,
                                                          LocoActivePacketAdapter.SELECTOR::matches,
                                                          LocoActivePacketAdapter.class,
                                                          500);
    if (locoActive != null && locoActive.getState() != LocoActive.UNKNOWN) {
      Packet errPacket = locoActive.getPacket();
      throw new ZCANError(errPacket.getSenderNID(),
                          errPacket.getCommandGroup(),
                          errPacket.getCommand(),
                          null,
                          "Loco is controlled remotely");
    }
    // 3. abfrage fahrzeug mode LOCO.0x01.COMMAND
    packet = zcan.createPacketBuilder().buildLocoModePacket(loco);
    LocoModePacketAdapter locoMode = zcan.sendReceive(packet,
                                                      LocoModePacketAdapter.SELECTOR::matches,
                                                      LocoModePacketAdapter.class,
                                                      500);
    if (locoMode != null) {
      packet = zcan.createPacketBuilder().buildLocoModePacket(loco,
                                                              locoMode.getSpeedSteps(),
                                                              locoMode.getProtocol(),
                                                              locoMode.getFunctionCount(),
                                                              locoMode.getSpeedLimitMode(),
                                                              locoMode.isAnalogFx(),
                                                              locoMode.isPulsFx()).
              commandMode(CommandMode.COMMAND).
              build();
    } else {
      packet = zcan.createPacketBuilder().buildLocoModePacket(loco,
                                                              SpeedSteps.STEP_128,
                                                              Protocol.DCC,
                                                              1,
                                                              SpeedlimitMode.ZIMO,
                                                              false,
                                                              false).
              commandMode(CommandMode.COMMAND).
              build();
    }
    // 4. max.500ms auf antwort warten LOCO.0x01.ACK
    locoMode = zcan.sendReceive(packet,
                                LocoModePacketAdapter.SELECTOR::matches,
                                LocoModePacketAdapter.class,
                                500);
    // 5.
    packet = zcan.createPacketBuilder().buildClearCVPacket(zcan.getMasterNID(),
                                                           loco).build();
    zcan.doSendPacket(packet);
    return new LocoImpl(zcan,
                        locoMode,
                        loco);
  }

}
