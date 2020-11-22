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

import at.or.reder.dcc.Direction;
import at.or.reder.dcc.util.Utils;
import at.or.reder.zcan20.CommandGroup;
import at.or.reder.zcan20.CommandMode;
import at.or.reder.zcan20.Loco;
import at.or.reder.zcan20.LocoActive;
import at.or.reder.zcan20.LocoMode;
import at.or.reder.zcan20.PacketListener;
import at.or.reder.zcan20.SpeedFlags;
import at.or.reder.zcan20.ZCAN;
import at.or.reder.zcan20.ZCANError;
import at.or.reder.zcan20.packet.CVInfoAdapter;
import at.or.reder.zcan20.packet.LocoActivePacketAdapter;
import at.or.reder.zcan20.packet.LocoFuncPacketAdapter;
import at.or.reder.zcan20.packet.LocoModePacketAdapter;
import at.or.reder.zcan20.packet.LocoSpeedPacketAdapter;
import at.or.reder.zcan20.packet.Packet;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Predicate;
import java.util.logging.Level;
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
  private final PacketListener funcListener = this::onFuncPacket;
  private final PacketListener speedListener = this::onSpeedPacket;
  private final Predicate<Packet> funcFilter = this::isFuncPacket;
  private final Predicate<Packet> speedFilter = this::isSpeedPacket;
  private Integer _speed;
  private Direction _direction;
  private final SortedMap<Integer, Integer> _functions = new TreeMap<>();

  public LocoImpl(ZCANImpl zcan,
                  LocoMode mode,
                  short loco,
                  boolean takeOwnership) throws IOException
  {
    this.zcan = zcan;
    this.mode = mode;
    this.loco = loco;
    zcan.addPacketListener(speedFilter,
                           speedListener);
    zcan.addPacketListener(funcFilter,
                           funcListener);
    RequestProcessor rp = zcan.getLookup().lookup(RequestProcessor.class);
    pingTask = rp.create(this::sendLocoPing,
                         true);
    for (int i = 0; i < ZCAN.NUM_FUNCTION; ++i) {
      _functions.put(i,
                     null);
    }
    setOwner(takeOwnership);
  }

  @Override
  public void scanFunctions() throws IOException
  {
    for (int i = 0; i < ZCAN.NUM_FUNCTION; ++i) {
      zcan.doSendPacket(zcan.createPacketBuilder().buildLocoFunctionPacket(loco,
                                                                           (short) i));
    }
  }

  private Object getLock()
  {
    return zcan.getLock();
  }

  private void takeOwnership(boolean force) throws IOException
  {
    ByteBuffer buffer = Utils.allocateLEBuffer(8);
    buffer.putShort(loco);
    // 1. status abfragen LOCO.0x10.REQUEST
    Packet packet = zcan.createPacketBuilder().
            commandGroup(CommandGroup.LOCO).
            command(CommandGroup.LOCO_ACTIVE).
            commandMode(CommandMode.REQUEST).data(buffer.flip()).
            build();
    // 2. max.500ms auf antwort warten
    LocoActivePacketAdapter locoActive = zcan.sendReceive(packet,
                                                          LocoActivePacketAdapter.SELECTOR::matches,
                                                          LocoActivePacketAdapter.class,
                                                          500);
    if ((locoActive != null && locoActive.getState() != LocoActive.UNKNOWN) && !force) {
      Packet errPacket = locoActive.getPacket();
      throw new ZCANError(errPacket.getSenderNID(),
                          errPacket.getCommandGroup(),
                          errPacket.getCommand(),
                          null,
                          "Loco is controlled remotely");
    }
    // 3. abfrage fahrzeug mode LOCO.0x01.COMMAND
    packet = zcan.createPacketBuilder().buildLocoModePacket(loco);
    LocoMode locoMode = zcan.sendReceive(packet,
                                         LocoModePacketAdapter.SELECTOR::matches,
                                         LocoModePacketAdapter.class,
                                         500);
    if (locoMode != null) {
      ZCAN.LOGGER.log(Level.FINE,
                      "Controlling Loco {0} with mode {1}",
                      new Object[]{loco, locoMode});
    }
  }

  @Override
  public boolean isOwner()
  {
    return !pingTask.isFinished();
  }

  @Override
  public void setOwner(boolean owner) throws IOException
  {
    if (owner != isOwner()) {
      if (owner) {
        takeOwnership(false);
      } else {
        pingTask.cancel();
      }
    }
  }

  @Override
  public void forcedTakeOwnership() throws IOException
  {
    takeOwnership(true);
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
    zcan.removePacketListener(funcFilter,
                              funcListener);
    zcan.removePacketListener(speedFilter,
                              speedListener);
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

  @Override
  public void control(Direction dir,
                      int speed) throws IOException
  {
    short s = (short) (Math.min(1023,
                                speed) & 0x3ff);
    Set<SpeedFlags> flags;
    switch (dir) {
      case FORWARD:
        flags = Set.of(SpeedFlags.FORWARD_TO_SYSTEM);
        break;
      case REVERSE:
        flags = Set.of(SpeedFlags.REVERSE_TO_SYSTEM);
        break;
      default:
        flags = Collections.emptySet();
    }
    Packet packet = zcan.createPacketBuilder().buildLocoSpeedPacket(loco,
                                                                    s,
                                                                    flags,
                                                                    (short) 1);
    zcan.doSendPacket(packet);
  }

  @Override
  public Integer getSpeed()
  {
    synchronized (getLock()) {
      return _speed;
    }
  }

  @Override
  public Direction getDirection()
  {
    synchronized (getLock()) {
      return _direction;
    }
  }

  private boolean isSpeedPacket(Packet packet)
  {
    if (packet.getCommandGroup() == CommandGroup.LOCO && packet.getCommand() == CommandGroup.LOCO_SPEED) {
      LocoSpeedPacketAdapter speed = packet.getAdapter(LocoSpeedPacketAdapter.class);
      return speed != null && speed.getLocoID() == loco;
    }
    return false;
  }

  private void onSpeedPacket(ZCAN sender,
                             Packet packet)
  {
    LocoSpeedPacketAdapter speed = packet.getAdapter(LocoSpeedPacketAdapter.class);
    if (speed != null) {
      Set<SpeedFlags> flags = speed.getFlags();
      int tmpSpeed = speed.getSpeed() & 0xffff;
      Direction tmpDir = null;
      if (flags.contains(SpeedFlags.FORWARD_FROM_SYSTEM)) {
        tmpDir = Direction.FORWARD;
      } else if (flags.contains(SpeedFlags.REVERSE_FROM_SYSTEM)) {
        tmpDir = Direction.REVERSE;
      }
      synchronized (getLock()) {
        _speed = tmpSpeed;
        _direction = tmpDir;
      }
    }
  }

  @Override
  public SortedMap<Integer, Integer> getAllFunctions()
  {
    TreeMap<Integer, Integer> result;
    synchronized (getLock()) {
      result = new TreeMap<>(_functions);
    }
    return result;
  }

  @Override
  public Integer getFunction(int iFunction)
  {
    if (iFunction >= 0 && iFunction < ZCAN.NUM_FUNCTION) {
      synchronized (getLock()) {
        return _functions.get(iFunction);
      }
    } else {
      throw new IndexOutOfBoundsException();
    }
  }

  @Override
  public void setFunction(int iFunction,
                          int iFuncValue) throws IOException
  {
    Packet packet = zcan.createPacketBuilder().buildLocoFunctionPacket(loco,
                                                                       (short) ((iFunction) & 0xffff),
                                                                       (short) iFuncValue);
    zcan.doSendPacket(packet);
  }

  private boolean isFuncPacket(Packet packet)
  {
    if (packet.getCommandGroup() == CommandGroup.LOCO && packet.getCommand() == CommandGroup.LOCO_FUNC_SWITCH) {
      LocoFuncPacketAdapter func = packet.getAdapter(LocoFuncPacketAdapter.class);
      return func != null && func.getLocoID() == loco;
    }
    return false;
  }

  private void onFuncPacket(ZCAN zcan,
                            Packet packet)
  {
    LocoFuncPacketAdapter func = packet.getAdapter(LocoFuncPacketAdapter.class);
    if (func != null) {
      int iFunction = (func.getFxNumber()) & 0xffff;
      int iVal = func.getFxValue() & 0xffff;
      synchronized (getLock()) {
        _functions.put(iFunction,
                       iVal);
      }
    }
  }

}
