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

import at.or.reder.dcc.AbstractPropertySet;
import at.or.reder.dcc.PropertySet;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.validation.constraints.NotNull;
import org.openide.util.NbBundle;

@NbBundle.Messages({"Z21PropertySet_at.or.reder.z21.host_name=Host",
                    "Z21PropertySet_at.or.reder.z21.host_desc=Hostname oder IP-Adresse zur Verbindung mittels UDP",
                    "Z21PropertySet_at.or.reder.z21.host_default=192.168.1.145",
                    "Z21PropertySet_at.or.reder.z21.port_name=Lokaler Port",
                    "Z21PropertySet_at.or.reder.z21.port_desc=Port für eingehende Nachrichten.",
                    "Z21PropertySet_at.or.reder.z21.port_default=21105",
                    "Z21PropertySet_at.or.reder.z21.pingintervall_name=Ping Intervall",
                    "Z21PropertySet_at.or.reder.z21.pingintervall_desc=Zeitabstand in Sekunden in dem mindestens ein Packet gesendet wird.",
                    "Z21PropertySet_at.or.reder.z21.pingintervall_default=5",
                    "Z21PropertySet_at.or.reder.z21.pingjitter_name=Jitter",
                    "Z21PropertySet_at.or.reder.z21.pingjitter_desc=Unregelmässigkeit mit der mindestens ein Packet gesendet wird. Angabe in Prozent.",
                    "Z21PropertySet_at.or.reder.z21.pingjitter_default=10",
                    "Z21PropertySet_at.or.reder.z21.iotimeout_name=IO-Timout",
                    "Z21PropertySet_at.or.reder.z21.iotimeout_desc=Wie lange soll auf eine Antwort des MX10 gewartet werden. Wird in Sekunden angegeben.",
                    "Z21PropertySet_at.or.reder.z21.iotimeout_default=5",
                    "Z21PropertySet_at.or.reder.z21.reconnecttimeout_name=Neuverbindungszeit",
                    "Z21PropertySet_at.or.reder.z21.reconnecttimeout_desc=Nach welcher Zeitspanne soll nach einem Verbindungsabriss ein erneuter Verbindungsaufbau versucht werden.",
                    "Z21PropertySet_at.or.reder.z21.reconnecttimeout_default=5"})
public final class Z21PropertySet extends AbstractPropertySet implements PropertySet
{

  public static final String PROP_PORT = "at.or.reder.z21.port";
  public static final String PROP_HOST = "at.or.reder.z21.host";
  public static final String PROP_PINGINTERVALL = "at.or.reder.z21.pingintervall";
  public static final String PROP_PINGJITTER = "at.or.reder.z21.pingjitter";
  public static final String PROP_IOTIMEOUT = "at.or.reder.z21.iotimeout";
  public static final String PROP_RECONNECTTIMEOUT = "at.or.reder.z21.reconnecttimeout";
  private static final Set<String> propertyNames;

  static {
    propertyNames = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(PROP_HOST,
                                                                            PROP_PORT,
                                                                            PROP_IOTIMEOUT,
                                                                            PROP_PINGINTERVALL,
                                                                            PROP_PINGJITTER,
                                                                            PROP_RECONNECTTIMEOUT)));
  }

  @Override
  public Set<String> getPropertyNames()
  {
    return propertyNames;
  }

  @Override
  public String getReadableName(String propertyName) throws IllegalArgumentException
  {
    if (!propertyNames.contains(propertyName)) {
      throw new IllegalArgumentException("Uknown property " + propertyName);
    }
    return NbBundle.getMessage(Z21PropertySet.class,
                               "MX21PropertySet_" + propertyName + "_name");
  }

  @Override
  public String getPropertyDescription(String propertyName) throws IllegalArgumentException
  {
    if (!propertyNames.contains(propertyName)) {
      throw new IllegalArgumentException("Uknown property " + propertyName);
    }
    return NbBundle.getMessage(Z21PropertySet.class,
                               "MX21PropertySet_" + propertyName + "_desc");
  }

  @Override
  public String getDefaultValue(String propertyName) throws IllegalArgumentException
  {
    if (!propertyNames.contains(propertyName)) {
      throw new IllegalArgumentException("Uknown property " + propertyName);
    }
    return NbBundle.getMessage(Z21PropertySet.class,
                               "MX21PropertySet_" + propertyName + "_default");
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

  @Override
  public int getIntValue(@NotNull Map<String, String> map,
                         @NotNull String propName)
  {
    if (PROP_HOST.equals(propName)) {
      throw new IllegalArgumentException("Property " + propName + " is a non integer property");
    }
    return super.getIntValue(map,
                             propName);
  }

}
