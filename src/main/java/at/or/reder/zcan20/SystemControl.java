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
 */package at.or.reder.zcan20;

import at.or.reder.dcc.PowerPort;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collection;
import javax.validation.constraints.NotNull;

public interface SystemControl
{

  public void getPowerStateInfo(@NotNull Collection<PowerPort> outputs) throws IOException;

  public void setPowerModeInfo(@NotNull PowerPort output,
                               @NotNull ZimoPowerMode mode) throws IOException;

  public String getHardwareVersion() throws IOException;

  public String getSoftwareVersion() throws IOException;

  public LocalDate getSoftwareBuildDate() throws IOException;

  public LocalTime getSoftwareBuildTime() throws IOException;

  public LocalDate getRealtimeDate() throws IOException;

  public LocalTime getRealtimeTime() throws IOException;

  public void setRealtimeDateTime(@NotNull LocalDateTime dt) throws IOException;

  public String getMiwiHardwareVersion() throws IOException;

  public String getMiwiSoftwareVersion() throws IOException;

  public String getMiwiChannel() throws IOException;

  public int getModuleNumber() throws IOException;

  public int getModuleType() throws IOException;

}
