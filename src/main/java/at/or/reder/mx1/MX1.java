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
package at.or.reder.mx1;

import at.or.reder.dcc.Direction;
import at.or.reder.dcc.LinkState;
import at.or.reder.dcc.PowerMode;
import at.or.reder.dcc.SpeedstepSystem;
import java.io.IOException;
import java.util.BitSet;
import java.util.concurrent.TimeUnit;
import javax.swing.event.ChangeListener;
import org.openide.util.Lookup;

public interface MX1 extends AutoCloseable, Lookup.Provider
{

  public static final int DEVICE_MX1_2000HS = 1;
  public static final int DEVICE_MX1_2000EC = 2;
  public static final int DEVICE_M31ZL = 3;
  public static final int DEVICE_MXULF = 4;

  public boolean open() throws IOException;

  @Override
  public void close() throws IOException;

  public void reset() throws IOException;

  public void readCV(int address,
                     int iCV) throws IOException;

  public int readCV(int address,
                    int iCV,
                    long timeout,
                    TimeUnit unit) throws IOException;

  public void writeCV(int address,
                      int iCV,
                      int value) throws IOException;

  public boolean writeCV(int address,
                         int iCV,
                         int value,
                         long timeout,
                         TimeUnit unit) throws IOException;

  public void getPowerMode() throws IOException;

  public PowerMode getPowerMode(long timeout,
                                TimeUnit unit) throws IOException;

  public void setPowerMode(PowerMode newMode) throws IOException;

  public void getCommandStationInfo() throws IOException;

  public CommandStationInfo getCommandStationInfo(long timeout,
                                                  TimeUnit unit) throws IOException;

  public void setFunction(int address,
                          int iFunction,
                          int val) throws IOException;

  public int getFunction(int address,
                         int iFunction) throws IOException;

  public int getFunction(int address,
                         int iFunction,
                         long timeout,
                         TimeUnit unit) throws IOException;

  public void setSpeed(int address,
                       int speed,
                       SpeedstepSystem speedSystem) throws IOException;

  public void emergencyStop(int address) throws IOException;

  public void locoControl(int address,
                          int speed,
                          SpeedstepSystem speedSytem,
                          Direction direction,
                          boolean man,
                          BitSet functions) throws IOException;

  public void getLocoInfo(int address) throws IOException;

  public LocoInfo getLocoInfo(int address,
                              long timeout,
                              TimeUnit unit) throws IOException;

  public LinkState getLinkState();

  public void addChangeListener(ChangeListener evt);

  public void removeChangeListener(ChangeListener l);

  public void addMX1PacketListener(MX1PacketListener l);

  public void removeMX1PacketListener(MX1PacketListener l);

}
