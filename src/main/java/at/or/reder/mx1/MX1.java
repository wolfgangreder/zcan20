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

import at.or.reder.dcc.LinkState;
import java.io.IOException;
import javax.swing.event.ChangeListener;

public interface MX1 extends AutoCloseable
{

  public boolean open() throws IOException;

  @Override
  public void close() throws IOException;

  public void reset() throws IOException;

  public void readCV(int address,
                     int iCV) throws IOException;

  public LinkState getLinkState();

  public void addChangeListener(ChangeListener evt);

  public void removeChangeListener(ChangeListener l);

  public void addMX1PacketListener(MX1PacketListener l);

  public void removeMX1PacketListener(MX1PacketListener l);

}