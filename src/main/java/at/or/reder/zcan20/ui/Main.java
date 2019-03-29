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
package at.or.reder.zcan20.ui;

import at.or.reder.zcan20.ZCAN;
import at.or.reder.zcan20.ZCANFactory;
import at.or.reder.zcan20.packet.Packet;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.logging.LogManager;
import javax.validation.constraints.NotNull;

/**
 *
 * @author Wolfgang Reder
 */
public class Main implements AutoCloseable
{

  private final String[] args;

  private ZCAN device;

  private Main(String[] args)
  {
    this.args = Arrays.copyOf(args,
                              args.length);
  }

  private void open() throws IOException
  {
    device = ZCANFactory.open("192.168.1.145",
                              14520,
                              14521,
                              null,
                              5,
                              TimeUnit.SECONDS);

  }

  private void checkOpen() throws IOException
  {
    if (device == null) {
      open();
    }
  }

  public void run() throws IOException
  {
    checkOpen();
    device.addPacketListener(this::toConsolePacketListener);

  }

  @Override
  public void close() throws IOException
  {
    if (device != null) {
      device.close();
    }
    device = null;
  }

  private void toConsolePacketListener(@NotNull ZCAN connection,
                                       @NotNull Packet packet)
  {

  }

  public static void main(String[] args) throws IOException
  {
    try (InputStream is = Main.class.getResourceAsStream("loggerconfig.properties")) {
      LogManager.getLogManager().readConfiguration(is);
    }
    try (Main main = new Main(args)) {
      main.run();
    }
  }

}