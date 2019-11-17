/*
 * Copyright 2019 Wolfgang Reder.
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
package at.or.reder.zcan20.impl;

import at.or.reder.zcan20.NetworkControl;
import static at.or.reder.zcan20.ZCAN.LOGGER;
import java.io.IOException;
import java.util.Random;
import java.util.logging.Level;
import org.openide.util.RequestProcessor;

/**
 *
 * @author Wolfgang Reder
 */
final class NetworkControlImpl implements NetworkControl, AutoCloseable
{

  private final ZCANImpl zcan;
  private volatile RequestProcessor.Task ownerPing;
  private volatile int ownerPingIntervall = 5000;
  private volatile int ownerPingJitter = 100;
  private final Random jitterRandom = new Random();

  public NetworkControlImpl(ZCANImpl zcan)
  {
    this.zcan = zcan;
  }

  @Override
  public void close()
  {
    if (ownerPing != null) {
      ownerPing.cancel();
    }
    ownerPing = null;
  }

  @Override
  public boolean sendPing()
  {
    try {
      zcan.doSendPacket(zcan.createPacketBuilder().buildPingPacket(zcan.getNID()));
    } catch (IOException ex) {
      LOGGER.log(Level.SEVERE,
                 "sendPing",
                 ex);
      return false;
    }
    return true;
  }

  @Override
  public boolean isAutopingEnabled()
  {
    return ownerPing != null;
  }

  private void postPing() throws IOException
  {
    zcan.doSendPacket(zcan.createPacketBuilder().buildPingPacket(zcan.getNID()));
  }

  private int getScheduleTime()
  {
    return ownerPingIntervall + jitterRandom.nextInt((int) ownerPingJitter) - (ownerPingJitter / 2);
  }

  void schedulePing()
  {
    if (ownerPing != null) {
      int nextSchedule = getScheduleTime();
      LOGGER.log(Level.FINER,
                 "Schedule ping in {0,number,0} ms",
                 new Object[]{nextSchedule});
      ownerPing.schedule(nextSchedule);
    }
  }

  private void onAutomaticPing()
  {
    try {
      postPing();
    } catch (IOException ex) {
      LOGGER.log(Level.WARNING,
                 "onAutomaticPing:" + ex.getMessage(),
                 ex);
    } finally {
      schedulePing();
    }
  }

  @Override
  public void setAutopingEnabled(boolean e)
  {
    if (e != isAutopingEnabled()) {
      if (e && zcan.isOpen()) {
        if (ownerPing == null) {
          ownerPing = zcan.postTask(this::onAutomaticPing,
                                    getScheduleTime());
        }
      } else if (ownerPing != null) {
        ownerPing.cancel();
        ownerPing = null;
      }
    }
  }

  @Override
  public int getAutopingIntervall()
  {
    return (int) (ownerPingIntervall / 1000);
  }

  @Override
  public void setAutopingIntervall(int autoPingIntervall)
  {
    this.ownerPingIntervall = autoPingIntervall * 1000;
  }

  @Override
  public long getLastPingTimestamp()
  {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

}
