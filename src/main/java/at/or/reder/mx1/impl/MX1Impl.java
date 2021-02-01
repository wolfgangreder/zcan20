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
package at.or.reder.mx1.impl;

import at.or.reder.dcc.Direction;
import at.or.reder.dcc.IdentifyProvider;
import at.or.reder.dcc.LinkState;
import at.or.reder.dcc.PowerMode;
import at.or.reder.dcc.SpeedstepSystem;
import at.or.reder.dcc.util.DCCUtils;
import at.or.reder.mx1.CVPacketAdapter;
import at.or.reder.mx1.CommandStationInfo;
import at.or.reder.mx1.LocoInfo;
import at.or.reder.mx1.MX1;
import at.or.reder.mx1.MX1Command;
import at.or.reder.mx1.MX1Packet;
import at.or.reder.mx1.MX1PacketFlags;
import at.or.reder.mx1.MX1PacketListener;
import at.or.reder.mx1.MX1PacketObject;
import at.or.reder.mx1.MX1Port;
import at.or.reder.mx1.PowerModePacketAdapter;
import at.or.reder.mx1.SerialInfoAction;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.BitSet;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.openide.util.Lookup;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author Wolfgang Reder
 */
public class MX1Impl implements MX1
{

  public static final Logger LOGGER = Logger.getLogger("at.or.reder.mx1");

  private final MX1Port port;
  private int sequence;
  private volatile LinkState linkState = LinkState.CLOSED;
  private final Set<ChangeListener> linkStateListener = new CopyOnWriteArraySet<>();
  private final Set<MX1PacketListener> packetListener = new CopyOnWriteArraySet<>();
  private final Map<Integer, LocoInfoRecord> locoInfo = new HashMap<>();
  private final Lookup lookup;

  public MX1Impl(String portName,
                 Map<String, String> settings)
  {
    port = new MX1PortImpl(portName);
    port.setPacketListener(this::onPacketData);
    lookup = Lookups.fixed(port,
                           new IdentifyProvider()
                   {
                     private boolean pomMode = true;

                     @Override
                     public void enterPOMMode()
                     {
                       pomMode = true;
                     }

                     @Override
                     public void enterServiceMode()
                     {
                       pomMode = false;
                     }

                     @Override
                     public int readCV(int iAddress,
                                       int iCV) throws IOException
                     {
                       long timeout = (!pomMode || iAddress == 0) ? 10 : 2;
                       return MX1Impl.this.readCV(pomMode ? iAddress : 0,
                                                  iCV,
                                                  timeout,
                                                  TimeUnit.SECONDS);
                     }

                   });
  }

  @Override
  public Lookup getLookup()
  {
    return lookup;
  }

  @Override
  public boolean open() throws IOException
  {
    if (linkState != LinkState.CONNECTED) {
      port.open();
    }
    reset();
    sendSerialInfo(SerialInfoAction.COMMUNICATION_START);
    return linkState == LinkState.CONNECTED;
  }

  @Override
  public synchronized LinkState getLinkState()
  {
    return linkState;
  }

  private void setLinkState(LinkState ls)
  {
    synchronized (this) {
      LOGGER.log(Level.FINEST,
                 "Setting linkState to " + ls.name());
      linkState = ls;
    }
    ChangeEvent evt = new ChangeEvent(this);
    for (ChangeListener lsl : linkStateListener) {
      lsl.stateChanged(evt);
    }
  }

  @Override
  public void addChangeListener(ChangeListener lsl)
  {
    if (lsl != null) {
      linkStateListener.add(lsl);
    }
  }

  @Override
  public void removeChangeListener(ChangeListener lsl)
  {
    linkStateListener.remove(lsl);
  }

  private void checkConnected()
  {
//    if (getLinkState() != LinkState.CONNECTED) {
//      throw new IllegalStateException("port not connected");
//    }
  }

  @Override
  public void reset() throws IOException
  {
    if (port == null) {
      throw new IllegalStateException("port not opened");
    }
    sequence = 0;
    MX1Packet packet = new PacketImpl((byte) (sequence++),
                                      EnumSet.of(MX1PacketFlags.FROM_PC,
                                                 MX1PacketFlags.SHORT_FRAME,
                                                 MX1PacketFlags.PRIMARY,
                                                 MX1PacketFlags.TO_COMMANDSTATION),
                                      MX1Command.RESET,
                                      null);
    LOGGER.log(Level.FINE,
               "Sending reset");
    port.sendPacket(packet);
  }

  private void sendSerialInfo(SerialInfoAction action) throws IOException
  {
    ByteBuffer payload = DCCUtils.allocateBEBuffer(2);
    payload.put((byte) 1);
    payload.put(action.getCode());
    payload.rewind();
    MX1Packet packet = new PacketImpl((byte) (sequence++),
                                      EnumSet.of(MX1PacketFlags.FROM_PC,
                                                 MX1PacketFlags.SHORT_FRAME,
                                                 MX1PacketFlags.PRIMARY,
                                                 MX1PacketFlags.TO_COMMANDSTATION),
                                      MX1Command.SERIAL_INFO,
                                      payload);
    LOGGER.log(Level.FINE,
               "Sending reset");
    port.sendPacket(packet);
  }

  @Override
  @SuppressWarnings("ConvertToTryWithResources")
  public void close() throws IOException
  {
    try {
      LOGGER.log(Level.FINE,
                 "Closing conection");
      sendSerialInfo(SerialInfoAction.COMMUNICATION_END);
      port.close();
    } finally {
      setLinkState(LinkState.CLOSED);
    }
  }

  private void onPacketData(MX1Packet packet)
  {
    if (packet == null) {
      try {
        close();
      } catch (IOException ex) {
        LOGGER.log(Level.SEVERE,
                   "Closing connection due to commonucation error",
                   ex);
      }
    } else {
      switch (packet.getCommand()) {
        case RESET:
          setLinkState(LinkState.CONNECTED);
          if (packet.getFlags().contains(MX1PacketFlags.PRIMARY)) {

          }
          break;
      }
      if (packet.getFlags().contains(MX1PacketFlags.REPLY)) {
        try {
          LOGGER.log(Level.FINEST,
                     "Enter sendack for packet " + packet.toString());
          sendACK(packet.getCommand(),
                  packet.getSequence());
        } catch (IOException ex) {
          LOGGER.log(Level.SEVERE,
                     "Sending ACK",
                     ex);
        }
      }
      LocoInfoPacketAdapter li = packet.getAdapter(LocoInfoPacketAdapter.class);
      if (li != null) {
        LocoInfoRecord lir = this.locoInfo.computeIfAbsent(li.getAddress(),
                                                           LocoInfoRecord::new);
        synchronized (lir) {
          lir.update(li);
        }
      }
      if (!packetListener.isEmpty()) {
        MX1PacketObject evt = new MX1PacketObject(this,
                                                  packet);
        for (MX1PacketListener pl : packetListener) {
          pl.onMX1Packet(evt);
        }
      }
    }
  }

  private void sendACK(MX1Command command,
                       int sequenceReply) throws IOException
  {
    checkConnected();
    ByteBuffer payLoad = DCCUtils.allocateBEBuffer(1);
    payLoad.put((byte) sequenceReply);
    payLoad.rewind();
    MX1Packet packet = new PacketImpl((byte) (sequence++),
                                      EnumSet.of(MX1PacketFlags.FROM_PC,
                                                 MX1PacketFlags.SHORT_FRAME,
                                                 MX1PacketFlags.ACK_1,
                                                 MX1PacketFlags.TO_COMMANDSTATION),
                                      command,
                                      payLoad);
    port.sendPacket(packet);
  }

  @Override
  public void readCV(int address,
                     int iCV) throws IOException
  {
    checkConnected();
    ByteBuffer payLoad = DCCUtils.allocateBEBuffer(4);
    payLoad.putShort(DCCUtils.short1((address & 0x3fff) | 0x8000)); // force DCC!
    payLoad.putShort(DCCUtils.short1(iCV));
    payLoad.rewind();
    MX1Packet packet = new PacketImpl((byte) (sequence++),
                                      EnumSet.of(MX1PacketFlags.FROM_PC,
                                                 MX1PacketFlags.SHORT_FRAME,
                                                 MX1PacketFlags.PRIMARY,
                                                 MX1PacketFlags.TO_COMMANDSTATION),
                                      MX1Command.RW_DECODER_CV,
                                      payLoad);
    String p = packet.toString();
    LOGGER.log(Level.FINE,
               "Sending readCV #{0,number,0} to address {1,number,0}:{2}",
               new Object[]{iCV, address, p});
    port.sendPacket(packet);
  }

  @Override
  public int readCV(int address,
                    int iCV,
                    long timeout,
                    TimeUnit unit) throws IOException
  {
    try (PacketListenerFuture<Integer> future = PacketListenerFuture.createFuture(this,
                                                                                  CVPacketAdapter.class,
                                                                                  CVPacketAdapter::getValue)) {
      readCV(address,
             iCV);
      Integer result = future.get(timeout,
                                  unit);
      if (result != null && future.isCompleted()) {
        return result;
      }
    } catch (TimeoutException ex) {
    } catch (InterruptedException | ExecutionException ex) {
      LOGGER.log(Level.SEVERE,
                 "Waiting for response",
                 ex);
    }
    return -1;
  }

  @Override
  public void writeCV(int address,
                      int iCV,
                      int value) throws IOException
  {
    checkConnected();
    ByteBuffer payLoad = DCCUtils.allocateBEBuffer(5);
    payLoad.putShort(DCCUtils.short1((address & 0x3fff) | 0x8000)); // force DCC!
    payLoad.putShort(DCCUtils.short1(iCV));
    payLoad.put((byte) value);
    payLoad.rewind();
    MX1Packet packet = new PacketImpl((byte) (sequence++),
                                      EnumSet.of(MX1PacketFlags.FROM_PC,
                                                 MX1PacketFlags.SHORT_FRAME,
                                                 MX1PacketFlags.PRIMARY,
                                                 MX1PacketFlags.TO_COMMANDSTATION),
                                      MX1Command.RW_DECODER_CV,
                                      payLoad);
    String p = packet.toString();
    LOGGER.log(Level.FINE,
               "Sending writeCV #{0,number,0}={3} to address {1,number,0}:{2}",
               new Object[]{iCV, address, p, value});
    port.sendPacket(packet);
  }

  @Override
  public boolean writeCV(int address,
                         int iCV,
                         int value,
                         long timeout,
                         TimeUnit unit) throws IOException
  {
    try (PacketListenerFuture<MX1Packet> future = PacketListenerFuture.createFuture(this,
                                                                                    (p) -> p.getFlags().contains(
                                                                                            MX1PacketFlags.ACK_1))) {
      writeCV(address,
              iCV,
              value);
      MX1Packet result = future.get(timeout,
                                    unit);
      return (result != null && future.isCompleted());
    } catch (TimeoutException ex) {
    } catch (InterruptedException | ExecutionException ex) {
      LOGGER.log(Level.SEVERE,
                 "Waiting for response",
                 ex);
    }
    return false;
  }

  @Override
  public void getPowerMode() throws IOException
  {
    checkConnected();
    ByteBuffer payLoad = DCCUtils.allocateBEBuffer(1);
    payLoad.put((byte) 3); // query status
    payLoad.rewind();
    MX1Packet packet = new PacketImpl((byte) (sequence++),
                                      EnumSet.of(MX1PacketFlags.FROM_PC,
                                                 MX1PacketFlags.SHORT_FRAME,
                                                 MX1PacketFlags.PRIMARY,
                                                 MX1PacketFlags.TO_COMMANDSTATION),
                                      MX1Command.TRACK_CONTROL,
                                      payLoad);
    LOGGER.log(Level.FINE,
               "Sending getPowerMode");
    port.sendPacket(packet);
  }

  @Override
  public PowerMode getPowerMode(long timeout,
                                TimeUnit unit) throws IOException
  {
    try (PacketListenerFuture<PowerMode> future = PacketListenerFuture.createFuture(this,
                                                                                    PowerModePacketAdapter.class,
                                                                                    PowerModePacketAdapter::getPowerMode)) {
      getPowerMode();
      PowerMode result = future.get(timeout,
                                    unit);
      if (result != null && future.isCompleted()) {
        return result;
      }
    } catch (TimeoutException ex) {
    } catch (InterruptedException | ExecutionException ex) {
      LOGGER.log(Level.SEVERE,
                 "Waiting for response",
                 ex);
    }
    return PowerMode.PENDING;
  }

  @Override
  public void setPowerMode(PowerMode newMode) throws IOException
  {
    byte mode;
    switch (newMode) {
      case ON:
        mode = 2;
        break;
      case OFF:
        mode = 1;
        break;
      case SSPEM:
      case SSPF0:
        mode = 0;
        break;
      default:
        return;
    }
    checkConnected();
    ByteBuffer payLoad = DCCUtils.allocateBEBuffer(1);
    payLoad.put(mode); // query status
    payLoad.rewind();
    MX1Packet packet = new PacketImpl((byte) (sequence++),
                                      EnumSet.of(MX1PacketFlags.FROM_PC,
                                                 MX1PacketFlags.SHORT_FRAME,
                                                 MX1PacketFlags.PRIMARY,
                                                 MX1PacketFlags.TO_COMMANDSTATION),
                                      MX1Command.TRACK_CONTROL,
                                      payLoad);
    LOGGER.log(Level.FINE,
               "Sending setPowerMode");
    port.sendPacket(packet);
  }

  @Override
  public void addMX1PacketListener(MX1PacketListener l
  )
  {
    if (l != null) {
      packetListener.add(l);
    }
  }

  @Override
  public void removeMX1PacketListener(MX1PacketListener l
  )
  {
    if (l != null) {
      packetListener.remove(l);
    }
  }

  @Override
  public void getCommandStationInfo() throws IOException
  {
    checkConnected();
    ByteBuffer payLoad = DCCUtils.allocateBEBuffer(1);
    payLoad.put((byte) 0);
    payLoad.rewind();
    MX1Packet packet = new PacketImpl((byte) (sequence++),
                                      EnumSet.of(MX1PacketFlags.FROM_PC,
                                                 MX1PacketFlags.SHORT_FRAME,
                                                 MX1PacketFlags.PRIMARY,
                                                 MX1PacketFlags.TO_COMMANDSTATION),
                                      MX1Command.CS_EQ_QUERY,
                                      payLoad);
    LOGGER.log(Level.FINE,
               "Sending getCommandStationInfo");
    port.sendPacket(packet);
  }

  @Override
  public CommandStationInfo getCommandStationInfo(long timeout,
                                                  TimeUnit unit) throws IOException
  {
    try (PacketListenerFuture<CommandStationInfo> future = PacketListenerFuture.createFuture(this,
                                                                                             CommandStationInfo.class)) {
      getCommandStationInfo();
      CommandStationInfo result = future.get(timeout,
                                             unit);
      if (result != null && future.isCompleted()) {
        return result;
      }
    } catch (TimeoutException ex) {
    } catch (InterruptedException | ExecutionException ex) {
      LOGGER.log(Level.SEVERE,
                 "Waiting for response",
                 ex);
    }
    return null;
  }

  @Override
  public void setFunction(int address,
                          int iFunction,
                          int val) throws IOException
  {
    LocoInfoRecord loco = this.locoInfo.computeIfAbsent(address,
                                                        LocoInfoRecord::new);
    int flags = 0;
    BitSet functions = new BitSet(13);
    synchronized (loco) {
      if (loco.getLastRead() == null) {
        LocoInfo li = getLocoInfo(address,
                                  2,
                                  TimeUnit.SECONDS);
        if (li != null) {
          loco.update(li);
          flags = loco.getFlags();
          functions = loco.getFunctions();
        }
      }
      if (iFunction == 0) {
        if (val != 0) {
          flags |= 0x10;
        } else {
          flags &= ~0x10;
        }
      } else {
        functions.set(iFunction,
                      val != 0);
      }
      ByteBuffer payLoad = DCCUtils.allocateBEBuffer(5);
      payLoad.putShort((short) address);
      payLoad.put((byte) flags);
      payLoad.putShort((short) LocoInfoPacketAdapter.getF112(functions));
      payLoad.rewind();
      MX1Packet packet = new PacketImpl((byte) (sequence++),
                                        EnumSet.of(MX1PacketFlags.FROM_PC,
                                                   MX1PacketFlags.SHORT_FRAME,
                                                   MX1PacketFlags.PRIMARY,
                                                   MX1PacketFlags.TO_COMMANDSTATION),
                                        MX1Command.INVERT_FUNCTION,
                                        payLoad);
      LOGGER.log(Level.FINE,
                 "Sending getInvertFunctionBits");
      port.sendPacket(packet);

    }
  }

  @Override
  public int getFunction(int address,
                         int iFunction) throws IOException
  {
    LocoInfoRecord loco = this.locoInfo.computeIfAbsent(address,
                                                        LocoInfoRecord::new);
    synchronized (loco) {
      if (loco.getLastRead() != null) {
        return loco.isFunctionSet(iFunction) ? 1 : 0;
      }
    }
    return -1;
  }

  @Override
  public int getFunction(int address,
                         int iFunction,
                         long timeout,
                         TimeUnit unit) throws IOException
  {
    getLocoInfo(address,
                timeout,
                unit);
    return getFunction(address,
                       iFunction);
  }

  @Override
  public void setSpeed(int address,
                       int speed,
                       SpeedstepSystem speedSystem) throws IOException
  {
    ByteBuffer payload = DCCUtils.allocateBEBuffer(3);
    payload.putShort((short) address);
    payload.put((byte) speedSystem.normalizedToSystem(speed));
    payload.rewind();
    MX1Packet packet = new PacketImpl((byte) (sequence++),
                                      EnumSet.of(MX1PacketFlags.FROM_PC,
                                                 MX1PacketFlags.SHORT_FRAME,
                                                 MX1PacketFlags.PRIMARY,
                                                 MX1PacketFlags.TO_COMMANDSTATION),
                                      MX1Command.LOCO_CONTROL,
                                      payload);
    LOGGER.log(Level.FINE,
               "Sending setSpeed");
    port.sendPacket(packet);

  }

  @Override
  public void emergencyStop(int address) throws IOException
  {
    ByteBuffer payload = DCCUtils.allocateBEBuffer(3);
    payload.putShort((short) address);
    payload.put((byte) 0x80);
    payload.rewind();
    MX1Packet packet = new PacketImpl((byte) (sequence++),
                                      EnumSet.of(MX1PacketFlags.FROM_PC,
                                                 MX1PacketFlags.SHORT_FRAME,
                                                 MX1PacketFlags.PRIMARY,
                                                 MX1PacketFlags.TO_COMMANDSTATION),
                                      MX1Command.LOCO_CONTROL,
                                      payload);
    LOGGER.log(Level.FINE,
               "Sending emergencyStop");
    port.sendPacket(packet);
  }

  @Override
  public void locoControl(int address,
                          int speed,
                          SpeedstepSystem speedSytem,
                          Direction direction,
                          boolean man,
                          BitSet functions) throws IOException
  {
    ByteBuffer payload = DCCUtils.allocateBEBuffer(6);
    payload.putShort((short) address);
    int s = speedSytem.normalizedToSystem(speed);
    payload.put((byte) s);
    int flags = 0;
    if (man) {
      flags |= 0x80;
    }
    if (functions.get(0)) {
      flags |= 0x10;
    }
    if (direction == Direction.REVERSE) {
      flags |= 0x20;
    }
    flags |= speedSytem.getMagic();
    payload.put((byte) flags);
    payload.putShort((short) LocoInfoPacketAdapter.getF112(functions));
    payload.rewind();
    MX1Packet packet = new PacketImpl((byte) (sequence++),
                                      EnumSet.of(MX1PacketFlags.FROM_PC,
                                                 MX1PacketFlags.SHORT_FRAME,
                                                 MX1PacketFlags.PRIMARY,
                                                 MX1PacketFlags.TO_COMMANDSTATION),
                                      MX1Command.LOCO_CONTROL,
                                      payload);
    LOGGER.log(Level.FINE,
               "Sending locoControl");
    port.sendPacket(packet);
  }

  @Override
  public void getLocoInfo(int address) throws IOException
  {
    ByteBuffer payload = DCCUtils.allocateBEBuffer(2);
    payload.putShort((short) address);
    payload.rewind();
    MX1Packet packet = new PacketImpl((byte) (sequence++),
                                      EnumSet.of(MX1PacketFlags.FROM_PC,
                                                 MX1PacketFlags.SHORT_FRAME,
                                                 MX1PacketFlags.PRIMARY,
                                                 MX1PacketFlags.TO_COMMANDSTATION),
                                      MX1Command.QUERY_CS_LOCO,
                                      payload);
    LOGGER.log(Level.FINE,
               "Sending getLocoInfo");
    port.sendPacket(packet);
  }

  @Override
  public LocoInfo getLocoInfo(int address,
                              long timeout,
                              TimeUnit unit) throws IOException
  {
    try (PacketListenerFuture<LocoInfoPacketAdapter> future = new PacketListenerFuture<>(this,
                                                                                         (p) -> {
                                                                                           LocoInfoPacketAdapter pa = p.
                                                                                                   getAdapter(
                                                                                                           LocoInfoPacketAdapter.class);
                                                                                           return pa != null && pa.
                                                                                                   getAddress() == address;
                                                                                         },
                                                                                         (p) -> p.getAdapter(
                                                                                                 LocoInfoPacketAdapter.class))) {
      getLocoInfo(address);
      LocoInfo result = future.get(timeout,
                                   unit);
      LocoInfoRecord rec = this.locoInfo.computeIfAbsent(address,
                                                         LocoInfoRecord::new);
      if (result != null && future.isCompleted()) {
        synchronized (rec) {
          rec.update(result);
        }
        return result;
      }
      return rec.toLocoInfo();
    } catch (TimeoutException ex) {
    } catch (InterruptedException | ExecutionException ex) {
      LOGGER.log(Level.SEVERE,
                 "Waiting for response",
                 ex);
    }
    return null;
  }

}
