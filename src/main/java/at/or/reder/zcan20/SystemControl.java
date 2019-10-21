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

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import javax.validation.constraints.NotNull;

public interface SystemControl
{

  public default void getPowerStateInfo(@NotNull PowerPort output) throws IOException
  {
    getPowerStateInfo(Collections.singleton(Objects.requireNonNull(output,
                                                                   "output is null")));
  }

  public void getPowerStateInfo(@NotNull Collection<PowerPort> outputs) throws IOException;

  public void setPowerModeInfo(@NotNull PowerPort output,
                               @NotNull PowerMode mode) throws IOException;
//
//  public default Future<PowerInfo> getOutputState(@NotNull PowerPort output) throws IOException, InterruptedException
//  {
//    return getOutputState(Collections.singleton(output)).stream().findFirst().orElse(null);
//  }
//
//  public List<Future<PowerInfo>> getOutputState(@NotNull Collection<? extends PowerPort> outputs) throws IOException,
//                                                                                                           InterruptedException;
//
//  public List<Future<PowerInfo>> setOutputState(@NotNull Map<PowerOutput, PowerMode> modes) throws IOException,
//                                                                                                   InterruptedException;

  public void loadDump() throws IOException;

}
