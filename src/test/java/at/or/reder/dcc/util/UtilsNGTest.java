/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.or.reder.dcc.util;

import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertSame;
import static org.testng.AssertJUnit.fail;
import org.testng.annotations.Test;

/**
 *
 * @author reder
 */
public class UtilsNGTest
{

  public UtilsNGTest()
  {
  }

  public byte[] toByteArray(int... ia)
  {
    byte[] result = new byte[ia.length];
    for (int i = 0; i < ia.length; ++i) {
      result[i] = (byte) ia[i];
    }
    return result;
  }

  public List<Byte> toByteList(int... ia)
  {
    List<Byte> result = new ArrayList<>(ia.length);
    for (int i = 0; i < ia.length; ++i) {
      result.add((byte) ia[i]);
    }
    return result;
  }

  @Test
  public void testHexString2ByteSink() throws ParseException
  {
    List<Byte> expected = toByteList(0,
                                     1,
                                     2,
                                     3,
                                     4,
                                     5,
                                     0x1a,
                                     0x1b,
                                     0x1c,
                                     0x0ad,
                                     0xaf,
                                     0xef,
                                     0xff);
    String in = "0001020304051a1b1cadafefff";
    AtomicInteger index = new AtomicInteger();
    Utils.hexString2ByteConsumer(in,
                                 (byte b) -> {
                                   int i = index.get();
                                   assertEquals("Missmatch at position " + i,
                                                Byte.valueOf(b),
                                                expected.get(i));
                                   index.incrementAndGet();
                                 },
                                 (char) 0);
    in = "00 01 02 03 04 05 1a 1b 1c ad af ef ff";
    index.set(0);
    Utils.hexString2ByteConsumer(in,
                                 (byte b) -> {
                                   int i = index.get();
                                   assertEquals("Missmatch at position " + i,
                                                Byte.valueOf(b),
                                                expected.get(i));
                                   index.incrementAndGet();
                                 },
                                 ' ');
    in = "00:01:02:03:04:05:1a:1b:1c:ad:af:eF:ff";
    index.set(0);
    Utils.hexString2ByteConsumer(in,
                                 (byte b) -> {
                                   int i = index.get();
                                   assertEquals("Missmatch at position " + i,
                                                Byte.valueOf(b),
                                                expected.get(i));
                                   index.incrementAndGet();
                                 },
                                 ':');
  }

  @Test
  public void testHexString2ByteSinkEmptyString() throws ParseException
  {
    Utils.hexString2ByteConsumer("",
                                 (b) -> fail("Empty input not detected"),
                                 (char) 0);
  }

  @Test(expectedExceptions = ParseException.class)
  public void testHexString2ByteSinkIllegalLength() throws ParseException
  {
    String in = "00f";
    AtomicInteger index = new AtomicInteger();
    Utils.hexString2ByteConsumer(in,
                                 (b) -> {
                                   if (index.incrementAndGet() > 1) {
                                     fail("Illegal Input not detected");
                                   }
                                 },
                                 (char) 0);
  }

  @Test(expectedExceptions = ParseException.class)
  public void testHexString2ByteSinkIllegalChar() throws ParseException
  {
    String in = "0gf";
    AtomicInteger index = new AtomicInteger();
    Utils.hexString2ByteConsumer(in,
                                 (b) -> {
                                   if (index.incrementAndGet() > 1) {
                                     fail("Illegal Input not detected");
                                   }
                                 },
                                 (char) 0);
  }

  @Test(expectedExceptions = ParseException.class)
  public void testHexString2ByteSinkIllegalLength2() throws ParseException
  {
    String in = "0gf";
    Utils.hexString2ByteConsumer(in,
                                 (b) -> fail("Illegal Input not detected"),
                                 (char) 'g');
  }

  @Test(expectedExceptions = NullPointerException.class)
  @SuppressWarnings("null")
  public void testHexString2ByteSinkNullCharsequence() throws ParseException
  {
    Utils.hexString2ByteConsumer(null,
                                 (b) -> fail("Illegal Input not detected"),
                                 (char) 'g');
  }

  @Test(expectedExceptions = NullPointerException.class)
  @SuppressWarnings("null")
  public void testHexString2ByteSinkNullSink() throws ParseException
  {
    Utils.hexString2ByteConsumer("ab",
                                 null,
                                 (char) 'g');
  }

  @Test(expectedExceptions = ParseException.class)
  public void testHexString2ByteSinkIllegalInterbyteChar() throws ParseException
  {
    AtomicInteger index = new AtomicInteger();
    Utils.hexString2ByteConsumer("ab cD:EF",
                                 (b) -> {
                                   if (index.incrementAndGet() > 2) {
                                     fail("Illegal Input not detected");
                                   }
                                 },
                                 (char) ' ');
  }

  @Test
  public void testHexString2ByteBuffer() throws ParseException
  {
    ByteBuffer expected = ByteBuffer.allocate(0);
    ByteBuffer result = Utils.hexString2ByteBuffer("",
                                                   null,
                                                   (char) 0);
    result.clear();
    assertEquals(expected,
                 result);
    expected = ByteBuffer.wrap(toByteArray(0xaf));
    expected.clear();
    result = Utils.hexString2ByteBuffer("af",
                                        null,
                                        (char) 0);
    result.clear();
    assertEquals(expected,
                 result);
    expected = ByteBuffer.wrap(toByteArray(0xaf,
                                           0xfe));
    expected.clear();
    result = Utils.hexString2ByteBuffer("affe",
                                        null,
                                        (char) 0);
    result.clear();
    assertEquals(expected,
                 result);
    expected = ByteBuffer.wrap(toByteArray(0xca,
                                           0xfe,
                                           0xba));
    expected.clear();
    result = Utils.hexString2ByteBuffer("cafeba",
                                        null,
                                        (char) 0);
    result.clear();
    assertEquals(expected,
                 result);
    expected = ByteBuffer.wrap(toByteArray(0xca,
                                           0xfe,
                                           0xba,
                                           0xbe));
    expected.clear();
    result = Utils.hexString2ByteBuffer("cafebabe",
                                        null,
                                        (char) 0);
    result.clear();
    assertEquals(expected,
                 result);
    expected = ByteBuffer.allocate(0);
    result = Utils.hexString2ByteBuffer("",
                                        null,
                                        ' ');
    result.clear();
    assertEquals(expected,
                 result);
    expected = ByteBuffer.wrap(toByteArray(0xaf));
    expected.clear();
    result = Utils.hexString2ByteBuffer("af",
                                        null,
                                        ' ');
    result.clear();
    assertEquals(expected,
                 result);
    expected = ByteBuffer.wrap(toByteArray(0xaf,
                                           0xfe));
    expected.clear();
    result = Utils.hexString2ByteBuffer("af fe",
                                        null,
                                        ' ');
    result.clear();
    assertEquals(expected,
                 result);
    expected = ByteBuffer.wrap(toByteArray(0xca,
                                           0xfe,
                                           0xba));
    expected.clear();
    result = Utils.hexString2ByteBuffer("ca fe ba",
                                        null,
                                        ' ');
    result.clear();
    assertEquals(expected,
                 result);
    expected = ByteBuffer.wrap(toByteArray(0xca,
                                           0xfe,
                                           0xba,
                                           0xbe));
    expected.clear();
    result = Utils.hexString2ByteBuffer("ca fe ba be",
                                        null,
                                        ' ');
    result.clear();
    assertEquals(expected,
                 result);
    expected = ByteBuffer.wrap(toByteArray(0xbe));
    expected.clear();
    result = Utils.hexString2ByteBuffer("be ",
                                        null,
                                        ' ');
    result.clear();
    assertEquals(expected,
                 result);
    expected = ByteBuffer.wrap(toByteArray(0xca,
                                           0xfe,
                                           0xba,
                                           0xbe));
    expected.clear();
    ByteBuffer input = ByteBuffer.allocate(4);
    result = Utils.hexString2ByteBuffer("ca fe ba be ",
                                        input,
                                        ' ');
    result.clear();
    assertSame(input,
               result);
    assertEquals(expected,
                 result);
  }

  @Test(expectedExceptions = {BufferOverflowException.class})
  public void testHexString2ByteBufferBfferOverflow() throws ParseException
  {
    ByteBuffer buffer = ByteBuffer.allocate(2);
    Utils.hexString2ByteBuffer("cafeba",
                               buffer,
                               (char) 0);
  }

  @Test
  public void testByte_n()
  {
    int i = 0x65432100;
    assertEquals((byte) 0,
                 Utils.byte1(i));
    assertEquals((byte) 0x21,
                 Utils.byte2(i));
    assertEquals((byte) 0x43,
                 Utils.byte3(i));
    assertEquals((byte) 0x65,
                 Utils.byte4(i));
    i = 0x00123456;
    assertEquals((byte) 0x56,
                 Utils.byte1(i));
    assertEquals((byte) 0x34,
                 Utils.byte2(i));
    assertEquals((byte) 0x12,
                 Utils.byte3(i));
    assertEquals((byte) 0x00,
                 Utils.byte4(i));
  }

  @Test
  public void testCrc8()
  {
    ByteBuffer buffer = ByteBuffer.wrap(new byte[]{(byte) 0x88, 0x00, 0x01});
    byte crc = Utils.crc8((byte) 0xff,
                          buffer);
    byte expected = (byte) 0xcb;
    assertEquals(expected,
                 crc);
  }

}
