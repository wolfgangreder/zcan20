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

import at.or.reder.zcan20.Loco;
import at.or.reder.zcan20.LocoActive;
import at.or.reder.zcan20.LocoMode;
import at.or.reder.zcan20.packet.CVInfoAdapter;
import at.or.reder.zcan20.packet.Packet;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.openide.util.Exceptions;
import org.openide.util.RequestProcessor;

/**
 *
 * @author Wolfgang Reder
 */
final class LocoImpl implements Loco
{

  private final ZCANImpl zcan;
  private final LocoMode mode;
  private final short loco;
  private final RequestProcessor.Task pingTask;

  public LocoImpl(ZCANImpl zcan,
                  LocoMode mode,
                  short loco)
  {
    this.zcan = zcan;
    this.mode = mode;
    this.loco = loco;
    RequestProcessor rp = zcan.getLookup().lookup(RequestProcessor.class);
    pingTask = rp.create(this::sendLocoPing);
//    pingTask.schedule(500);
  }

  @Override
  public LocoMode getMode()
  {
    return mode;
  }

  @Override
  public short getLoco()
  {
    return loco;
  }

  private void sendLocoPing()
  {
    try {
      zcan.doSendPacket(zcan.createPacketBuilder().buildLocoActivePacket(loco,
                                                                         LocoActive.ACTIVE).build());
    } catch (IOException ex) {
      Exceptions.printStackTrace(ex);
    } finally {
      pingTask.schedule(500);
    }
  }

  @Override
  public void close() throws IOException
  {
    if (!pingTask.isFinished()) {
      pingTask.cancel();
    }
    zcan.doSendPacket(zcan.createPacketBuilder().buildLocoActivePacket(loco,
                                                                       LocoActive.UNKNOWN).build());
  }

  @Override
  public byte readCV(int cv,
                     int timeout) throws IOException, TimeoutException
  {
    try {
      Future<CVInfoAdapter> result = readCV(cv);
      CVInfoAdapter adapter = result.get(timeout,
                                         TimeUnit.MILLISECONDS);
      return (byte) (adapter.getValue() & 0xff);
    } catch (InterruptedException ex) {
      Thread.currentThread().interrupt();
      throw new IOException(ex);
    } catch (ExecutionException ex) {
      throw new IOException(ex);
    }
  }

  @Override
  public Future<CVInfoAdapter> readCV(int cv) throws IOException
  {
    Packet packet = zcan.createPacketBuilder().buildReadCVPacket(zcan.getMasterNID(),
                                                                 loco,
                                                                 cv);
    sendLocoPing();
    return zcan.doSendPacket(packet,
                             CVInfoAdapter.SELECTOR::matches,
                             CVInfoAdapter.class);
  }

  @Override
  public void clearCV() throws IOException
  {
    Packet packet = zcan.createPacketBuilder().buildClearCVPacket(zcan.getMasterNID(),
                                                                  loco).build();
    zcan.doSendPacket(packet);
  }

}
