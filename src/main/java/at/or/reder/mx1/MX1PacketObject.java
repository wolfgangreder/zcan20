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
package at.or.reder.mx1;

import java.util.EventObject;

/**
 *
 * @author Wolfgang Reder
 */
public final class MX1PacketObject extends EventObject
{

  private final MX1Packet packet;

  public MX1PacketObject(MX1 source,
                         MX1Packet packet)
  {
    super(source);
    this.packet = packet;
  }

  public MX1Packet getPacket()
  {
    return packet;
  }

  @Override
  public MX1 getSource()
  {
    return (MX1) source;
  }

}
