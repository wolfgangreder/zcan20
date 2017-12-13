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
package com.reder.zcan20;

import com.reder.zcan20.impl.UDPPort;
import com.reder.zcan20.impl.ZCANImpl;
import com.reder.zcan20.impl.ZPort;
import com.reder.zcan20.packet.PacketBuilder;
import com.reder.zcan20.packet.impl.DefaultPacketBuilder;
import java.io.IOException;
import java.util.Map;
import javax.validation.constraints.NotNull;

public final class ZCANFactory
{

  public static final String PROP_NID = "com.reder.zcan20.nid";
  public static final String DEFAULT_NID = "c2ff";

  public static final short FUNC_RNG = 254;
  public static final short FUNC_MAN = 255;

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
   * @return Interface to the device.
   * @throws IOException if the connection cannot be established.
   */
  public static ZCAN open(@NotNull final String address,
                          int remotePort,
                          int localPort,
                          Map<String, String> properties) throws IOException
  {
    ZPort port = new UDPPort(address,
                             remotePort,
                             localPort);
    ZCANImpl result = new ZCANImpl(port,
                                   properties);
    result.open();
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

}
