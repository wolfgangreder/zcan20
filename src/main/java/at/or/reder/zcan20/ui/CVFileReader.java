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
import java.io.LineNumberReader;
import java.io.Reader;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 *
 * @author Wolfgang Reder
 */
public class CVFileReader
{

  private static enum State
  {
    INIT,
    FILEINFO,
    CONFIG
  }
  private final SortedMap<Integer, Short> values = new TreeMap<>();
  private final Map<String, String> fileInfo = new HashMap<>();
  private final Map<String, String> config = new HashMap<>();

  public CVFileReader()
  {
  }

  private void insertIntoMap(String line,
                             Map<String, String> mapToAdd)
  {
    int equalPos = line.indexOf('=');
    if (equalPos > 0) {
      mapToAdd.put(line.substring(0,
                                  equalPos),
                   line.substring(equalPos + 1));
    }
  }

  private void addCVValue(String line) throws IOException
  {
    int pos = line.indexOf('=');
    if (pos > 0) {
      String strVal = line.substring(pos + 1);
      pos = strVal.indexOf(':');
      try {
        int num = Integer.parseInt(strVal.substring(0,
                                                    pos));
        int val = Integer.parseInt(strVal.substring(pos + 1));
        values.put(num,
                   (short) val);
      } catch (NumberFormatException ex) {
        throw new IOException(ex);
      }
    }
  }

  public void read(Reader r) throws IOException
  {
    State state = State.INIT;
    try (LineNumberReader reader = new LineNumberReader(r)) {
      String line;
      while ((line = reader.readLine()) != null) {
        if (line.startsWith("//")) {
          continue;
        }
        switch (state) {
          case INIT:
            if ("[FileInfo]".equals(line)) {
              state = State.FILEINFO;
            } else if ("[Config]".equals(line)) {
              state = State.CONFIG;
            }
            break;
          case FILEINFO:
            if ("[/FileInfo]".equals(line)) {
              state = State.INIT;
            } else {
              insertIntoMap(line,
                            fileInfo);
            }
            break;
          case CONFIG:
            if ("[/Config]".equals(line)) {
              state = State.INIT;
            } else if (line.startsWith("CV=")) {
              addCVValue(line);
            } else {
              insertIntoMap(line,
                            config);
            }
        }
      }
    }
  }

  public void printZCS(Writer writer) throws IOException
  {
    writer.write("// *** MX32 CV Set ***\r\n");
    writer.write("[FileInfo]\r\n");
    writer.write("Content=Config.CV\r\n");
    writer.write("Version=01.11.00002\r\n");
    writer.write("Date=2017.12.27\r\n");
    writer.write("Name=CV Set\r\n");
    writer.write("[/FileInfo]\r\n");
    writer.write("//********************\r\n");
    writer.write("[Config]\r\n");
    writer.write("Id=13\r\n");
    writer.write("Type=2\r\n");
    writer.write("Name=CV Set\r\n");
    writer.write("Decoder=ZIMO\r\n");
    writer.write("Info=01|\r\n");
    writer.write("//********************\r\n");
    for (int i = 1; i < 1025; ++i) {
      Short val = values.get(i);
      if (val != null) {
        writer.write("CV=");
        writer.write(Integer.toString(i));
        writer.write(":");
        writer.write(Integer.toString(Short.toUnsignedInt(val)));
        writer.write("\r\n");
      }
    }
    writer.write("[/Config]\r\n");
    writer.write("//********************\r\n");
    writer.write("[ZCS]\r\n");
    writer.write("Typ=0\r\n");
    writer.write("[/ZCS]\r\n");
    writer.flush();
  }

  public Map<Integer, Short> getValues()
  {
    return new TreeMap<>(values);
  }

}
