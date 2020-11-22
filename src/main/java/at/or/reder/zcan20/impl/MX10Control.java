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

import at.or.reder.dcc.AccessoryEvent;
import at.or.reder.dcc.AccessoryEventListener;
import at.or.reder.dcc.Controller;
import at.or.reder.dcc.LinkState;
import at.or.reder.dcc.LinkStateListener;
import at.or.reder.dcc.Locomotive;
import at.or.reder.dcc.LocomotiveFuncEvent;
import at.or.reder.dcc.LocomotiveFuncEventListener;
import at.or.reder.dcc.LocomotiveSpeedEvent;
import at.or.reder.dcc.LocomotiveSpeedEventListener;
import at.or.reder.dcc.NotConnectedException;
import at.or.reder.dcc.PowerEvent;
import at.or.reder.dcc.PowerEventListener;
import at.or.reder.dcc.PowerMode;
import at.or.reder.dcc.PowerPort;
import at.or.reder.dcc.impl.LocomotiveImpl;
import at.or.reder.zcan20.CommandGroup;
import at.or.reder.zcan20.Loco;
import at.or.reder.zcan20.LocoControl;
import at.or.reder.zcan20.MX10PropertiesSet;
import at.or.reder.zcan20.PacketListener;
import at.or.reder.zcan20.SystemControl;
import at.or.reder.zcan20.ZAccessoryControl;
import at.or.reder.zcan20.ZCAN;
import at.or.reder.zcan20.ZimoPowerMode;
import at.or.reder.zcan20.packet.AccessoryPacketAdapter;
import at.or.reder.zcan20.packet.AccessoryPacketRequestAdapter;
import at.or.reder.zcan20.packet.LocoFuncPacketAdapter;
import at.or.reder.zcan20.packet.LocoSpeedPacketAdapter;
import at.or.reder.zcan20.packet.Packet;
import at.or.reder.zcan20.packet.PowerInfo;
import at.or.reder.zcan20.packet.PowerStateInfo;
import at.or.reder.zcan20.util.IndexedListenerSupport;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import org.openide.util.Lookup;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;

/**
 *
 * @author Wolfgang Reder
 */
final class MX10Control implements Controller
{

  private ZCANImpl device;
  private final Set<LinkStateListener> linkStateListener = new CopyOnWriteArraySet<>();
  private final LinkStateListener myLinkStateListener = this::onLinkStateChanged;
  private final Set<PowerEventListener> powerEventListener = new CopyOnWriteArraySet<>();
  private final Set<AccessoryEventListener> accessoryEventListener = new CopyOnWriteArraySet<>();
  private final IndexedListenerSupport<Integer, LocomotiveSpeedEventListener> locoSpeedEventListener
                                                                                      = new IndexedListenerSupport<>(0);
  private final IndexedListenerSupport<Integer, LocomotiveFuncEventListener> locoFuncEventListener
                                                                                     = new IndexedListenerSupport<>(0);
  private final PacketListener packetListener = this::onPacket;
  private final PropertyChangeSupport propSupport = new PropertyChangeSupport(this);
  private final InstanceContent ic = new InstanceContent();
  private final Lookup lookup = new AbstractLookup(ic);
  private final Object lock = new Object();
  private final Map<String, String> connectionProperties;
  private final MX10PropertiesSet propertySet = new MX10PropertiesSet();
  private int ioTimeout;

  public MX10Control(Map<String, String> connectionProperties) throws IllegalArgumentException
  {
    this.connectionProperties = new HashMap<>(connectionProperties);
    List<Map.Entry<String, String>> props = connectionProperties.entrySet().stream().
            filter((e) -> e.getKey().equals(MX10PropertiesSet.PROP_HOST) || e.getKey().equals(MX10PropertiesSet.PROP_PORT)).
            collect(Collectors.toList());
    boolean propsValid = !props.isEmpty();
    if (propsValid) {
      propsValid = propertySet.isKeyValueValid(connectionProperties,
                                               MX10PropertiesSet.PROP_HOST);
      if (!propsValid) {
        this.connectionProperties.remove(MX10PropertiesSet.PROP_HOST);
        propsValid = propertySet.isKeyValueValid(connectionProperties,
                                                 MX10PropertiesSet.PROP_PORT);
      } else {
        int inPort = propertySet.getIntValue(connectionProperties,
                                             MX10PropertiesSet.PROP_INPORT);
        int outPort = propertySet.getIntValue(connectionProperties,
                                              MX10PropertiesSet.PROP_OUTPORT);
        propsValid = inPort > 0 && outPort > 0 && inPort != outPort;
        if (!propsValid) {
          throw new IllegalArgumentException("Invalid ports for UDP connection");
        }
      }
    }
    if (!propsValid) {
      throw new IllegalArgumentException("Either " + MX10PropertiesSet.PROP_HOST + " or " + MX10PropertiesSet.PROP_PORT
                                                 + " must be set");
    }
  }

  @Override
  public String toString()
  {
    return "MX10Control{" + "device=" + device + '}';
  }

  Object getLock()
  {
    return lock;
  }

  @Override
  public LinkState getLinkState()
  {
    synchronized (getLock()) {
      return device != null ? device.getLinkState() : LinkState.CLOSED;
    }
  }

  ZCANImpl getDevice()
  {
    synchronized (getLock()) {
      return device;
    }
  }

  private ZPort openZPort() throws IOException
  {
    assert Thread.holdsLock(lock);
    String host = connectionProperties.get(MX10PropertiesSet.PROP_HOST);
    if (host != null) {
      int outPort = propertySet.getIntValue(connectionProperties,
                                            MX10PropertiesSet.PROP_OUTPORT);
      int inPort = propertySet.getIntValue(connectionProperties,
                                           MX10PropertiesSet.PROP_INPORT);
      InetAddress address = InetAddress.getByName(host);
      if (address.isMulticastAddress()) {
        return new McastPort(host,
                             outPort);
      } else {
        return new UDPPort(host,
                           outPort,
                           inPort);
      }
    } else {
      return new VCOMPort(connectionProperties.get(MX10PropertiesSet.PROP_PORT));
    }
  }

  @Override
  public void open() throws IOException
  {
    synchronized (lock) {
      if (device == null) {
        device = new ZCANImpl(openZPort(),
                              connectionProperties,
                              lock);
        ioTimeout = propertySet.getIntValue(connectionProperties,
                                            MX10PropertiesSet.PROP_IOTIMEOUT);
        ic.add(device);
        device.addLinkStateListener(myLinkStateListener);
        device.addPacketListener(packetListener);
        device.open(ioTimeout,
                    TimeUnit.SECONDS);
      }
    }
  }

  @Override
  public void close() throws IOException
  {
    synchronized (lock) {
      if (device != null) {
        try {
          device.close();
        } finally {
          device.removePacketListener(packetListener);
          device.removeLinkStateListener(myLinkStateListener);
          ic.remove(device);
          device = null;
        }
      }
    }
  }

  private void onPacket(ZCAN zcan,
                        Packet packet)
  {
    switch (packet.getCommandGroup().getMagic()) {
      case CommandGroup.CONFIG_MAGIC:
        dispatchConfig(packet);
        break;
      case CommandGroup.SYSTEM_MAGIC:
        dispatchConfig(packet);
        break;
      case CommandGroup.TRACK_CONFIG_PUBLIC_MAGIC:
      case 0x06:
        dispatchPOM(packet);
        break;
      case CommandGroup.ACCESSORY_MAGIC:
        dispatchAccessory(packet);
        break;
      case CommandGroup.LOCO_SPEED:
        dispatchLoco(packet);
        break;
      default:
    }
  }

  private void onLinkStateChanged(Controller controller,
                                  LinkState linkState)
  {
    if (!linkStateListener.isEmpty()) {
      for (LinkStateListener l : linkStateListener) {
        l.onLinkStateChanged(this,
                             linkState);
      }
    }
  }

  @Override
  public void addLinkStateListener(LinkStateListener listener)
  {
    if (listener != null) {
      linkStateListener.add(listener);
    }
  }

  @Override
  public void removeLinkStateListener(LinkStateListener listern)
  {
    linkStateListener.remove(listern);
  }

  @Override
  public void addPropertyChangeListener(PropertyChangeListener listener)
  {
    propSupport.addPropertyChangeListener(listener);
  }

  @Override
  public void addPropertyChangeListener(String propertyName,
                                        PropertyChangeListener listener)
  {
    propSupport.addPropertyChangeListener(propertyName,
                                          listener);
  }

  @Override
  public void removePropertyChangeListener(PropertyChangeListener listener)
  {
    propSupport.removePropertyChangeListener(listener);
  }

  @Override
  public void removePropertyChangeListener(String propertyName,
                                           PropertyChangeListener listener)
  {
    propSupport.removePropertyChangeListener(propertyName,
                                             listener);
  }

  @Override
  public Lookup getLookup()
  {
    return lookup;
  }

  private boolean dispatchPOM(Packet packet)
  {
//    System.out.println(Utils.packetToString(packet));
//    CVInfoAdapter cvInfo = packet.getAdapter(CVInfoAdapter.class);
//    if (cvInfo != null) {
//      CVEvent event = new CVEventImpl(this,
//                                      packet.getSenderNID(),
//                                      DecoderType.LOCO,
//                                      cvInfo.getDecoderID(),
//                                      cvInfo.getNumber(),
//                                      cvInfo.getValue(),
//                                      Collections.singleton(cvInfo));
//      for (CVEventListener l : cvEventListener) {
//        l.onCVEvent(event);
//      }
//      return true;
//    }
//    TSETrackModePacketAdapter mode = packet.getAdapter(TSETrackModePacketAdapter.class);
//    if (mode != null) {
//      System.out.println(mode);
//    }
//
    return false;
  }

  @Override
  public PowerMode getPowerMode(PowerPort port) throws IOException, TimeoutException
  {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public void postPowerModeRequest(PowerPort port) throws IOException
  {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public void setPowerMode(PowerPort port,
                           PowerMode mode) throws IOException
  {
    ZCANImpl d = getDevice();
    SystemControl sysControl = d.getLookup().lookup(SystemControl.class);
    if (sysControl != null) {
      sysControl.setPowerModeInfo(port,
                                  ZimoPowerMode.valueOf(mode));
    }
  }

  @Override
  public void addPowerEventListener(PowerEventListener listener)
  {
    if (listener != null) {
      powerEventListener.add(listener);
    }
  }

  @Override
  public void removePowerEventListener(PowerEventListener listener)
  {
    powerEventListener.remove(listener);
  }

  private final ConcurrentMap<PowerPort, PowerStateInfo> lastPowerState = new ConcurrentHashMap<>();
  private final AtomicReference<PowerInfo> powerInfo = new AtomicReference<>();

  private boolean dispatchConfig(Packet packet)
  {
    List<PowerEvent> events = new ArrayList<>();
    PowerInfo power = packet.getAdapter(PowerInfo.class);
    if (power != null) {
      powerInfo.set(power);
      PowerStateInfo state1 = lastPowerState.get(PowerPort.OUT_1);
      PowerStateInfo state2 = lastPowerState.get(PowerPort.OUT_2);
      PowerMode mode = state1 != null ? state1.getMode().getSysteMode() : PowerMode.PENDING;
      events.add(new MX10PowerEvent(PowerPort.OUT_1,
                                    mode,
                                    this,
                                    packet.getSenderNID() & 0xffff,
                                    power,
                                    state1));
      mode = state2 != null ? state2.getMode().getSysteMode() : PowerMode.PENDING;
      events.add(new MX10PowerEvent(PowerPort.OUT_2,
                                    mode,
                                    this,
                                    packet.getSenderNID() & 0xffff,
                                    power,
                                    state2));
    } else {
      PowerStateInfo powerState = packet.getAdapter(PowerStateInfo.class);
      if (powerState != null) {
        lastPowerState.put(powerState.getOutput(),
                           powerState);
        PowerInfo info = powerInfo.get();
        PowerMode mode = powerState.getMode().getSysteMode();
        events.add(new MX10PowerEvent(powerState.getOutput(),
                                      mode,
                                      this,
                                      packet.getSenderNID() & 0xffff,
                                      info,
                                      powerState));
      }
    }
    for (PowerEvent evt : events) {
      for (PowerEventListener l : powerEventListener) {
        l.onPowerEvent(evt);
      }
    }
    return !events.isEmpty();
  }

  @Override
  public Locomotive getLocomotive(int locoAddress) throws IOException, TimeoutException
  {
    LocoControl ctrl = getDevice().getLookup().lookup(LocoControl.class);
    Loco loco = ctrl.getLoco((short) locoAddress,
                             false);
    Locomotive result = new LocomotiveImpl(loco,
                                           this);
    return result;
  }

  @Override
  public Future<Byte> getAccessoryState(short decoder,
                                        byte port) throws IOException
  {
    if (getLinkState() == LinkState.CONNECTED) {
      ZCANImpl zcan = getDevice();
      ZAccessoryControl zac = zcan.getLookup().lookup(ZAccessoryControl.class);
      return zac.getAccessoryState(decoder,
                                   port);
    } else {
      throw new NotConnectedException();
    }
  }

  @Override
  public void setAccessoryState(short decoder,
                                byte port,
                                byte state) throws IOException
  {
    if (getLinkState() == LinkState.CONNECTED) {
      ZCANImpl zcan = getDevice();
      ZAccessoryControl zac = zcan.getLookup().lookup(ZAccessoryControl.class);
      zac.setAccessoryState(decoder,
                            port,
                            state);
    } else {
      throw new NotConnectedException();
    }
  }

  @Override
  public Future<Byte> setAccessoryStateChecked(short decoder,
                                               byte port,
                                               byte state) throws IOException
  {
    if (getLinkState() == LinkState.CONNECTED) {
      ZCANImpl zcan = getDevice();
      ZAccessoryControl zac = zcan.getLookup().lookup(ZAccessoryControl.class);
      return zac.setAccessoryStateChecked(decoder,
                                          port,
                                          state);
    } else {
      throw new NotConnectedException();
    }
  }

  private void dispatchAccessory(Packet packet)
  {
    if (!accessoryEventListener.isEmpty()) {
      AccessoryEvent evt = null;
      AccessoryPacketAdapter apa = packet.getAdapter(AccessoryPacketAdapter.class);
      if (apa != null) {
        evt = new MX10AccessoryEvent(this,
                                     apa);
      } else {
        AccessoryPacketRequestAdapter apr = packet.getAdapter(AccessoryPacketRequestAdapter.class);
        if (apr != null) {
          evt = new MX10AccessoryEvent(this,
                                       apr);
        }
      }
      if (evt != null) {
        for (AccessoryEventListener l : accessoryEventListener) {
          l.onAccessoryEvent(evt);
        }
      }
    }
  }

  @Override
  public void addAccessoryEventListener(AccessoryEventListener l)
  {
    if (l != null) {
      accessoryEventListener.add(l);
    }
  }

  @Override
  public void removeAccessoryEventListener(AccessoryEventListener l)
  {
    accessoryEventListener.remove(l);
  }

  @Override
  public void addLocomotiveSpeedEventListener(int address,
                                              LocomotiveSpeedEventListener listener)
  {
    locoSpeedEventListener.addEventListener(address,
                                            listener);
  }

  @Override
  public void removeLocomotiveSpeedEventListener(int address,
                                                 LocomotiveSpeedEventListener listener)
  {
    locoSpeedEventListener.removeEventListener(address,
                                               listener);
  }

  private void dispatchLoco(Packet packet)
  {
    switch (packet.getCommand()) {
      case CommandGroup.LOCO_SPEED:
        dispatchLocoSpeed(packet);
        break;
      case CommandGroup.LOCO_FUNC_SWITCH:
        dispatchLocoFunc(packet);
        break;
    }
  }

  private void dispatchLocoSpeed(Packet packet)
  {
    LocoSpeedPacketAdapter apa = packet.getAdapter(LocoSpeedPacketAdapter.class);
    if (apa != null) {
      Set<LocomotiveSpeedEventListener> listener = locoSpeedEventListener.getListener(apa.getDecoderId() & 0xffff);
      if (!listener.isEmpty()) {
        LocomotiveSpeedEvent evt = new MX10LocoSpeedEventImpl(this,
                                                              null,
                                                              packet.getSenderNID() & 0xff,
                                                              apa);
        for (LocomotiveSpeedEventListener l : listener) {
          l.onLocomotiveEvent(evt);
        }
      }
    }
  }

  @Override
  public void addLocomotiveFuncEventListener(int address,
                                             LocomotiveFuncEventListener listener)
  {
    locoFuncEventListener.addEventListener(address,
                                           listener);
  }

  @Override
  public void removeLocomotiveFuncEventListener(int address,
                                                LocomotiveFuncEventListener listener)
  {
    locoFuncEventListener.removeEventListener(address,
                                              listener);
  }

  private void dispatchLocoFunc(Packet packet)
  {
    LocoFuncPacketAdapter apa = packet.getAdapter(LocoFuncPacketAdapter.class);
    if (apa != null) {
      Set<LocomotiveFuncEventListener> listener = locoFuncEventListener.getListener(apa.getDecoderId() & 0xffff);
      if (!listener.isEmpty()) {
        LocomotiveFuncEvent evt = new MX10LocoFuncEventImpl(this,
                                                            null,
                                                            packet.getSenderNID() & 0xff,
                                                            apa);
        for (LocomotiveFuncEventListener l : listener) {
          l.onLocomotiveEvent(evt);
        }
      }
    }
  }

}
