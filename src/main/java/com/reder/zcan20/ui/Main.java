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
 */
package com.reder.zcan20.ui;

import com.reder.zcan20.CommandGroup;
import com.reder.zcan20.ZCAN;
import com.reder.zcan20.ZCANFactory;
import com.reder.zcan20.packet.CVInfoAdapter;
import com.reder.zcan20.packet.Packet;
import com.reder.zcan20.util.Utils;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.LogManager;

/**
 *
 * @author Wolfgang Reder
 */
public class Main
{

  private static final CountDownLatch done = new CountDownLatch(1);

  private static void onTrackConfigPacket(ZCAN device,
                                          Packet packet)
  {
    System.out.println(packet);
    if (packet.getAdapter(CVInfoAdapter.class) != null) {
      CVInfoAdapter info = packet.getAdapter(CVInfoAdapter.class);
      printCV(info);
      if (info.getDecoderAddress() == 246) {
        done.countDown();
      }
    }
  }

  public static void printPowerStateInfo(ZCAN device) throws IOException
  {
//    try {
//      PowerStateInfo info = device.getPowerStateInfo(PowerOutput.OUT_1,
//                                                     10,
//                                                     TimeUnit.SECONDS);
//      System.out.println("State for " + info.getOutput() + ": " + info.getMode());
//      info = device.getPowerStateInfo(PowerOutput.OUT_2,
//                                      10,
//                                      TimeUnit.SECONDS);
//      System.out.println("State for " + info.getOutput() + ": " + info.getMode());
//    } catch (TimeoutException ex) {
//      Logger.getLogger(Main.class.getName()).log(Level.SEVERE,
//                                                 null,
//                                                 ex);
//    }
  }

  public static void printCV(CVInfoAdapter info)
  {
    if (info != null) {
      StringBuilder builder = new StringBuilder("Decoder ");
      builder.append(info.getDecoderAddress());
      builder.append(":CV #");
      builder.append(info.getNumber());
      builder.append("=");
      builder.append(info.getValue());
      builder.append(" (0x");
      Utils.appendHexString(info.getValue(),
                            builder,
                            2);
      builder.append(")");
      System.out.println(builder.toString());
    }
  }

  public static void main(String[] args) throws IOException, ExecutionException, TimeoutException, InterruptedException
  {
    try (InputStream is = Main.class.getResourceAsStream("loggerconfig.properties")) {
      LogManager.getLogManager().readConfiguration(is);
    }
    try (ZCAN device = ZCANFactory.open("192.168.1.145",
                                        14520,
                                        14521,
                                        null)) {
      device.addPacketListener(CommandGroup.TRACK_CONFIG_PUBLIC,
                               Main::onTrackConfigPacket);
//      device.addPacketListener(CommandGroup.LOCO,
//                               Main::onTrackConfigPacket);
//      device.addPacketListener(Main::onTrackConfigPacket);
//      LocoMode mode = device.takeOwnership(246);
//      LocoMode mode = device.getMode(246);
//      System.out.println(mode);
      device.readCV(246,
                    29);
////      printPowerStateInfo(device);
////      int b = System.in.read();
      done.await(20,
                 TimeUnit.SECONDS);
    }
  }

}
