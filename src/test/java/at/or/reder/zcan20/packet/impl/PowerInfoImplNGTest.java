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
package at.or.reder.zcan20.packet.impl;

import at.or.reder.dcc.PowerPort;
import at.or.reder.zcan20.impl.UDPMarshaller;
import at.or.reder.zcan20.packet.Packet;
import at.or.reder.zcan20.packet.PowerInfo;
import at.or.reder.zcan20.util.HexStringInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import static org.testng.AssertJUnit.*;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 *
 * @author Wolfgang Reder
 */
public class PowerInfoImplNGTest
{

  public PowerInfoImplNGTest()
  {
  }

  @BeforeClass
  public static void setUpClass() throws Exception
  {
  }

  private ByteBuffer createBuffer(String str) throws IOException
  {
    HexStringInputStream in = new HexStringInputStream(str);
    ByteBuffer result = ByteBuffer.allocate(in.available());
    in.read(result.array());
    result.rewind();
    return result;
  }

  @Test(enabled = false)

  public void testPacketDecode() throws IOException
  {
    ByteBuffer buffer = createBuffer("160000001802a6c0a6c000000837a3000000a8367c000100070089742d00");
    Packet packet = UDPMarshaller.unmarshalPacket(buffer);
    PowerInfo powerInfo = packet.getAdapter(PowerInfo.class);
    assertNotNull(powerInfo);
    float current1 = powerInfo.getOutputCurrent(PowerPort.OUT_1);
    float voltage1 = powerInfo.getOutputVoltage(PowerPort.OUT_1);
    float current2 = powerInfo.getOutputCurrent(PowerPort.OUT_2);
    float voltage2 = powerInfo.getOutputVoltage(PowerPort.OUT_2);
    assertEquals(0.163f,
                 current1,
                 1e-5f);
    assertEquals(0.124f,
                 current2,
                 1e-5f);
    assertEquals(14.088f,
                 voltage1,
                 1e-5f);
    assertEquals(13.992f,
                 voltage2,
                 1e-5f);
    float power1 = powerInfo.getOutputPower(PowerPort.OUT_1);
    float power2 = powerInfo.getOutputPower(PowerPort.OUT_2);
    float powerOut = powerInfo.getTotalOutputPower();
    assertEquals(current1 * voltage1,
                 power1,
                 1e-5f);
    assertEquals(current2 * voltage2,
                 power2,
                 1e-5f);
    assertEquals(powerOut,
                 power1 + power2,
                 1e-5f);
    float ci = powerInfo.getInputCurrent();
    float vi = powerInfo.getInputVoltage();
    float pi = powerInfo.getInputPower();
    assertEquals(29.833f,
                 vi,
                 1e-5f);
    assertEquals(0.45f,
                 ci,
                 1e-5f);
    assertEquals(ci * vi,
                 pi,
                 1e-5f);
    float e = powerInfo.getEfficiency();
    assertEquals(0.30029,
                 e,
                 1e-5f);
  }

}
