/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.reder.zcan20;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import javax.validation.constraints.NotNull;

/**
 *
 * @author reder
 */
public interface SystemControl
{

  public void getPowerStateInfo(@NotNull PowerOutput output,
                                long timeOut,
                                @NotNull TimeUnit unit) throws IOException;

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
