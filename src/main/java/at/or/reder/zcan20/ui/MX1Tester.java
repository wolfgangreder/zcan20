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
import at.or.reder.mx1.CVPacketAdapter;
import at.or.reder.mx1.MX1;
import at.or.reder.mx1.MX1Factory;
import at.or.reder.mx1.MX1PacketObject;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
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
  int cv8Value;

  public void run()
  {
    Map<String, String> config = new HashMap<>();
    try (MX1 mx1 = MX1Factory.open("/dev/ttyACM0",
                                   config)) {
      mx1.addChangeListener(this::onLinkStateChanged);
      mx1.addMX1PacketListener(this::onPacket);
      synchronized (this) {
        while (!linkStateSet) {
          this.wait();
        }
      }
      System.err.println("Linkstate=" + mx1.getLinkState());
      if (mx1.getLinkState() == LinkState.CONNECTED) {
        mx1.readCV(0,
                   8);
        synchronized (this) {
          while (!cvPacketReceived) {
            this.wait();
          }
        }
      }
      System.err.println("CV8=" + cv8Value);
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

  private void onPacket(MX1PacketObject evt)
  {
    CVPacketAdapter cva = evt.getPacket().getAdapter(CVPacketAdapter.class);
    if (cva != null) {
      if (cva.getCV() == 8) {
        cv8Value = cva.getValue();
        synchronized (this) {
          cvPacketReceived = true;
          this.notifyAll();
        }
      }
    }
  }

}
