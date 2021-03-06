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

import java.io.IOException;
import java.util.function.Consumer;
import javax.validation.constraints.NotNull;

public interface MX1Port extends AutoCloseable
{

  public void open() throws IOException;

  @Override
  public void close() throws IOException;

  public void sendPacket(@NotNull MX1Packet packet) throws IOException;

  public Consumer<MX1Packet> getPacketListener();

  public void setPacketListener(Consumer<MX1Packet> listener);

  public long getBytesSent();

  public long getBytesReceived();

  public long getPacketsSent();

  public long getPacketsReceived();

}
