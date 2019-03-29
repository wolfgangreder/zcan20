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

import at.or.reder.zcan20.CVReadState;
import at.or.reder.zcan20.ZCAN;
import at.or.reder.zcan20.packet.CVInfoAdapter;
import at.or.reder.zcan20.packet.Packet;
import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.function.Consumer;
import javax.validation.constraints.NotNull;
import org.openide.util.Exceptions;

/**
 *
 * @author Wolfgang Reder
 */
public final class CVBatchReader implements Runnable
{

  private final ZCAN device;
  private final Writer logger;
  private final short address;
  private final Consumer<CVInfoAdapter> infoConsumer;
  private final SortedMap<Integer, CVInfoAdapter> result = new TreeMap<>();

  public CVBatchReader(@NotNull ZCAN device,
                       @NotNull Writer logger,
                       short address,
                       Consumer<CVInfoAdapter> cvConsumer)
  {
    this.device = Objects.requireNonNull(device,
                                         "device is null");
    this.logger = Objects.requireNonNull(logger,
                                         "logger is null");
    this.address = address;
    if (cvConsumer != null) {
      infoConsumer = cvConsumer;
    } else {
      infoConsumer = this::logCVInfo;
    }
  }

  private void logCVInfo(CVInfoAdapter adapter)
  {
//    try {
//      logger.write(adapter.toString());
//      logger.write(System.getProperty("line.separator"));
//      logger.flush();
//    } catch (IOException ex) {
//      Exceptions.printStackTrace(ex);
//    }
  }

  private boolean waitBusy(Packet packet,
                           int cv)
  {
    CVInfoAdapter adapter = packet.getAdapter(CVInfoAdapter.class);
    return adapter != null && adapter.getNumber() == cv && (adapter.getReadState() == CVReadState.READ || adapter.getReadState()
                                                                                                          == CVReadState.ACK);
  }

  public void reset()
  {
    result.clear();
  }

  public SortedMap<Integer, CVInfoAdapter> getValues()
  {
    return result;
  }

  public boolean readCriticalCVS()
  {
    Set<Integer> toRead = new HashSet<>(Arrays.asList(29,
                                                      8,
                                                      7,
                                                      65,
                                                      250,
                                                      251,
                                                      252,
                                                      253,
                                                      260,
                                                      261,
                                                      262,
                                                      263,
                                                      1,
                                                      17,
                                                      18));
    for (int i = 0; i < 5 && !toRead.isEmpty(); ++i) {
      toRead = readCVS(toRead);
    }
    return toRead.isEmpty();
  }

  public Set<Integer> readCVS(Collection<Integer> toRead)
  {
    Set<Integer> notRead = new TreeSet<>();
    for (int i : toRead) {
      try {
        CVInfoAdapter adapter = device.readCV(address,
                                              i,
                                              5000,
                                              (p) -> waitBusy(p,
                                                              i)
        );
        if (adapter != null && adapter.getReadState() == CVReadState.READ && adapter.getNumber() == i) {
          infoConsumer.accept(adapter);
          result.put(i,
                     adapter);
        } else {
          notRead.add(i);
        }
      } catch (IOException ex) {
        Exceptions.printStackTrace(ex);
      }
    }
    return notRead;
  }

  @Override
  public void run()
  {
    Set<Integer> toRead = new TreeSet<>();
    for (int i = 1; i < 1025; ++i) {
      toRead.add(i);
    }
    for (int i = 0; i < 3 && !toRead.isEmpty(); ++i) {
      try {
        toRead = readCVS(toRead);
        logger.write("After run " + i + ":" + toRead.size() + " CV's left\n");
        logger.flush();
      } catch (IOException ex) {
        Exceptions.printStackTrace(ex);
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
      CVInfoAdapter adapter = result.get(i);
      if (adapter != null && adapter.getReadState() == CVReadState.READ) {
        writer.write("CV=");
        writer.write(Integer.toString(adapter.getNumber()));
        writer.write(":");
        writer.write(Integer.toString(Short.toUnsignedInt(adapter.getValue())));
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

}
