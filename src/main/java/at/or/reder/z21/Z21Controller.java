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
package at.or.reder.z21;

import at.or.reder.dcc.AccessoryEventListener;
import at.or.reder.dcc.Controller;
import at.or.reder.dcc.LinkState;
import at.or.reder.dcc.LinkStateListener;
import at.or.reder.dcc.Locomotive;
import at.or.reder.dcc.PowerEventListener;
import at.or.reder.dcc.PowerMode;
import at.or.reder.dcc.PowerPort;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.Future;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;
import org.openide.util.Lookup;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;

final class Z21Controller implements Controller
{

  private final Set<LinkStateListener> linkStateListener = new CopyOnWriteArraySet<>();
  private final LinkStateListener myLinkStateListener = this::onLinkStateChanged;
  private final Set<PowerEventListener> powerEventListener = new CopyOnWriteArraySet<>();
  private final Set<AccessoryEventListener> accessoryEventListener = new CopyOnWriteArraySet<>();
  private final PropertyChangeSupport propSupport = new PropertyChangeSupport(this);
  private final InstanceContent ic = new InstanceContent();
  private final Lookup lookup = new AbstractLookup(ic);
  private final Object lock = new Object();
  private final Map<String, String> connectionProperties;
  private final Z21PropertySet propertySet = new Z21PropertySet();
  private int ioTimeout;

  Z21Controller(Map<String, String> connectionProperties) throws IllegalArgumentException
  {
    this.connectionProperties = new HashMap<>(connectionProperties);
    List<Map.Entry<String, String>> props = connectionProperties.entrySet().stream().
            filter((e) -> e.getKey().equals(Z21PropertySet.PROP_HOST)).
            collect(Collectors.toList());
    boolean propsValid = !props.isEmpty();
    if (propsValid) {
      propsValid = propertySet.isKeyValueValid(connectionProperties,
                                               Z21PropertySet.PROP_HOST);
      if (propsValid) {
        int port = propertySet.getIntValue(connectionProperties,
                                           Z21PropertySet.PROP_PORT);
        propsValid = port > 0;
        if (!propsValid) {
          throw new IllegalArgumentException("Invalid ports for UDP connection");
        }
      }
    }
    if (!propsValid) {
      throw new IllegalArgumentException(Z21PropertySet.PROP_HOST + " must be set");
    }
  }

  Object getLock()
  {
    return lock;
  }

  @Override
  public LinkState getLinkState()
  {
    synchronized (getLock()) {
    }
  }

  @Override
  public void open() throws IOException
  {
    synchronized (lock) {
    }
  }

  @Override
  public void close() throws IOException
  {
    synchronized (lock) {
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

  @Override
  public Locomotive getLocomotive(short locoAddress) throws IOException, TimeoutException
  {
  }

  @Override
  public Future<Byte> getAccessoryState(short decoder,
                                        byte port) throws IOException
  {
  }

  @Override
  public void setAccessoryState(short decoder,
                                byte port,
                                byte state) throws IOException
  {
  }

  @Override
  public Future<Byte> setAccessoryStateChecked(short decoder,
                                               byte port,
                                               byte state) throws IOException
  {
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

}
