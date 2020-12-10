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

import at.or.reder.dcc.LinkState;
import at.or.reder.dcc.PowerMode;
import at.or.reder.mx1.MX1;
import at.or.reder.mx1.MX1Factory;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;
import javax.swing.event.ChangeEvent;
import org.openide.util.Exceptions;

/**
 *
 * @author Wolfgang Reder
 */
public class MX1Tester
{

  boolean linkStateSet;
  boolean cvPacketReceived;

  private void printResult(int cv,
                           int val,
                           long start,
                           long end)
  {
    System.err.println(MessageFormat.format("CV #{0,number,0} read with value #{1,number,0} in {2,number,0}ms",
                                            new Object[]{cv, val, end - start}));
  }

  public void run()
  {
    Map<String, String> config = new HashMap<>();
    try (MX1 mx1 = MX1Factory.open("/dev/ttyACM0",
                                   config)) {
      mx1.addChangeListener(this::onLinkStateChanged);
      List<Integer> toRead = new ArrayList<>();
      for (int i = 1; i <= 256; ++i) {
        toRead.add(i);
      }
      synchronized (this) {
        while (!linkStateSet) {
          this.wait();
        }
      }
      Map<Integer, Integer> result = new TreeMap<>();
      if (mx1.getLinkState() == LinkState.CONNECTED) {
        try {
          mx1.setPowerMode(PowerMode.ON);
          for (int round = 0; !toRead.isEmpty() && round < 5; round++) {
            ListIterator<Integer> iter = toRead.listIterator();
            while (iter.hasNext()) {
              long start = System.currentTimeMillis();
              int cv = iter.next();
              int value = mx1.readCV(0,
                                     cv,
                                     10,
                                     TimeUnit.SECONDS);
              if (value != -1) {
                result.put(cv,
                           value);
                iter.remove();
              }
              printResult(cv,
                          value,
                          start,
                          System.currentTimeMillis());
            }
          }
        } finally {
          mx1.setPowerMode(PowerMode.OFF);
        }
      }
      try (Writer writer = new FileWriter("/home/wolfi/cvlist.csv")) {
        for (Map.Entry<Integer, Integer> e : result.entrySet()) {
          writer.write(e.getKey());
          writer.write(";");
          writer.write(e.getValue());
          writer.write("\n");
        }
      }
    } catch (InterruptedException | IOException ex) {
      Exceptions.printStackTrace(ex);
    }

  }

  private void onLinkStateChanged(ChangeEvent evt)
  {
    synchronized (this) {
      linkStateSet = true;
      this.notifyAll();
    }
  }

}
