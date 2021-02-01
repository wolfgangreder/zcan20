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

import at.or.reder.dcc.util.DCCUtils;
import java.nio.ByteBuffer;
import static org.testng.AssertJUnit.assertEquals;
import org.testng.annotations.Test;

/**
 *
 * @author Wolfgang Reder
 */
public class MX1PortImplNGTest
{

  public MX1PortImplNGTest()
  {
  }

  @Test
  public void testCalculateNeededBufSize1()
  {
    ByteBuffer in = ByteBuffer.allocate(10);
    in.limit(in.position());
    int result = MX1PortImpl.calculateNeededBufSize(in);
    assertEquals(0,
                 result);
    in.limit(1);
    in.put((byte) 0x00);
    in.position(0);
    result = MX1PortImpl.calculateNeededBufSize(in);
    assertEquals(1,
                 result);
    in.rewind();
    in.put((byte) MX1PortImpl.SOH);
    in.position(0);
    result = MX1PortImpl.calculateNeededBufSize(in);
    assertEquals(2,
                 result);
    in.put((byte) MX1PortImpl.DLE);
    in.position(0);
    result = MX1PortImpl.calculateNeededBufSize(in);
    assertEquals(2,
                 result);
    in.put((byte) MX1PortImpl.EOT);
    in.position(0);
    result = MX1PortImpl.calculateNeededBufSize(in);
    assertEquals(2,
                 result);
  }

  @Test
  public void testCalculateNeededBufSize2()
  {
    ByteBuffer in = ByteBuffer.allocate(10);
    in.limit(2);
    in.rewind();
    in.put((byte) 0);
    in.put((byte) 0xff);
    in.rewind();
    int result = MX1PortImpl.calculateNeededBufSize(in);
    assertEquals(2,
                 result);
    in.rewind();
    in.put((byte) 0);
    in.put(MX1PortImpl.DLE);
    in.rewind();
    result = MX1PortImpl.calculateNeededBufSize(in);
    assertEquals(3,
                 result);
    in.rewind();
    in.put(MX1PortImpl.EOT);
    in.put((byte) 0);
    in.rewind();
    result = MX1PortImpl.calculateNeededBufSize(in);
    assertEquals(3,
                 result);
    in.limit(2);
    in.rewind();
    in.put((byte) 0);
    in.put((byte) 0xff);
    in.rewind();
    result = MX1PortImpl.calculateNeededBufSize(in);
    assertEquals(2,
                 result);
    in.rewind();
    in.put((byte) 0);
    in.put(MX1PortImpl.DLE);
    in.rewind();
    result = MX1PortImpl.calculateNeededBufSize(in);
    assertEquals(3,
                 result);
    in.rewind();
    in.put(MX1PortImpl.EOT);
    in.put((byte) 0);
    in.rewind();
    result = MX1PortImpl.calculateNeededBufSize(in);
    assertEquals(3,
                 result);
    in.rewind();
    in.put(MX1PortImpl.EOT);
    in.put(MX1PortImpl.DLE);
    in.rewind();
    result = MX1PortImpl.calculateNeededBufSize(in);
    assertEquals(4,
                 result);
  }

  @Test
  public void testCalculateNeededBufSize3()
  {
    ByteBuffer in = ByteBuffer.allocate(10);
    in.limit(3);
    in.put((byte) 0);
    in.put((byte) 2);
    in.put((byte) 3);
    in.rewind();
    int result = MX1PortImpl.calculateNeededBufSize(in);
    assertEquals(3,
                 result);
    in.rewind();

    in.put((byte) MX1PortImpl.DLE);
    in.put((byte) 2);
    in.put((byte) 3);
    in.rewind();
    result = MX1PortImpl.calculateNeededBufSize(in);
    assertEquals(4,
                 result);
    in.rewind();

    in.put((byte) 2);
    in.put((byte) MX1PortImpl.DLE);
    in.put((byte) 3);
    in.rewind();
    result = MX1PortImpl.calculateNeededBufSize(in);
    assertEquals(4,
                 result);
    in.rewind();

    in.put((byte) 2);
    in.put((byte) 3);
    in.put((byte) MX1PortImpl.DLE);
    in.rewind();
    result = MX1PortImpl.calculateNeededBufSize(in);
    assertEquals(4,
                 result);
    in.rewind();

    in.put((byte) 2);
    in.put((byte) MX1PortImpl.SOH);
    in.put((byte) MX1PortImpl.DLE);
    in.rewind();
    result = MX1PortImpl.calculateNeededBufSize(in);
    assertEquals(5,
                 result);
    in.rewind();

    in.put((byte) MX1PortImpl.SOH);
    in.put((byte) MX1PortImpl.DLE);
    in.put((byte) 2);
    in.rewind();
    result = MX1PortImpl.calculateNeededBufSize(in);
    assertEquals(5,
                 result);
    in.rewind();

    in.put((byte) MX1PortImpl.SOH);
    in.put((byte) 2);
    in.put((byte) MX1PortImpl.DLE);
    in.rewind();
    result = MX1PortImpl.calculateNeededBufSize(in);
    assertEquals(5,
                 result);
    in.rewind();
  }

  @Test
  public void testEscapeBuffer1()
  {
    ByteBuffer in = ByteBuffer.wrap(new byte[]{(byte) 0, MX1PortImpl.DLE, (byte) 2, MX1PortImpl.EOT});
    int result = MX1PortImpl.escapeBuffer(in,
                                          null);
    ByteBuffer out = ByteBuffer.allocate(result);
    result = MX1PortImpl.escapeBuffer(in,
                                      out);
    assertEquals(0,
                 out.position());
    assertEquals(6,
                 result);
    String s1 = DCCUtils.byteArray2HexString(new byte[]{(byte) 0, MX1PortImpl.DLE, (byte) (MX1PortImpl.DLE ^ 0x20), (byte) 2,
                                                     MX1PortImpl.DLE, (byte) (MX1PortImpl.EOT ^ 0x20)});
    String s2 = DCCUtils.byteBuffer2HexString(out,
                                           null,
                                           (char) 0).toString();
    assertEquals(s1,
                 s2);
    in = ByteBuffer.wrap(new byte[]{(byte) 0, (byte) 3, (byte) 4, (byte) 5, (byte) 6, (byte) 7, (byte) 8});
    result = MX1PortImpl.escapeBuffer(in,
                                      out);
    assertEquals(0,
                 out.position());
    assertEquals(7,
                 result);
  }

  @Test
  public void testExcapeBuffer2()
  {
    ByteBuffer out = ByteBuffer.allocate(2);
    ByteBuffer in = ByteBuffer.wrap(new byte[]{(byte) 0, MX1PortImpl.DLE});
    int result = MX1PortImpl.escapeBuffer(in,
                                          out);
    assertEquals(0,
                 out.position());
    assertEquals(3,
                 result);
    out = ByteBuffer.allocate(result);
    result = MX1PortImpl.escapeBuffer(in,
                                      out);
    assertEquals(0,
                 out.position());
    assertEquals(3,
                 result);
    String s1 = DCCUtils.byteArray2HexString(new byte[]{(byte) 0, MX1PortImpl.DLE, (byte) (MX1PortImpl.DLE ^ 0x20)});
    String s2 = DCCUtils.byteBuffer2HexString(out,
                                           null,
                                           (char) 0).toString();
    assertEquals(s1,
                 s2);
  }

}
