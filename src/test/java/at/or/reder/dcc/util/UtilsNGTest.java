/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.or.reder.dcc.util;

import at.or.reder.dcc.DCCConstants;
import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertFalse;
import static org.testng.AssertJUnit.assertSame;
import static org.testng.AssertJUnit.assertTrue;
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
    DCCUtils.hexString2ByteConsumer(in,
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
    DCCUtils.hexString2ByteConsumer(in,
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
    DCCUtils.hexString2ByteConsumer(in,
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
    DCCUtils.hexString2ByteConsumer("",
                                 (b) -> fail("Empty input not detected"),
                                 (char) 0);
  }

  @Test(expectedExceptions = ParseException.class)
  public void testHexString2ByteSinkIllegalLength() throws ParseException
  {
    String in = "00f";
    AtomicInteger index = new AtomicInteger();
    DCCUtils.hexString2ByteConsumer(in,
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
    DCCUtils.hexString2ByteConsumer(in,
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
    DCCUtils.hexString2ByteConsumer(in,
                                 (b) -> fail("Illegal Input not detected"),
                                 (char) 'g');
  }

  @Test(expectedExceptions = NullPointerException.class)
  @SuppressWarnings("null")
  public void testHexString2ByteSinkNullCharsequence() throws ParseException
  {
    DCCUtils.hexString2ByteConsumer(null,
                                 (b) -> fail("Illegal Input not detected"),
                                 (char) 'g');
  }

  @Test(expectedExceptions = NullPointerException.class)
  @SuppressWarnings("null")
  public void testHexString2ByteSinkNullSink() throws ParseException
  {
    DCCUtils.hexString2ByteConsumer("ab",
                                 null,
                                 (char) 'g');
  }

  @Test(expectedExceptions = ParseException.class)
  public void testHexString2ByteSinkIllegalInterbyteChar() throws ParseException
  {
    AtomicInteger index = new AtomicInteger();
    DCCUtils.hexString2ByteConsumer("ab cD:EF",
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
    ByteBuffer result = DCCUtils.hexString2ByteBuffer("",
                                                   null,
                                                   (char) 0);
    result.clear();
    assertEquals(expected,
                 result);
    expected = ByteBuffer.wrap(toByteArray(0xaf));
    expected.clear();
    result = DCCUtils.hexString2ByteBuffer("af",
                                        null,
                                        (char) 0);
    result.clear();
    assertEquals(expected,
                 result);
    expected = ByteBuffer.wrap(toByteArray(0xaf,
                                           0xfe));
    expected.clear();
    result = DCCUtils.hexString2ByteBuffer("affe",
                                        null,
                                        (char) 0);
    result.clear();
    assertEquals(expected,
                 result);
    expected = ByteBuffer.wrap(toByteArray(0xca,
                                           0xfe,
                                           0xba));
    expected.clear();
    result = DCCUtils.hexString2ByteBuffer("cafeba",
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
    result = DCCUtils.hexString2ByteBuffer("cafebabe",
                                        null,
                                        (char) 0);
    result.clear();
    assertEquals(expected,
                 result);
    expected = ByteBuffer.allocate(0);
    result = DCCUtils.hexString2ByteBuffer("",
                                        null,
                                        ' ');
    result.clear();
    assertEquals(expected,
                 result);
    expected = ByteBuffer.wrap(toByteArray(0xaf));
    expected.clear();
    result = DCCUtils.hexString2ByteBuffer("af",
                                        null,
                                        ' ');
    result.clear();
    assertEquals(expected,
                 result);
    expected = ByteBuffer.wrap(toByteArray(0xaf,
                                           0xfe));
    expected.clear();
    result = DCCUtils.hexString2ByteBuffer("af fe",
                                        null,
                                        ' ');
    result.clear();
    assertEquals(expected,
                 result);
    expected = ByteBuffer.wrap(toByteArray(0xca,
                                           0xfe,
                                           0xba));
    expected.clear();
    result = DCCUtils.hexString2ByteBuffer("ca fe ba",
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
    result = DCCUtils.hexString2ByteBuffer("ca fe ba be",
                                        null,
                                        ' ');
    result.clear();
    assertEquals(expected,
                 result);
    expected = ByteBuffer.wrap(toByteArray(0xbe));
    expected.clear();
    result = DCCUtils.hexString2ByteBuffer("be ",
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
    result = DCCUtils.hexString2ByteBuffer("ca fe ba be ",
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
    DCCUtils.hexString2ByteBuffer("cafeba",
                               buffer,
                               (char) 0);
  }

  @Test
  public void testByte_n()
  {
    int i = 0x65432100;
    assertEquals((byte) 0,
                 DCCUtils.byte1(i));
    assertEquals((byte) 0x21,
                 DCCUtils.byte2(i));
    assertEquals((byte) 0x43,
                 DCCUtils.byte3(i));
    assertEquals((byte) 0x65,
                 DCCUtils.byte4(i));
    i = 0x00123456;
    assertEquals((byte) 0x56,
                 DCCUtils.byte1(i));
    assertEquals((byte) 0x34,
                 DCCUtils.byte2(i));
    assertEquals((byte) 0x12,
                 DCCUtils.byte3(i));
    assertEquals((byte) 0x00,
                 DCCUtils.byte4(i));
  }

  @Test
  public void testCrc8()
  {
    ByteBuffer buffer = ByteBuffer.wrap(new byte[]{(byte) 0x88, 0x00, 0x01});
    byte crc = DCCUtils.crc8((byte) 0xff,
                          buffer);
    byte expected = (byte) 0xcb;
    assertEquals(expected,
                 crc);
  }

  @Test
  public void testEncodeLongAddress()
  {
    int result = DCCUtils.encodeLongAddress(0);
    assertEquals(-1,
                 result);
    result = DCCUtils.encodeLongAddress(-1);
    assertEquals(-1,
                 result);
    result = DCCUtils.encodeLongAddress(DCCConstants.ADDRESS_SHORT_MAX);
    assertEquals(0xc07f,
                 result);
    result = DCCUtils.encodeLongAddress(DCCConstants.ADDRESS_SHORT_MAX + 1);
    assertEquals(0xc080,
                 result);
    result = DCCUtils.encodeLongAddress(255);
    assertEquals(0xc0ff,
                 result);
    result = DCCUtils.encodeLongAddress(256);
    assertEquals(0xc100,
                 result);
    result = DCCUtils.encodeLongAddress(DCCConstants.ADDRESS_MAX);
    assertEquals(0xe7ff,
                 result);
    result = DCCUtils.encodeLongAddress(DCCConstants.ADDRESS_MAX + 1);
    assertEquals(-1,
                 result);
  }

  @Test
  public void testEncodeCV17()
  {
    int result = DCCUtils.encodeCV17(0);
    assertEquals(-1,
                 result);
    result = DCCUtils.encodeCV17(-1);
    assertEquals(-1,
                 result);
    result = DCCUtils.encodeCV17(127);
    assertEquals(0xc0,
                 result);
    result = DCCUtils.encodeCV17(128);
    assertEquals(0xc0,
                 result);
    result = DCCUtils.encodeCV17(255);
    assertEquals(0xc0,
                 result);
    result = DCCUtils.encodeCV17(256);
    assertEquals(0xc1,
                 result);
    result = DCCUtils.encodeCV17(DCCConstants.ADDRESS_MAX);
    assertEquals(0xe7,
                 result);
    result = DCCUtils.encodeCV17(DCCConstants.ADDRESS_MAX + 1);
    assertEquals(-1,
                 result);
  }

  @Test
  public void testEncodeCV18()
  {
    int result = DCCUtils.encodeCV18(0);
    assertEquals(-1,
                 result);
    result = DCCUtils.encodeCV18(-1);
    assertEquals(-1,
                 result);
    result = DCCUtils.encodeCV18(DCCConstants.ADDRESS_SHORT_MAX);
    assertEquals(0x7f,
                 result);
    result = DCCUtils.encodeCV18(DCCConstants.ADDRESS_SHORT_MAX + 1);
    assertEquals(0x80,
                 result);
    result = DCCUtils.encodeCV18(255);
    assertEquals(0xff,
                 result);
    result = DCCUtils.encodeCV18(256);
    assertEquals(0x00,
                 result);
    result = DCCUtils.encodeCV18(DCCConstants.ADDRESS_MAX);
    assertEquals(0xff,
                 result);
    result = DCCUtils.encodeCV18(DCCConstants.ADDRESS_MAX + 1);
    assertEquals(-1,
                 result);
  }

  @Test
  public void testDecodeLongAddress()
  {
    int result = DCCUtils.decodeLongAddress(0,
                                         0);
    assertEquals(0,
                 result);
    result = DCCUtils.decodeLongAddress(256,
                                     0);
    assertEquals(-1,
                 result);
    result = DCCUtils.decodeLongAddress(0,
                                     256);
    assertEquals(-1,
                 result);
    result = DCCUtils.decodeLongAddress(0x80,
                                     0);
    assertEquals(0,
                 result);
    result = DCCUtils.decodeLongAddress(0x40,
                                     0);
    assertEquals(0,
                 result);
    result = DCCUtils.decodeLongAddress(0xc0,
                                     0x7f);
    assertEquals(127,
                 result);
    result = DCCUtils.decodeLongAddress(0xc0,
                                     0x3);
    assertEquals(3,
                 result);
    result = DCCUtils.decodeLongAddress(0xc0,
                                     128);
    assertEquals(128,
                 result);
    result = DCCUtils.decodeLongAddress(0xc0,
                                     0xff);
    assertEquals(255,
                 result);
    result = DCCUtils.decodeLongAddress(0xc1,
                                     0x00);
    assertEquals(256,
                 result);
    result = DCCUtils.decodeLongAddress(0xe7,
                                     0xff);
    assertEquals(10239,
                 result);
    result = DCCUtils.decodeLongAddress(0xe8,
                                     0);
    assertEquals(-1,
                 result);
    result = DCCUtils.decodeLongAddress(0xff,
                                     0xff);
    assertEquals(-1,
                 result);
  }

  @Test
  public void testRoundTripCV1718()
  {
    for (int i = 1; i < DCCConstants.ADDRESS_MAX + 1; ++i) {
      int cv17 = DCCUtils.encodeCV17(i);
      int cv18 = DCCUtils.encodeCV18(i);
      int result = DCCUtils.decodeLongAddress(cv17,
                                           cv18);
      assertEquals(i,
                   result);
    }
  }

  @Test
  public void testEncodeConsistsAddress()
  {
    int result = DCCUtils.encodeConsistAddress(-1,
                                            false);
    assertEquals(-1,
                 result);
    result = DCCUtils.encodeConsistAddress(-1,
                                        true);
    assertEquals(-1,
                 result);
    result = DCCUtils.encodeConsistAddress(0,
                                        false);
    assertEquals(0,
                 result);
    result = DCCUtils.encodeConsistAddress(0,
                                        true);
    assertEquals(0,
                 result);
    result = DCCUtils.encodeConsistAddress(1,
                                        false);
    assertEquals(1,
                 result);
    result = DCCUtils.encodeConsistAddress(1,
                                        true);
    assertEquals(129,
                 result);
    result = DCCUtils.encodeConsistAddress(99,
                                        false);
    assertEquals(99,
                 result);
    result = DCCUtils.encodeConsistAddress(99,
                                        true);
    assertEquals(99 + 0x80,
                 result);
    result = DCCUtils.encodeConsistAddress(100,
                                        false);
    assertEquals(100,
                 result);
    result = DCCUtils.encodeConsistAddress(100,
                                        true);
    assertEquals(100 + 0x80,
                 result);
    result = DCCUtils.encodeConsistAddress(127,
                                        false);
    assertEquals(127,
                 result);
    result = DCCUtils.encodeConsistAddress(127,
                                        true);
    assertEquals(127 + 0x80,
                 result);
    result = DCCUtils.encodeConsistAddress(128,
                                        false);
    assertEquals(0x0100 + 28,
                 result);
    result = DCCUtils.encodeConsistAddress(128,
                                        true);
    assertEquals(0x0180 + 28,
                 result);
    result = DCCUtils.encodeConsistAddress(199,
                                        true);
    assertEquals(0x0180 + 99,
                 result);
    result = DCCUtils.encodeConsistAddress(199,
                                        false);
    assertEquals(0x0100 + 99,
                 result);
    result = DCCUtils.encodeConsistAddress(DCCConstants.ADDRESS_MAX,
                                        false);
    assertEquals((102 << 8) + 39,
                 result);
    result = DCCUtils.encodeConsistAddress(DCCConstants.ADDRESS_MAX,
                                        true);
    assertEquals((102 << 8) + 39 + 0x80,
                 result);
    result = DCCUtils.encodeConsistAddress(DCCConstants.ADDRESS_MAX + 1,
                                        true);
    assertEquals(-1,
                 result);
    result = DCCUtils.encodeConsistAddress(DCCConstants.ADDRESS_MAX + 1,
                                        false);
    assertEquals(-1,
                 result);
  }

  @Test
  public void testEncodeCV19()
  {
    int result = DCCUtils.encodeCV19(-1,
                                  false);
    assertEquals(-1,
                 result);
    result = DCCUtils.encodeCV19(-1,
                              true);
    assertEquals(-1,
                 result);
    result = DCCUtils.encodeCV19(0,
                              false);
    assertEquals(0,
                 result);
    result = DCCUtils.encodeCV19(0,
                              true);
    assertEquals(0,
                 result);
    result = DCCUtils.encodeCV19(1,
                              false);
    assertEquals(1,
                 result);
    result = DCCUtils.encodeCV19(1,
                              true);
    assertEquals(129,
                 result);
    result = DCCUtils.encodeCV19(99,
                              false);
    assertEquals(99,
                 result);
    result = DCCUtils.encodeCV19(99,
                              true);
    assertEquals(99 + 0x80,
                 result);
    result = DCCUtils.encodeCV19(100,
                              false);
    assertEquals(100,
                 result);
    result = DCCUtils.encodeCV19(100,
                              true);
    assertEquals(100 + 0x80,
                 result);
    result = DCCUtils.encodeCV19(127,
                              false);
    assertEquals(127,
                 result);
    result = DCCUtils.encodeCV19(127,
                              true);
    assertEquals(127 + 0x80,
                 result);
    result = DCCUtils.encodeCV19(128,
                              false);
    assertEquals(28,
                 result);
    result = DCCUtils.encodeCV19(128,
                              true);
    assertEquals(0x80 + 28,
                 result);
    result = DCCUtils.encodeCV19(199,
                              true);
    assertEquals(0x80 + 99,
                 result);
    result = DCCUtils.encodeCV19(199,
                              false);
    assertEquals(99,
                 result);
    result = DCCUtils.encodeCV19(DCCConstants.ADDRESS_MAX,
                              false);
    assertEquals(39,
                 result);
    result = DCCUtils.encodeCV19(DCCConstants.ADDRESS_MAX,
                              true);
    assertEquals(39 + 0x80,
                 result);
    result = DCCUtils.encodeCV19(DCCConstants.ADDRESS_MAX + 1,
                              true);
    assertEquals(-1,
                 result);
    result = DCCUtils.encodeCV19(DCCConstants.ADDRESS_MAX + 1,
                              false);
    assertEquals(-1,
                 result);
  }

  @Test
  public void testEncodeCV20()
  {
    int result = DCCUtils.encodeCV20(-1);
    assertEquals(-1,
                 result);
    result = DCCUtils.encodeCV20(-1);
    assertEquals(-1,
                 result);
    result = DCCUtils.encodeCV20(0);
    assertEquals(0,
                 result);
    result = DCCUtils.encodeCV20(0);
    assertEquals(0,
                 result);
    result = DCCUtils.encodeCV20(1);
    assertEquals(0,
                 result);
    result = DCCUtils.encodeCV20(1);
    assertEquals(0,
                 result);
    result = DCCUtils.encodeCV20(99);
    assertEquals(0,
                 result);
    result = DCCUtils.encodeCV20(99);
    assertEquals(0,
                 result);
    result = DCCUtils.encodeCV20(100);
    assertEquals(0,
                 result);
    result = DCCUtils.encodeCV20(100);
    assertEquals(0,
                 result);
    result = DCCUtils.encodeCV20(127);
    assertEquals(0,
                 result);
    result = DCCUtils.encodeCV20(127);
    assertEquals(0,
                 result);
    result = DCCUtils.encodeCV20(128);
    assertEquals(1,
                 result);
    result = DCCUtils.encodeCV20(128);
    assertEquals(1,
                 result);
    result = DCCUtils.encodeCV20(199);
    assertEquals(1,
                 result);
    result = DCCUtils.encodeCV20(199);
    assertEquals(1,
                 result);
    result = DCCUtils.encodeCV20(DCCConstants.ADDRESS_MAX);
    assertEquals(102,
                 result);
    result = DCCUtils.encodeCV20(DCCConstants.ADDRESS_MAX);
    assertEquals(102,
                 result);
    result = DCCUtils.encodeCV20(DCCConstants.ADDRESS_MAX + 1);
    assertEquals(-1,
                 result);
    result = DCCUtils.encodeCV20(DCCConstants.ADDRESS_MAX + 1);
    assertEquals(-1,
                 result);
  }

  @Test
  public void testIsConsistsDirectionInverted()
  {
    boolean result = DCCUtils.isConsistsDirectionInverted(0);
    assertFalse(result);
    result = DCCUtils.isConsistsDirectionInverted(1);
    assertFalse(result);
    result = DCCUtils.isConsistsDirectionInverted(127);
    assertFalse(result);
    result = DCCUtils.isConsistsDirectionInverted(128);
    assertTrue(result);
    result = DCCUtils.isConsistsDirectionInverted(255);
    assertTrue(result);
  }

  @Test
  public void testDecodeConsistsAddress()
  {
    int result = DCCUtils.decodeConsistsAddress(0,
                                             0);
    assertEquals(-1,
                 result);
    result = DCCUtils.decodeConsistsAddress(128,
                                         0);
    assertEquals(-1,
                 result);
    result = DCCUtils.decodeConsistsAddress(1,
                                         0);
    assertEquals(1,
                 result);
    result = DCCUtils.decodeConsistsAddress(-1,
                                         -1);
    assertEquals(-1,
                 result);
    result = DCCUtils.decodeConsistsAddress(39,
                                         102);
    assertEquals(10239,
                 result);
    result = DCCUtils.decodeConsistsAddress(39 + 0x80,
                                         102);
    assertEquals(10239,
                 result);
    result = DCCUtils.decodeConsistsAddress(40,
                                         102);
    assertEquals(-1,
                 result);
  }

  @Test
  public void testRoundTripCV1920()
  {
    for (int i = 1; i < DCCConstants.ADDRESS_MAX + 1; ++i) {
      int cv19 = DCCUtils.encodeCV19(i,
                                  false);
      int cv20 = DCCUtils.encodeCV20(i);
      int result = DCCUtils.decodeConsistsAddress(cv19,
                                               cv20);
      assertEquals(i,
                   result);
      assertFalse(DCCUtils.isConsistsDirectionInverted(cv19));
    }
    for (int i = 1; i < DCCConstants.ADDRESS_MAX + 1; ++i) {
      int cv19 = DCCUtils.encodeCV19(i,
                                  true);
      int cv20 = DCCUtils.encodeCV20(i);
      int result = DCCUtils.decodeConsistsAddress(cv19,
                                               cv20);
      assertEquals(i,
                   result);
      assertTrue(DCCUtils.isConsistsDirectionInverted(cv19));
    }
  }

}
