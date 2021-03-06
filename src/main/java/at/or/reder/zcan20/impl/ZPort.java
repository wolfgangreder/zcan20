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
package at.or.reder.zcan20.impl;

import at.or.reder.zcan20.packet.Packet;
import at.or.reder.zcan20.packet.Ping;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.Future;
import javax.validation.constraints.NotNull;

/**
 *
 * @author Wolfgang Reder
 */
public interface ZPort extends AutoCloseable
{

  public String getName();

  public void start() throws IOException;

  @Override
  public void close() throws IOException;

  public void sendPacket(@NotNull Packet packet) throws IOException;

  public void sendRaw(@NotNull ByteBuffer buffer) throws IOException;

  public Packet readPacket() throws IOException;

  public Future<Ping> sendInitPacket(ZCANImpl zcan) throws IOException;

}
