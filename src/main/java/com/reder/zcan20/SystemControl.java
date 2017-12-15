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
 */package com.reder.zcan20;

import java.io.IOException;
import javax.validation.constraints.NotNull;

public interface SystemControl
{

  public void getPowerStateInfo(@NotNull PowerOutput output) throws IOException;

  public void setPowerStateInfo(@NotNull PowerOutput output,
                                @NotNull PowerState state) throws IOException;
//
//  public default Future<PowerInfo> getOutputState(@NotNull PowerOutput output) throws IOException, InterruptedException
//  {
//    return getOutputState(Collections.singleton(output)).stream().findFirst().orElse(null);
//  }
//
//  public List<Future<PowerInfo>> getOutputState(@NotNull Collection<? extends PowerOutput> outputs) throws IOException,
//                                                                                                           InterruptedException;
//
//  public List<Future<PowerInfo>> setOutputState(@NotNull Map<PowerOutput, PowerMode> modes) throws IOException,
//                                                                                                   InterruptedException;

  public void loadDump() throws IOException;

}
