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

import at.or.reder.dcc.PropertySet;
import at.or.reder.dcc.util.Utils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import javax.validation.constraints.NotNull;
import org.openide.util.NbBundle;
import org.openide.util.NbBundle.Messages;

/**
 *
 * @author Wolfgang Reder
 */
@Messages({"MX10PropertySet_at.or.reder.zcan20.mx10.port_name=COM Port",
           "MX10PropertySet_at.or.reder.zcan20.mx10.port_desc=Serielle Schittstelle zur Verbindung mittels COM Port.",
           "MX10PropertySet_at.or.reder.zcan20.mx10.port_default=",
           "MX10PropertySet_at.or.reder.zcan20.mx10.host_name=Host",
           "MX10PropertySet_at.or.reder.zcan20.mx10.host_desc=Hostname oder IP-Adresse zur Verbindung mittel UDP",
           "MX10PropertySet_at.or.reder.zcan20.mx10.host_default=192.168.1.145",
           "MX10PropertySet_at.or.reder.zcan20.mx10.inport_name=Lokaler Port",
           "MX10PropertySet_at.or.reder.zcan20.mx10.inport_desc=Port für eingehende Nachrichten.",
           "MX10PropertySet_at.or.reder.zcan20.mx10.inport_default=14521",
           "MX10PropertySet_at.or.reder.zcan20.mx10.outport_name=Entfernter Port",
           "MX10PropertySet_at.or.reder.zcan20.mx10.outport_desc=Port für ausgehende Nachrichten.",
           "MX10PropertySet_at.or.reder.zcan20.mx10.outport_default=14520",
           "MX10PropertySet_at.or.reder.zcan20.mx10.pingintervall_name=Ping Intervall",
           "MX10PropertySet_at.or.reder.zcan20.mx10.pingintervall_desc=Zeitabstand in Sekunden in dem mindestens ein Packet gesendet wird.",
           "MX10PropertySet_at.or.reder.zcan20.mx10.pingintervall_default=5",
           "MX10PropertySet_at.or.reder.zcan20.mx10.pingjitter_name=Jitter",
           "MX10PropertySet_at.or.reder.zcan20.mx10.pingjitter_desc=Unregelmässigkeit mit der mindestens ein Packet gesendet wird. Angabe in Prozent.",
           "MX10PropertySet_at.or.reder.zcan20.mx10.pingjitter_default=10",
           "MX10PropertySet_at.or.reder.zcan20.mx10.iotimeout_name=IO-Timout",
           "MX10PropertySet_at.or.reder.zcan20.mx10.iotimeout_desc=Wie lange soll auf eine Antwort des MX10 gewartet werden. Wird in Sekunden angegeben.",
           "MX10PropertySet_at.or.reder.zcan20.mx10.iotimeout_default=5",
           "MX10PropertySet_at.or.reder.zcan20.mx10.reconnecttimeout_name=Neuverbindungszeit",
           "MX10PropertySet_at.or.reder.zcan20.mx10.reconnecttimeout_desc=Nach welcher Zeitspanne soll nach einem Verbindungsabriss ein erneuter Verbindungsaufbau versucht werden.",
           "MX10PropertySet_at.or.reder.zcan20.mx10.reconnecttimeout_default=5"})
public final class MX10PropertiesSet implements PropertySet
{

  public static final String PROP_PORT = "at.or.reder.zcan20.mx10.port";
  public static final String PROP_HOST = "at.or.reder.zcan20.mx10.host";
  public static final String PROP_INPORT = "at.or.reder.zcan20.mx10.inport";
  public static final String PROP_OUTPORT = "at.or.reder.zcan20.mx10.outport";
  public static final String PROP_PINGINTERVALL = "at.or.reder.zcan20.mx10.pingintervall";
  public static final String PROP_PINGJITTER = "at.or.reder.zcan20.mx10.pingjitter";
  public static final String PROP_IOTIMEOUT = "at.or.reder.zcan20.mx10.iotimeout";
  public static final String PROP_RECONNECTTIMEOUT = "at.or.reder.zcan20.mx10.reconnecttimeout";
  private static final Set<String> propertyNames;

  static {
    propertyNames = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(PROP_HOST,
                                                                            PROP_INPORT,
                                                                            PROP_IOTIMEOUT,
                                                                            PROP_OUTPORT,
                                                                            PROP_PINGINTERVALL,
                                                                            PROP_PINGJITTER,
                                                                            PROP_PORT,
                                                                            PROP_RECONNECTTIMEOUT)));
  }

  @Override
  public List<String> getPropertyNames()
  {
    return new ArrayList<>(propertyNames);
  }

  @Override
  public String getReadableName(String propertyName) throws IllegalArgumentException
  {
    if (!propertyNames.contains(propertyName)) {
      throw new IllegalArgumentException("Uknown property " + propertyName);
    }
    return NbBundle.getMessage(MX10PropertiesSet.class,
                               "MX10PropertySet_" + propertyName + "_name");
  }

  @Override
  public String getPropertyDescription(String propertyName) throws IllegalArgumentException
  {
    if (!propertyNames.contains(propertyName)) {
      throw new IllegalArgumentException("Uknown property " + propertyName);
    }
    return NbBundle.getMessage(MX10PropertiesSet.class,
                               "MX10PropertySet_" + propertyName + "_desc");
  }

  @Override
  public String getDefaultValue(String propertyName) throws IllegalArgumentException
  {
    if (!propertyNames.contains(propertyName)) {
      throw new IllegalArgumentException("Uknown property " + propertyName);
    }
    return NbBundle.getMessage(MX10PropertiesSet.class,
                               "MX10PropertySet_" + propertyName + "_default");
  }

  @SuppressWarnings("UseSpecificCatch")
  private int testInteger(String value)
  {
    try {
      return Integer.parseInt(value);
    } catch (Throwable th) {
    }
    return -1;
  }

  private boolean testPortNum(String value)
  {
    int i = testInteger(value);
    return i > 0 && i < 0x10000;
  }

  private boolean testSerialPort(String value)
  {
    switch (Utils.getOSType()) {
      case LINUX: {
        Pattern pattern = Pattern.compile("\\A/dev/(tty|ACM)/\\d+\\z",
                                          Pattern.CASE_INSENSITIVE);
        return pattern.matcher(value).matches();
      }
      case WINDOWS: {
        Pattern pattern = Pattern.compile("\\ACOM\\d+:?\\z");
        return pattern.matcher(value).matches();
      }
      default:
        return false;
    }
  }

  @Override
  public boolean isValueValid(String propertyName,
                              String value)
  {
    if (propertyName == null || propertyName.isBlank()) {
      return false;
    }
    switch (propertyName) {
      case PROP_PORT:
        return testSerialPort(value);
      case PROP_INPORT:
        return testPortNum(value);
      case PROP_OUTPORT:
        return testPortNum(value);
      case PROP_HOST:
        return true;
      case PROP_PINGINTERVALL:
        return testInteger(value) > 0;
      case PROP_PINGJITTER: {
        int i = testInteger(value);
        return i > 0 && i < 100;
      }
      case PROP_IOTIMEOUT:
        return testInteger(value) > 0;
      case PROP_RECONNECTTIMEOUT:
        return testInteger(value) > 0;
      default:
        return false;
    }
  }

  public boolean isKeyValueValid(Map<String, String> map,
                                 String propertyName)
  {
    return isValueValid(propertyName,
                        getStringValue(map,
                                       propertyName));
  }

  @Override
  public Map<String, String> getDefaultProperties()
  {
    Map<String, String> result = new HashMap<>();
    for (String p : propertyNames) {
      result.put(p,
                 getDefaultValue(p));
    }
    return result;
  }

  public String getStringValue(@NotNull Map<String, String> map,
                               @NotNull String propName)
  {
    String v = map.get(propName);
    if (v != null) {
      return v;
    } else {
      return getDefaultValue(propName);
    }
  }

  public int getIntValue(@NotNull Map<String, String> map,
                         @NotNull String propName)
  {
    if (PROP_HOST.equals(propName) || PROP_PORT.equals(propName)) {
      throw new IllegalArgumentException("Property " + propName + " is a non integer property");
    }
    String strProp = getStringValue(map,
                                    propName);
    return testInteger(strProp);
  }

}
