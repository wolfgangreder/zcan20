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

import at.or.reder.zcan20.ZimoPowerMode;
import at.or.reder.dcc.PowerPort;
import at.or.reder.zcan20.SystemControl;
import at.or.reder.zcan20.packet.Packet;
import at.or.reder.zcan20.packet.PacketBuilder;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import javax.validation.constraints.NotNull;

/**
 *
 * @author Wolfgang Reder
 */
final class SystemControlImpl implements SystemControl
{

  private final ZCANImpl zcan;

  public SystemControlImpl(ZCANImpl zcan)
  {
    this.zcan = zcan;
  }

  @Override
  public void getPowerStateInfo(@NotNull final Collection<PowerPort> output) throws IOException
  {
    if (Collections.frequency(Objects.requireNonNull(output,
                                                     "output is null"),
                              null) > 0) {
      throw new NullPointerException("output contains null");
    }
    if (output.stream().filter((o) -> !o.isValidInSet()).findAny().isPresent()) {
      throw new IllegalArgumentException(
              "output is not valid for set/request methods");
    }
    PacketBuilder builder = zcan.createPacketBuilder();
    Packet packet = builder.buildSystemPowerInfoPacket(zcan.getMasterNID(),
                                                       output);
    zcan.doSendPacket(packet);
  }

  @Override
  public void setPowerModeInfo(PowerPort output,
                               ZimoPowerMode state) throws IOException
  {
    Objects.requireNonNull(output,
                           "output is null");
    if (!output.isValidInSet()) {
      throw new IllegalArgumentException(
              "output is not valid for set/request methods");
    }
    Objects.requireNonNull(state,
                           "state is null");
    PacketBuilder builder = zcan.createPacketBuilder();
    Packet packet = builder.buildSystemPowerInfoPacket(zcan.getMasterNID(),
                                                       Collections.singleton(output),
                                                       state);
    zcan.doSendPacket(packet);
  }

  @Override
  public String getHardwareVersion() throws IOException
  {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public String getSoftwareVersion() throws IOException
  {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public LocalDate getSoftwareBuildDate() throws IOException
  {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public LocalTime getSoftwareBuildTime() throws IOException
  {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public LocalDate getRealtimeDate() throws IOException
  {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public LocalTime getRealtimeTime() throws IOException
  {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public void setRealtimeDateTime(LocalDateTime dt) throws IOException
  {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public String getMiwiHardwareVersion() throws IOException
  {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public String getMiwiSoftwareVersion() throws IOException
  {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public String getMiwiChannel() throws IOException
  {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public int getModuleNumber() throws IOException
  {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public int getModuleType() throws IOException
  {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

}
