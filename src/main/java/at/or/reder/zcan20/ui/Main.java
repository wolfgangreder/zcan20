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

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.LogManager;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class Main
{

  private enum GUIMode
  {
    LOGGER, BUILDER;
  }
  private static final Options OPTIONS;

  static {
    OPTIONS = new Options();
    OPTIONS.addOption(Option.builder("m").
            argName("mode").
            desc("GUI Mode [logger,builder]").
            hasArg().
            longOpt("mode").
            numberOfArgs(1).required(
            false).build());
  }

  private final CommandLine commandLine;
  private final GUIMode mode;

  private Main(String[] args) throws ParseException
  {
    DefaultParser parser = new DefaultParser();
    this.commandLine = parser.parse(OPTIONS,
                                    args);
    GUIMode m = GUIMode.LOGGER;
    if (commandLine.hasOption('m')) {
      try {
        m = GUIMode.valueOf(commandLine.getOptionValue('m',
                                                       GUIMode.LOGGER.name()).toUpperCase());
      } catch (Throwable th) {

      }
    }
    this.mode = m;
  }

  public void run()
  {
    new MX1Tester().run();
//    SwingUtilities.invokeLater(() -> {
//      switch (mode) {
//        case BUILDER:
//          new CANBuilder(commandLine).setVisible(true);
//          break;
//        default:
//          new PacketLogger(commandLine).setVisible(true);
//          break;
//      }
//    });
  }

  public static void main(String[] args) throws IOException, ParseException
  {
    try (InputStream is = Main.class.getResourceAsStream("/logging.properties")) {
      if (is != null) {
        LogManager.getLogManager().
                readConfiguration(is);
      }
    }
    new Main(args).run();
  }

}
