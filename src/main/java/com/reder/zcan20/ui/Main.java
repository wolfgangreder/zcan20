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
import com.reder.zcan20.packet.Packet;
import com.reder.zcan20.packet.PacketAdapter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
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

  private static void onTrackConfigPacket(ZCAN device,
                                          Packet packet)
  {
    PacketAdapter adapter = packet.getAdapter(PacketAdapter.class);
    if (adapter != null) {
      System.out.println("Packet from " + Integer.toHexString(packet.getSenderNID() & 0xffff) + ": " + adapter);
    } else {
      System.out.println(packet);
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
                                        null,
                                        5,
                                        TimeUnit.SECONDS);
            LineNumberReader in = new LineNumberReader(new InputStreamReader(System.in))) {
      device.addPacketListener(CommandGroup.TRACK_CONFIG_PUBLIC,
                               Main::onTrackConfigPacket);
      device.readCV((short) 1118,
                    (short) 29);
      in.readLine();
    }
  }

}
