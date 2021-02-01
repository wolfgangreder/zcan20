/*
 * Copyright 2020 Wolfgang Reder.
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

import at.or.reder.dcc.DecoderInfo;
import at.or.reder.dcc.Direction;
import at.or.reder.dcc.IdentifyProvider;
import at.or.reder.dcc.LinkState;
import at.or.reder.dcc.PowerMode;
import at.or.reder.dcc.util.DCCUtils;
import at.or.reder.mx1.CommandStationInfo;
import at.or.reder.mx1.LocoInfo;
import at.or.reder.mx1.MX1;
import at.or.reder.mx1.MX1Factory;
import at.or.reder.mx1.MX1Packet;
import at.or.reder.mx1.MX1PacketAdapter;
import at.or.reder.mx1.MX1PacketObject;
import at.or.reder.mx1.MX1Port;
import at.or.reder.zcan20.DecoderType;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.Charset;
import java.text.MessageFormat;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import javax.swing.event.ChangeEvent;
import org.openide.util.Exceptions;

/**
 *
 * @author Wolfgang Reder
 */
public class MX1Tester
{

  private static final Set<Integer> TO_EXCLUDE = Set.of(198,
                                                        199,
                                                        200,
                                                        201,
                                                        202,
                                                        203,
                                                        204,
                                                        205,
                                                        206,
                                                        207,
                                                        208,
                                                        209,
                                                        210,
                                                        211,
                                                        212,
                                                        213,
                                                        214,
                                                        215,
                                                        216,
                                                        217,
                                                        218,
                                                        219,
                                                        220,
                                                        221,
                                                        222,
                                                        223,
                                                        224,
                                                        225,
                                                        226,
                                                        227,
                                                        228,
                                                        229,
                                                        230,
                                                        231,
                                                        232,
                                                        233,
                                                        234,
                                                        235,
                                                        236,
                                                        237,
                                                        238,
                                                        239,
                                                        240,
                                                        241,
                                                        242,
                                                        243,
                                                        244,
                                                        245,
                                                        246,
                                                        247,
                                                        248,
                                                        249,
                                                        898,
                                                        899,
                                                        900,
                                                        901,
                                                        902,
                                                        903,
                                                        904,
                                                        905,
                                                        906,
                                                        907,
                                                        908,
                                                        909,
                                                        910,
                                                        911,
                                                        912,
                                                        913,
                                                        914,
                                                        915,
                                                        916,
                                                        917,
                                                        918,
                                                        919,
                                                        920,
                                                        921,
                                                        922,
                                                        923,
                                                        924,
                                                        925,
                                                        926,
                                                        927,
                                                        928,
                                                        929,
                                                        930,
                                                        931,
                                                        932,
                                                        933,
                                                        934,
                                                        935,
                                                        936,
                                                        937,
                                                        938,
                                                        939,
                                                        940,
                                                        941,
                                                        942,
                                                        943,
                                                        944,
                                                        945,
                                                        946,
                                                        947,
                                                        948,
                                                        949,
                                                        950,
                                                        951,
                                                        952,
                                                        953,
                                                        954,
                                                        955,
                                                        956,
                                                        957,
                                                        958,
                                                        959,
                                                        960,
                                                        961,
                                                        962,
                                                        963,
                                                        964,
                                                        965,
                                                        966,
                                                        967,
                                                        968,
                                                        969,
                                                        970,
                                                        971,
                                                        972,
                                                        973,
                                                        974,
                                                        975,
                                                        976,
                                                        977,
                                                        978,
                                                        979);
  boolean linkStateSet;
  boolean cvPacketReceived;

  private void printResult(int cv,
                           int val,
                           long dur)
  {
    System.err.println(MessageFormat.format("CV #{0,number,0} read with value #{1,number,0} in {2,number,0}ms",
                                            new Object[]{cv, val, dur}));
  }

  private void printCommandStationInfo(CommandStationInfo info)
  {
    if (info == null) {
      return;
    }
    StringBuilder builder = new StringBuilder("Connected to ");
    switch (info.getDeviceId()) {
      case MX1.DEVICE_M31ZL:
        builder.append("MX31 ZL");
        break;
      case MX1.DEVICE_MX1_2000EC:
        builder.append("MX1 2000 EC");
        break;
      case MX1.DEVICE_MX1_2000HS:
        builder.append("MX1 2000 HS");
        break;
      case MX1.DEVICE_MXULF:
        builder.append("MXULF");
        break;
      default:
        builder.append(" #");
        builder.append(Integer.toString(info.getDeviceId()));
    }
//    builder.append("@0x");
//    builder.append(Integer.toHexString(info.getCANAddress()));
//    builder.append("\n");
//    builder.append("HW:");
//    builder.append(info.getHWVersionMajor());
//    builder.append(".");
//    builder.append(info.getHWVersionMinor());
    builder.append("\nSW:");
    builder.append(info.getSWVersionMajor());
    builder.append(".");
    builder.append(info.getSWVersionMinor());
//    builder.append("\nBoot:");
//    builder.append(info.getBootROMVersionMajor());
//    builder.append(".");
//    builder.append(info.getBootROMVersionMinor());
//    builder.append("\nRelease:");
//    builder.append(info.getReleaseDate().toString());
//    builder.append("\nMemorysize\nROM:");
//    builder.append(info.getROMPages());
//    builder.append(" pages (");
//    builder.append(info.getROMBytes());
//    builder.append(" bytes)\nRAM:");
//    builder.append(info.getRAMPages());
//    builder.append(" pages (");
//    builder.append(info.getRAMBytes());
//    builder.append(" bytes)");
    builder.append("\n\n");
    System.err.println(builder.toString());
  }

  private final String comPort;

  public MX1Tester(String comPort)
  {
    this.comPort = comPort;
  }

  public void run()
  {
    Map<String, String> config = new HashMap<>();
    MX1Port port = null;
    try (MX1 mx1 = MX1Factory.open("/dev/ttyACM0",
                                   config);
            Writer writer = new FileWriter("/home/wolfi/cvlist.csv")) {
//      mx1.addMX1PacketListener(this::onPacket);
      port = mx1.getLookup().lookup(MX1Port.class);
      mx1.addChangeListener(this::onLinkStateChanged);
      List<Integer> toRead = new ArrayList<>();
      int startcv = 1;//TO_EXCLUDE.stream().collect(Collectors.maxBy(Integer::compare)).orElse(1);

      for (int i = startcv; i <= 1024; ++i) {
        if (!TO_EXCLUDE.contains(i)) {
          toRead.add(i);
        }
      }
      final int numcv = toRead.size();
      synchronized (this) {
        while (!linkStateSet) {
          this.wait();
        }
      }
      printCommandStationInfo(mx1.getCommandStationInfo(10,
                                                        TimeUnit.SECONDS));
      long totalStart;
      int reads = 0;
      Map<Integer, Integer> result = new TreeMap<>();
      if (mx1.getLinkState() == LinkState.CONNECTED) {
        StringBuilder builder = new StringBuilder();
        try {
//          mx1.setPowerMode(PowerMode.ON);
//          Thread.sleep(2000);
          totalStart = System.currentTimeMillis();
          if (false) {
            for (int round = 0; !toRead.isEmpty() && round < 5; round++) {
              ListIterator<Integer> iter = toRead.listIterator();
              while (iter.hasNext()) {
                long start = System.currentTimeMillis();
                int cv = iter.next();
//              MX1Impl.LOGGER.log(Level.INFO,
//                                 "Reading CV " + cv);
                ++reads;
                int value = mx1.readCV(3,
                                       cv,
                                       2,
                                       TimeUnit.SECONDS);
                long dur = System.currentTimeMillis() - start;
                if (value != -1) {
                  result.put(cv,
                             value);
                  iter.remove();
//                writer.write('#');
//                writer.write(Long.toString(dur));
//                writer.write("ms\n");
                  writer.write(Integer.toString(cv));
                  writer.write('=');
                  writer.write(Integer.toString(value));
                  writer.write('\n');
                  writer.flush();
                }
                printResult(cv,
                            value,
                            dur);
              }
            }
          } else {
            try {
              IdentifyProvider ip = mx1.getLookup().lookup(IdentifyProvider.class);
              DecoderInfo di = DCCUtils.identifyDecoder(ip,
                                                     0,
                                                     false);
              BitSet functions = new BitSet(13);
              //functions.set(0);
              System.err.println(di.toString());
              mx1.locoControl(di.getAddress(),
                              0,
                              di.getSpeedSteps(),
                              Direction.FORWARD,
                              false,
                              functions);
              LocoInfo li = mx1.getLocoInfo(di.getAddress(),
                                            10,
                                            TimeUnit.SECONDS);
              Thread.sleep(5000);
              System.err.println(li);
              mx1.locoControl(di.getAddress(),
                              200,
                              di.getSpeedSteps(),
                              Direction.REVERSE,
                              false,
                              functions);
              li = mx1.getLocoInfo(di.getAddress(),
                                   10,
                                   TimeUnit.SECONDS);
              System.err.println(li);
              Thread.sleep(5000);
              mx1.locoControl(di.getAddress(),
                              700,
                              di.getSpeedSteps(),
                              Direction.REVERSE,
                              false,
                              functions);
              Thread.sleep(5000);
              li = mx1.getLocoInfo(di.getAddress(),
                                   10,
                                   TimeUnit.SECONDS);
              System.err.println(li);
              mx1.locoControl(di.getAddress(),
                              1024,
                              di.getSpeedSteps(),
                              Direction.REVERSE,
                              false,
                              functions);
              Thread.sleep(5000);
              mx1.locoControl(di.getAddress(),
                              0,
                              di.getSpeedSteps(),
                              Direction.REVERSE,
                              false,
                              functions);
              Thread.sleep(15000);
              mx1.locoControl(di.getAddress(),
                              512,
                              di.getSpeedSteps(),
                              Direction.FORWARD,
                              false,
                              functions);
              Thread.sleep(15000);
              mx1.locoControl(di.getAddress(),
                              0,
                              di.getSpeedSteps(),
                              Direction.FORWARD,
                              false,
                              functions);
              Thread.sleep(5000);
            } finally {
              mx1.setPowerMode(PowerMode.OFF);
            }
          }
          long totalEnd = System.currentTimeMillis();
          Duration dur = Duration.of(totalEnd - totalStart,
                                     ChronoUnit.MILLIS);
          builder.append("Read ");
          builder.append(Integer.toString(numcv - toRead.size()));
          builder.append(" CV with ");
          builder.append(Integer.toString(reads));
          builder.append(" read commands in ");
          builder.append(dur.toString());
          builder.append(" med. ");
          if (reads != 0) {
            dur = Duration.of((totalEnd - totalStart) / reads,
                              ChronoUnit.MILLIS);
            builder.append(dur.toString());
          }
          System.out.println(builder.toString());
        } finally {
//          mx1.setPowerMode(PowerMode.OFF);
        }
        builder.setLength(0);
      }
    } catch (InterruptedException | IOException ex) {
      Exceptions.printStackTrace(ex);
    }
    if (port != null) {
      StringBuilder builder = new StringBuilder();
      builder.append("RX:");
      builder.append(Long.toUnsignedString(port.getPacketsReceived()));
      builder.append(" (");
      builder.append(Long.toUnsignedString(port.getBytesReceived()));
      builder.append(" bytes) TX:");
      builder.append(Long.toUnsignedString(port.getPacketsSent()));
      builder.append(" (");
      builder.append(Long.toUnsignedString(port.getBytesSent()));
      builder.append(" bytes)");
      System.out.println(builder.toString());
    }

  }

  private void onLinkStateChanged(ChangeEvent evt)
  {
    synchronized (this) {
      linkStateSet = true;
      this.notifyAll();
    }
  }

  private void onPacket(MX1PacketObject evt)
  {
    MX1Packet packet = evt.getPacket();
    if (packet != null) {
      System.err.println("Receive Packet " + packet);
      MX1PacketAdapter adapter = packet.getAdapter(MX1PacketAdapter.class);
      if (adapter != null) {
        System.err.println("    PacketAdapter " + adapter.toString());
      }
    }
  }

  private MX1 connect() throws IOException
  {
    Map<String, String> config = new HashMap<>();
    MX1 result = MX1Factory.open(comPort,
                                 config);
    result.addChangeListener(this::onLinkStateChanged);
    if (result.getLinkState() == LinkState.CLOSED) {
      try {
        synchronized (this) {
          while (!linkStateSet) {
            this.wait();
          }
        }
      } catch (InterruptedException ex) {
      }
    }
    return result;
  }

  public void readCV(ProgrammingMode mode,
                     int address,
                     OutputFormat format,
                     File outputFile) throws IOException
  {
    Charset ch = Charset.forName("CP1252");
    String nl = "\r\n";
    try (FileWriter writer = new FileWriter(outputFile,
                                            ch)) {
      readCV(mode,
             address,
             format,
             writer,
             nl);
    }
  }

  public void readCV(ProgrammingMode mode,
                     int address,
                     OutputFormat format,
                     Writer writer,
                     String nl) throws IOException
  {
    writer.write("// *** MX32 CV Set ***");
    writer.write(nl);
    writer.write("[FileInfo]");
    writer.write(nl);
    writer.write("Version=01.11.00002");
    writer.write(nl);
    writer.write("Date=");
    writer.write(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy.MM.dd")));
    writer.write(nl);
    writer.write("Name=CV Set");
    writer.write(nl);
    writer.write("[/FileInfo]");
    writer.write(nl);
    writer.write("//********************");
    writer.write(nl);
    writer.write("[Config]");
    writer.write(nl);
    writer.write("Id=13");
    writer.write(nl);
    writer.write("Type=2");
    writer.write(nl);
    writer.write("Name=CV Set");
    writer.write(nl);
    AtomicReference<DecoderInfo> decoderInfo = new AtomicReference<>();
    SortedMap<Integer, Integer> cvMap = new TreeMap<>();
    readCV(mode,
           address,
           cvMap::put,
           decoderInfo::set);
    writer.write("Decoder=");
    if (decoderInfo.get() != null) {
      writer.write(decoderInfo.get().getManufacturerName());
    }
    writer.write(nl);
    writer.write("Info=01|");
    writer.write(nl);
    writer.write("//********************");
    writer.write(nl);
    for (Map.Entry<Integer, Integer> e : cvMap.entrySet()) {
      writer.write("CV=");
      writer.write(Integer.toString(e.getKey()));
      writer.write(':');
      writer.write(Integer.toString(e.getValue()));
      writer.write(nl);
    }
    writer.write("[/Config]");
    writer.write(nl);
    writer.write("//********************");
    writer.write(nl);
    writer.write("[ZCS]");
    writer.write(nl);
    writer.write("Typ=0");
    writer.write(nl);
    writer.write("[/ZCS]");
    writer.write(nl);
  }

  public void readCV(ProgrammingMode mode,
                     int address,
                     BiConsumer<Integer, Integer> cvConsumer,
                     Consumer<DecoderInfo> decoderInfoConsumer) throws IOException
  {
    try (MX1 mx1 = connect()) {
      if (mode == ProgrammingMode.SERVICE) {
        address = 0;
      }
      IdentifyProvider ip = mx1.getLookup().lookup(IdentifyProvider.class
      );
      DecoderInfo di = DCCUtils.identifyDecoder(ip,
                                             address,
                                             mode == ProgrammingMode.SERVICE);
      if (decoderInfoConsumer != null) {
        decoderInfoConsumer.accept(di);
      }
      DecoderType zimoType = di.getLookup().lookup(DecoderType.class
      );
      int cvFrom = 1;
      int cvTo = 512;
      if (zimoType != null && zimoType.isSound()) {
        cvTo = 889;
      }
      List<Integer> toRead = new ArrayList<>();
      for (int i = cvFrom; i <= cvTo; ++i) {
        if (!TO_EXCLUDE.contains(i)) {
          toRead.add(i);
        }
      }
      int round = 0;
      long timeOut = mode == ProgrammingMode.SERVICE ? 10 : 2;
      while ((round < 5) && !toRead.isEmpty()) {
        ListIterator<Integer> iter = toRead.listIterator();
        while (iter.hasNext()) {
          int iCV = iter.next();
          int cvValue = mx1.readCV(address,
                                   iCV,
                                   timeOut,
                                   TimeUnit.SECONDS);
          if (cvValue != -1) {
            iter.remove();
            cvConsumer.accept(iCV,
                              cvValue);
          }
        }
      }
    }
  }

}
