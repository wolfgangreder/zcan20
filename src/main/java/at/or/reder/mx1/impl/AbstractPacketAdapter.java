/*
 * Copyright 2020 Wolfgang Reder.
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
package at.or.reder.mx1.impl;

import at.or.reder.mx1.MX1Command;
import at.or.reder.mx1.MX1Packet;
import at.or.reder.mx1.MX1PacketAdapter;
import java.util.Objects;
import javax.validation.constraints.NotNull;

/**
 *
 * @author Wolfgang Reder
 */
abstract class AbstractPacketAdapter implements MX1PacketAdapter
{

  private final MX1Packet packet;

  public AbstractPacketAdapter(@NotNull MX1Packet packet)
  {
    this.packet = Objects.requireNonNull(packet,
                                         "packet is null");
  }

  @Override
  public final MX1Packet getPacket()
  {
    return packet;
  }

  @Override
  public final MX1Command getCommand()
  {
    return packet.getCommand();
  }

}
