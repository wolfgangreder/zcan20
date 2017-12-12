/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
   * Opens a connection to a device via serial Port.
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
   * @return a new PacketBuilder
   */
  public static PacketBuilder createPacketBuilder()
  {
    return new DefaultPacketBuilder();
  }

}
