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
package at.or.reder.zcan20.ui;

import at.or.reder.zcan20.CanId;
import at.or.reder.zcan20.CommandGroup;
import at.or.reder.zcan20.CommandMode;
import at.or.reder.zcan20.packet.Packet;
import at.or.reder.zcan20.packet.PacketAdapter;
import java.nio.ByteBuffer;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import org.openide.util.Lookup;

/**
 *
 * @author Wolfgang Reder
 */
public final class TSPacket implements Packet
{

  private final Map<Integer, String> labelMap = new HashMap<>();
  private final LocalDateTime ts;
  private final Packet packet;

  public TSPacket(Packet packet,
                  LocalDateTime ts)
  {
    this.ts = Objects.requireNonNull(ts,
                                     "ts is null");
    this.packet = Objects.requireNonNull(packet,
                                         "packet is null");
  }

  public Map<Integer, String> getLabelMap()
  {
    return labelMap;
  }

  @Override
  public int hashCode()
  {
    return packet.hashCode();
  }

  @Override
  public boolean equals(Object obj)
  {
    return packet.equals(obj);
  }

  public LocalDateTime getTimestamp()
  {
    return ts;
  }

  @Override
  public CanId getCanId()
  {
    return packet.getCanId();
  }

  @Override
  public CommandGroup getCommandGroup()
  {
    return packet.getCommandGroup();
  }

  @Override
  public CommandMode getCommandMode()
  {
    return packet.getCommandMode();
  }

  @Override
  public byte getCommand()
  {
    return packet.getCommand();
  }

  @Override
  public short getSenderNID()
  {
    return packet.getSenderNID();
  }

  @Override
  public ByteBuffer getData()
  {
    return packet.getData();
  }

  @Override
  public int getDLC()
  {
    return packet.getDLC();
  }

  @Override
  public <T extends PacketAdapter> T getAdapter(Class<? extends T> clazz)
  {
    return packet.getAdapter(clazz);
  }

  @Override
  public Packet getPacket()
  {
    return packet.getPacket();
  }

  @Override
  public Lookup getLookup()
  {
    return packet.getLookup();
  }

}
