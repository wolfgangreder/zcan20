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

import at.or.reder.dcc.LinkState;
import at.or.reder.dcc.util.Utils;
import at.or.reder.mx1.MX1;
import at.or.reder.mx1.MX1Command;
import at.or.reder.mx1.MX1Packet;
import at.or.reder.mx1.MX1PacketFlags;
import at.or.reder.mx1.MX1PacketListener;
import at.or.reder.mx1.MX1PacketObject;
import at.or.reder.mx1.MX1Port;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

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

  public MX1Impl(String portName,
                 Map<String, String> settings)
  {
    port = new MX1PortImpl(portName);
    port.setPacketListener(this::onPacketData);
  }

  @Override
  public boolean open() throws IOException
  {
    if (linkState != LinkState.CONNECTED) {
      port.open();
    }
    reset();
    return linkState == LinkState.CONNECTED;
  }

  @Override
  public LinkState getLinkState()
  {
    return linkState;
  }

  private void setLinkState(LinkState ls)
  {
    linkState = ls;
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
    if (linkState != LinkState.CONNECTED) {
      throw new IllegalStateException("port not connected");
    }
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

  @Override
  @SuppressWarnings("ConvertToTryWithResources")
  public void close() throws IOException
  {
    try {
      LOGGER.log(Level.FINE,
                 "Closing conection");
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
      }
      if (packet.getFlags().contains(MX1PacketFlags.REPLY)) {
        try {
          sendACK(packet.getCommand(),
                  packet.getSequence());
        } catch (IOException ex) {
          LOGGER.log(Level.SEVERE,
                     "Sending ACK",
                     ex);
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
    ByteBuffer payLoad = Utils.allocateBEBuffer(1);
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
    ByteBuffer payLoad = Utils.allocateBEBuffer(4);
    payLoad.putShort(Utils.short1((address & 0x3fff) | 0x8000)); // force DCC!
    payLoad.putShort(Utils.short1(iCV));
    payLoad.rewind();
    MX1Packet packet = new PacketImpl((byte) (sequence++),
                                      EnumSet.of(MX1PacketFlags.FROM_PC,
                                                 MX1PacketFlags.SHORT_FRAME,
                                                 MX1PacketFlags.PRIMARY,
                                                 MX1PacketFlags.TO_COMMANDSTATION),
                                      MX1Command.RW_DECODER_CV,
                                      payLoad);
    LOGGER.log(Level.FINE,
               "Sending reset");
    port.sendPacket(packet);
  }

  @Override
  public void addMX1PacketListener(MX1PacketListener l)
  {
    if (l != null) {
      packetListener.add(l);
    }
  }

  @Override
  public void removeMX1PacketListener(MX1PacketListener l)
  {
    if (l != null) {
      packetListener.remove(l);
    }
  }

}
