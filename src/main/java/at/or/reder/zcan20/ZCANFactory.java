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

import at.or.reder.zcan20.impl.UDPPort;
import at.or.reder.zcan20.impl.ZCANImpl;
import at.or.reder.zcan20.impl.ZPort;
import at.or.reder.zcan20.packet.PacketBuilder;
import at.or.reder.zcan20.packet.impl.DefaultPacketBuilder;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import javax.validation.constraints.NotNull;

public final class ZCANFactory
{

  public static final String PROP_NID = "com.reder.zcan20.nid";
  public static final String DEFAULT_NID = "c2ff";
  public static final int MAX_LOCO_FX = 32;
  public static final short LOCO_MIN = 0;
  public static final short LOCO_MAX = 0x27ff;
  public static final short ACC_MIN = 0x3000;
  public static final short ACC_MAX = 0x31ff;
  public static final short MX1_MIN = 0x5000;
  public static final short MX1_MAX = 0x503f;
  public static final short MX8_MIN = 0x5040;
  public static final short MX8_MAX = 0x507f;
  public static final short MX9_MIN = 0x5080;
  public static final short MX9_MAX = 0x50bf;
  public static final short MFX_MIN = (short) 0x8000;
  public static final short MFX_MAX = (short) 0xbfff;
  public static final short CENTRAL_MIN = (short) 0xc000;
  public static final short CENTRAL_MAX = (short) 0xc0ff;
  public static final short SPECIAL_MIN = (short) 0xc200;
  public static final short SPECIAL_MAX = (short) 0xc2ff;
  public static final short FUNC_RNG = 254;
  public static final short FUNC_MAN = 255;
  public static final Set<Integer> READONLY_CV = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(8,
                                                                                                         7,
                                                                                                         65,
                                                                                                         250,
                                                                                                         251,
                                                                                                         252,
                                                                                                         253,
                                                                                                         260,
                                                                                                         261,
                                                                                                         262,
                                                                                                         263,
                                                                                                         1,
                                                                                                         17,
                                                                                                         18)));

  /**
   * Opens a connection to a device via serial Port.
   *
   * @param commPort Serial port to open
   * @param properties Map of connection properties. Can be {@code null}.
   * @return Interface to the device.
   * @throws IOException if the connection cannot be established.
   */
  public static ZCAN open(@NotNull final String commPort,
                          Map<String, String> properties) throws IOException
  {
    throw new UnsupportedOperationException("not implemented yet");
  }

  /**
   * Opens a connection to a device via UDP.
   *
   * @param address IP address or fqn of the remote device.
   * @param remotePort Remote listening port.
   * @param localPort Local listening port.
   * @param properties Map of connection properties. Can be {@code null}.
   * @param timeOut timeOut to wait for ping
   * @param unit timeunit for waiting
   * @return Interface to the device.
   * @throws IOException if the connection cannot be established.
   */
  public static ZCAN open(@NotNull final String address,
                          int remotePort,
                          int localPort,
                          Map<String, String> properties,
                          long timeOut,
                          TimeUnit unit) throws IOException
  {
    ZPort port = new UDPPort(address,
                             remotePort,
                             localPort);
    ZCANImpl result = new ZCANImpl(port,
                                   properties);
    result.open(timeOut,
                unit);
    return result;
  }

  /**
   * Create a PacketBuilder.
   *
   * @param myNID Own network id
   * @return a new PacketBuilder
   */
  public static PacketBuilder createPacketBuilder(short myNID)
  {
    return new DefaultPacketBuilder(myNID);
  }

  public static short toLongAddress(short cv17,
                                    short cv18)
  {
    if (cv17 < 192 || cv17 > 231) {
      throw new IllegalArgumentException("cv17 out of range");
    }
    int tmp = (cv17 << 8) & 0x3F00;
    tmp |= (cv18 & 0xff);
    return (short) tmp;
  }

  public static short toCV17(short longAddress)
  {
    if (!isLongAddress(longAddress)) {
      throw new IllegalArgumentException("argument is not a long address");
    }
    return (short) ((longAddress >> 8) & 0x3f);
  }

  public static short toCV18(short longAddress)
  {
    if (!isLongAddress(longAddress)) {
      throw new IllegalArgumentException("argument is not a long address");
    }
    return (short) (longAddress & 0xff);
  }

  public static boolean isLongAddress(short address)
  {
    return address > 127;
  }

}
