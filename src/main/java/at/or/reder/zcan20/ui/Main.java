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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.logging.LogManager;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PatternOptionBuilder;

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
            desc("POM | SERVICE").
            hasArg().
            longOpt("mode").
            numberOfArgs(1).required(
            false).build()).
            addOption(Option.builder("d").argName("device").desc("MX10|MXULF MXULF default").hasArg().longOpt("device").
                    numberOfArgs(1).required(false).build()).
            addOption(Option.builder("p").argName("port").desc("Connection port (IP Addres or COM Port) to use").numberOfArgs(1).
                    hasArg().required(false).build()).
            addOption(Option.builder("a").argName("DCC Address").hasArg().longOpt("dcc-address").numberOfArgs(1).type(
                    PatternOptionBuilder.NUMBER_VALUE).build()).
            addOption(Option.builder().longOpt("read-cv").hasArg(false).build());
  }

  private final CommandLine commandLine;
  private final ProgrammingMode mode;
  private final String device;
  private final String port;
  private final int address;
  private final File outputFile;

  private Main(String[] args) throws ParseException
  {
    DefaultParser parser = new DefaultParser();
    this.commandLine = parser.parse(OPTIONS,
                                    args);
    ProgrammingMode tmpMode = ProgrammingMode.POM;

    if (commandLine.hasOption('m')) {
      try {
        tmpMode = ProgrammingMode.valueOf(commandLine.getOptionValue("m"));
      } catch (Throwable th) {
      }
    }
    mode = tmpMode;
    device = commandLine.getOptionValue("d",
                                        "MXULF");
    port = commandLine.getOptionValue("p");
    address = ((Number) commandLine.getParsedOptionValue("a")).intValue();
    String output = null;
    if (!commandLine.getArgList().isEmpty()) {
      output = commandLine.getArgList().get(0);
    }
    if (output != null) {
      outputFile = new File(output);
    } else {
      outputFile = null;
    }
  }

  public void run() throws IOException
  {
    MX1Tester tester = new MX1Tester(port);
    if (outputFile != null) {
      tester.readCV(mode,
                    address,
                    OutputFormat.ZCS,
                    outputFile);
    } else {
      try (PrintWriter writer = new PrintWriter(System.out)) {
        tester.readCV(mode,
                      address,
                      OutputFormat.ZCS,
                      writer,
                      System.lineSeparator());
      }
    }
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
