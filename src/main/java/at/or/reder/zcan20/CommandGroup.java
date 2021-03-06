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
package at.or.reder.zcan20;

import at.or.reder.dcc.util.DCCUtils;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

/**
 *
 * @author Wolfgang Reder
 */
public final class CommandGroup implements Serializable
{

  private static final ConcurrentMap<Byte, CommandGroup> INSTANCES = new ConcurrentHashMap<>();
  private static final byte MAGIC_COMMAND_DISABLE = (byte) 0xff;

  public static final byte SYSTEM_POWER = 0x00;
  public static final byte SYSTEM_MAGIC = 0;
  public static final CommandGroup SYSTEM = valueOf(SYSTEM_MAGIC,
                                                    "SYSTEM",
                                                    SYSTEM_POWER);
  public static final byte ACCESSORY_STATE = 0x00;
  public static final byte ACCESSORY_MODE = 0x01;
  public static final byte ACCESSORY_GPIO = 0x02;
  public static final byte ACCESSORY_PORT4 = 0x04;
  public static final byte ACCESSORY_DATA = 0x05;
  public static final byte ACCESSORY_PORT6 = 0x06;
  public static final byte ACCESSORY_MAGIC = (byte) 0x01;
  public static final CommandGroup ACCESSORY = valueOf(ACCESSORY_MAGIC,
                                                       "ACCESSORY",
                                                       ACCESSORY_STATE,
                                                       ACCESSORY_MODE,
                                                       ACCESSORY_GPIO,
                                                       ACCESSORY_PORT4,
                                                       ACCESSORY_DATA,
                                                       ACCESSORY_PORT6);
  public static final byte LOCO_STATE = 0x00;
  public static final byte LOCO_MODE = 0x01;
  public static final byte LOCO_SPEED = 0x02;
  public static final byte LOCO_FUNC_INFO = 0x03;
  public static final byte LOCO_FUNC_SWITCH = 0x04;
  public static final byte LOCO_ACTIVE = 0x10;
  public static final byte LOCO_LOAD = 0x12;
  public static final CommandGroup LOCO = valueOf((byte) 0x02,
                                                  "LOCO",
                                                  LOCO_STATE,
                                                  LOCO_MODE,
                                                  LOCO_SPEED,
                                                  LOCO_FUNC_INFO,
                                                  LOCO_FUNC_SWITCH,
                                                  LOCO_ACTIVE,
                                                  LOCO_LOAD);
  public static final CommandGroup FREE_1 = valueOf((byte) 0x03,
                                                    "FREE 1",
                                                    MAGIC_COMMAND_DISABLE);
  public static final CommandGroup RCS = valueOf((byte) 0x04,
                                                 "RCS",
                                                 MAGIC_COMMAND_DISABLE);
  public static final CommandGroup FREE_2 = valueOf((byte) 0x05,
                                                    "FREE 2",
                                                    MAGIC_COMMAND_DISABLE);
  public static final byte DATA_GROUP_COUNT = 0x00;
  public static final byte DATA_ITEMLIST_INDEX = 0x01;
  public static final byte DATA_ITEMLIST_NID = 0x02;
  /**
   * {@code
   *   Aufbau:
   ***-------------------------------*
   * | 0 | 1 | 2 | 3 | 4 | 5 | 6 | 7 |
   ***-------------------------------*
   * | NID   |?????  |GRP ?????????? |
   ***-------------------------------*
   * }
   *
   * NID: Fahrzeugadresse GRP: Fahrzeugruppe (Dampflokomotive,....)
   */
  public static final byte DATA_GROUP = 0x08;
  public static final byte DATA_NAME = 0x10;
  public static final byte DATA_ITEM_IMAGE = 0x12;
  public static final byte DATA_NAME_EXT = 0x21;
  public static final byte DATA_LOCO_GUI_EXT = 0x27;
  public static final CommandGroup DATA = valueOf((byte) 0x07,
                                                  "DATA",
                                                  DATA_GROUP_COUNT,
                                                  DATA_ITEMLIST_INDEX,
                                                  DATA_ITEMLIST_NID,
                                                  DATA_NAME,
                                                  DATA_ITEM_IMAGE,
                                                  DATA_NAME_EXT,
                                                  DATA_LOCO_GUI_EXT);
  public static final byte CONFIG_POWER_INFO = 0x00;
  /*
  data[0]{2} = senderNid
  data[2] = flags port 1 (main)
            0x00 -> ein
            0x01 -> aus
            0x04 -> überstrom
            0x10 -> sspf0
            0x20 -> sspem
  data[3] = ???
  data[4]{2} = spannung [mV]
  data[6]{2} = strom [mA]
  data[8] = flags port 2 (prog)
  data[9] = ???
  data[10]{2} = spannung [mV]
  data[12]{2} = strom [mA]
  data[14]{4} = ???
  data[18] = eingangsspannung [mV]
  data[20] = eingangssrom [10mA]
   */
  public static final byte CONFIG_TACHO = 0x05;  // Irgendwas mit dem Fahrzeugstatus (Decoderadressen werden gesendet) Geschwindigkeitsinformation
  /*
  data[3]: 1 -> getShort(3): tachoAnzeige
  data[3]: 3 -> ?????
  data[3]: 8 -> getShort(3): 0x01 -> vorwärts, 0x02->rückwärts,0x8->dir Pending
   */
  public static final byte CONFIG_MODULE_INFO = 0x08;
  public static final byte CONFIG_UNKNOWN_A = 0x0a; // Wird gesendet, wenn man auf Sammelstop geht.
  public static final byte CONFIG_MODULE_POWER_INFO = 0x20;
  public static final byte CONFIG_MAGIC = 0x18;
  public static final CommandGroup CONFIG = valueOf(CONFIG_MAGIC,
                                                    "CONFIG",
                                                    CONFIG_POWER_INFO,
                                                    CONFIG_TACHO,
                                                    CONFIG_MODULE_INFO,
                                                    CONFIG_MODULE_POWER_INFO);
  public static final byte CONFIG_CAN_UNKNOWN_2 = 0x02; // wird bei einem modulestart als ACK mit dlc=8 gesendet
  public static final byte CONFIG_CAN_MAGIC = 0x08;
  public static final CommandGroup CONFIG_CAN = valueOf(CONFIG_CAN_MAGIC,
                                                        "CONFIG_CAN",
                                                        CONFIG_POWER_INFO);
  public static final CommandGroup PUBLIC = valueOf((byte) 0x09,
                                                    "PUBLIC");
  public static final byte NETWORK_PING = 0x00;
  public static final byte NETWORK_PORT_OPEN = 0x06;
  public static final byte NETWORK_PORT_CLOSE = 0x07;
  public static final byte NETWORK_INTERFACE_OPTION = 0x0a;
  public static final byte NETWORK_ERROR = 0x0f;
  public static final CommandGroup NETWORK = valueOf((byte) 0x0a,
                                                     "NETWORK",
                                                     NETWORK_PING,
                                                     NETWORK_PORT_OPEN,
                                                     NETWORK_PORT_CLOSE,
                                                     NETWORK_INTERFACE_OPTION,
                                                     NETWORK_ERROR);
  public static final CommandGroup NETWORK_EXT = valueOf((byte) 0x1a,
                                                         "NETWORK_EX",
                                                         NETWORK_PORT_OPEN);
  public static final CommandGroup FILE_CONTROL = valueOf((byte) 0x0e,
                                                          "FILE CONTROL",
                                                          MAGIC_COMMAND_DISABLE);
  public static final CommandGroup FILE_TRANSFER = valueOf((byte) 0x0f,
                                                           "FILE TRANSFER",
                                                           MAGIC_COMMAND_DISABLE);
  public static final byte TRACK_CONFIG_PRIVATE_MAGIC = (byte) 0x06;
  public static final CommandGroup TRACK_CONFIG_PRIVATE = valueOf(TRACK_CONFIG_PRIVATE_MAGIC,
                                                                  "TRACK CONFIG PRIVATE",
                                                                  MAGIC_COMMAND_DISABLE);
  public static final byte TRACK_CONFIG_PUBLIC_MAGIC = 0x16;
  public static final byte TSE_PROG_MODE = 0x00;
  public static final byte TSE_PROG_BUSY = 0x02; // not documented !
  public static final byte TSE_PROG_CLEAR = 0x04;
  public static final byte TSE_PROG_READ = 0x08;
  public static final byte TSE_PROG_WRITE = 0x09;
  public static final CommandGroup TRACK_CONFIG_PUBLIC = valueOf(TRACK_CONFIG_PUBLIC_MAGIC,
                                                                 "TRACK CONFIG PUBLIC",
                                                                 TSE_PROG_BUSY,
                                                                 TSE_PROG_READ,
                                                                 TSE_PROG_WRITE);

  private static final long serialVersionUID = 1L;

  private static CommandGroup valueOf(byte magic,
                                      String name,
                                      Byte... allowedCommands)
  {
    return INSTANCES.computeIfAbsent((byte) (magic & 0xff),
                                     (m) -> new CommandGroup(m,
                                                             name,
                                                             Arrays.asList(
                                                                     allowedCommands)));
  }

  public static CommandGroup valueOf(byte magic)
  {
    return INSTANCES.computeIfAbsent((byte) (magic & 0xff),
                                     (m) -> new CommandGroup(m,
                                                             "UNKNOWN"));
  }

  private final byte magic;
  private final String name;
  private final Set<Byte> allowedCommands;

  private CommandGroup(byte magic,
                       String name)
  {
    this(magic,
         name,
         null);
  }

  private CommandGroup(byte magic,
                       String name,
                       Collection<Byte> allowedCommands)
  {
    this.magic = magic;
    this.name = Objects.requireNonNull(name,
                                       "name is null");
    if (allowedCommands == null) {
      this.allowedCommands = null;
    } else if (!allowedCommands.contains(MAGIC_COMMAND_DISABLE)) {
      this.allowedCommands = Collections.unmodifiableSet(allowedCommands.
              stream().
              filter((i) -> i != null).
              collect(Collectors.toSet()));
    } else {
      this.allowedCommands = Collections.emptySet();
    }
  }

  public boolean isCommandAllowed(byte cmd)
  {
    return allowedCommands == null || allowedCommands.contains(cmd);
  }

  public Set<Byte> getAllowedCommands()
  {
    return allowedCommands;
  }

  public byte getMagic()
  {
    return (byte) magic;
  }

  public String getName()
  {
    return name;
  }

  @Override
  public int hashCode()
  {
    int hash = 5;
    hash = 73 * hash + this.magic;
    return hash;
  }

  @Override
  @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
  public boolean equals(Object obj)
  {
    return this == obj;
  }

  private Object readResolve() throws ObjectStreamException
  {
    return valueOf(magic);
  }

  @Override
  public String toString()
  {
    return "CommandGroup " + getName() + " 0x" + DCCUtils.appendHexString(magic,
                                                                       new StringBuilder(),
                                                                       2);
  }

}
