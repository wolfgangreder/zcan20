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
package at.or.reder.zcan20.util;

import at.or.reder.zcan20.packet.Packet;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.validation.constraints.NotNull;

/**
 *
 * @author Wolfgang Reder
 */
public final class Utils
{

  public static final Logger LOGGER = Logger.getLogger("at.or.reder.zcan20");

  public static byte byte1(int i)
  {
    return (byte) (i & 0xff);
  }

  public static byte byte2(int i)
  {
    return (byte) ((i & 0xff00) >> 8);
  }

  public static byte byte3(int i)
  {
    return (byte) ((i & 0xff0000) >> 16);
  }

  public static byte byte4(int i)
  {
    return (byte) ((i & 0xff000000) >> 24);
  }

  public static short short1(int i)
  {
    return (short) ((i & 0xffff));
  }

  public static short short2(int i)
  {
    return (short) ((i & 0xffff0000) >> 16);
  }

  public static byte[] toByteArray(int... in)
  {
    byte[] result = new byte[in.length];
    for (int i = 0; i < in.length; i++) {
      result[i] = (byte) in[i];
    }
    return result;
  }

  /**
   * Allocates a ByteBuffer with byteorder {@code ByteOrder.LITTLE_ENDIAN}.
   *
   * @param size size of buffer
   * @return a Buffer with byteorder {@code ByteOrder.LITTLE_ENDIAN}
   */
  public static ByteBuffer allocateLEBuffer(int size)
  {
    ByteBuffer result = ByteBuffer.allocate(size);
    result.order(ByteOrder.LITTLE_ENDIAN);
    return result;
  }

  /**
   * Converts {@code pValue} to hex and appends the result to {@code builder}.
   *
   * @param pValue The Integer to convert
   * @param builder The Builder to append the result to.
   * @return {@code builder}
   * @see #appendHexString(int, java.lang.StringBuilder, int)
   */
  public static StringBuilder appendHexString(int pValue,
                                              StringBuilder builder)
  {
    return appendHexString(pValue,
                           builder,
                           -1);
  }

  /**
   * Converts {@code pValue} to hex and appends the result to {@code builder}. If necessary leading zeros will be added to append
   * at least {@code minimumDigits} to {@code builder}.
   *
   * @param pValue The Integer to convert.
   * @param builder The Buidler to append the result to.
   * @param minimumDigits miminumDigits to append, or {@code -1} to append only the mimimal requried digits.
   * @return {@code builder}
   */
  public static StringBuilder appendHexString(int pValue,
                                              StringBuilder builder,
                                              int minimumDigits)
  {
    String tmp = Integer.toHexString(pValue);
    for (int i = tmp.length(); i < minimumDigits; ++i) {
      builder.append('0');
    }
    return builder.append(tmp);
  }

  private static final class InterfaceItem implements Comparable<InterfaceItem>
  {

    private final InterfaceAddress address;
    private final boolean isSubIterface;

    public InterfaceItem(InterfaceAddress address,
                         boolean isSubIterface)
    {
      this.address = address;
      this.isSubIterface = isSubIterface;
    }

    public InterfaceAddress getAddress()
    {
      return address;
    }

    public boolean isIsSubIterface()
    {
      return isSubIterface;
    }

    @Override
    public int hashCode()
    {
      int hash = 3;
      hash = 79 * hash + Objects.hashCode(this.address);
      hash = 79 * hash + (this.isSubIterface ? 1 : 0);
      return hash;
    }

    @Override
    public boolean equals(Object obj)
    {
      if (this == obj) {
        return true;
      }
      if (obj == null) {
        return false;
      }
      if (getClass() != obj.getClass()) {
        return false;
      }
      final InterfaceItem other = (InterfaceItem) obj;
      if (this.isSubIterface != other.isSubIterface) {
        return false;
      }
      return Objects.equals(this.address,
                            other.address);
    }

    @Override
    public int compareTo(InterfaceItem o)
    {
      if (o == this) {
        return 0;
      }
      int result = Boolean.compare(isSubIterface,
                                   o.isSubIterface);
      if (result == 0) {
        result = -Short.compare(address.getNetworkPrefixLength(),
                                o.address.getNetworkPrefixLength());
        if (result == 0) {
          byte[] myAddress = address.getAddress().
                  getAddress();
          byte[] otherAddress = o.address.getAddress().
                  getAddress();
          result = Integer.compare(myAddress.length,
                                   otherAddress.length);
          if (result == 0) {
            for (int i = 0; i < myAddress.length && result == 0; ++i) {
              result = Byte.compare(myAddress[i],
                                    otherAddress[i]);
            }
          }
        }
      }
      return result;
    }

    @Override
    public String toString()
    {
      return "InterfaceItem{" + "address=" + address + ", isSubIterface=" + isSubIterface + '}';
    }

  }

  /**
   * Collects all available local network addresses.
   *
   * @return List of addresses, or empty if no network available
   * @throws java.net.SocketException on error
   */
  public static List<InterfaceAddress> getAllInterfaceAddresses() throws SocketException
  {
    Enumeration<NetworkInterface> e = NetworkInterface.getNetworkInterfaces();
    List<InterfaceItem> result = new ArrayList<>();
    while (e.hasMoreElements()) {
      NetworkInterface master = e.nextElement();
      master.getInterfaceAddresses().
              stream().
              map((i) -> new InterfaceItem(i,
                                           false)).
              forEach(result::add);
      Enumeration<NetworkInterface> sub = master.getSubInterfaces();
      while (sub.hasMoreElements()) {
        sub.nextElement().
                getInterfaceAddresses().
                stream().
                map((i) -> new InterfaceItem(i,
                                             true)).
                forEach(result::add);
      }
    }
    result.sort(InterfaceItem::compareTo);
    return result.stream().
            map((i) -> i.getAddress()).
            collect(Collectors.toList());
  }

  /**
   * Prüft ob die beiden Addresse <code>addr1</code> und <code>addr2</code> im dur <code>subnetRange</code> definierten Subnet
   * liegen.
   *
   * @param addr1 erste Adresse
   * @param addr2 zweite Adresse
   * @param subnetRange breite des Subnets in bits
   * @return true falls die beiden Adresse im gleichen Subnet liegen.
   * @throws IllegalArgumentException falls <code>addr1</code> oder <code>addr2</code> gleich <code>null</code>, * * oder
   * <code>subnetRange&lt;0</code>
   */
  public static boolean matchesSubnet(InetAddress addr1,
                                      InetAddress addr2,
                                      short subnetRange) throws IllegalArgumentException
  {
    if (addr1 == null) {
      throw new IllegalArgumentException("addr1==null");
    }
    if (addr2 == null) {
      throw new IllegalArgumentException("addr2==null");
    }
    if (subnetRange < 0) {
      throw new IllegalArgumentException("subnetRange<0");
    }
    byte[] a1 = addr1.getAddress();
    byte[] a2 = addr2.getAddress();
    if (a1.length != a2.length) {
      return false;
    }
    boolean equals = Arrays.equals(a1,
                                   a2);
    if (equals) {
      return true;
    }
    if (subnetRange == 0 || subnetRange >= a1.length * 8) {
      return equals;
    }
    byte[] mask = createMask(subnetRange);
    equals = true;
    for (int i = 0; equals && i < mask.length; ++i) {
      equals = (a1[i] & mask[i]) == (a2[i] & mask[i]);
    }
    return equals;
  }

  private static byte[] createMask(short bitCount)
  {
    int maskLen = bitCount / 8;
    if (bitCount % 8 != 0) {
      ++maskLen;
    }
    byte[] result = new byte[maskLen];
    Arrays.fill(result,
                ((byte) 0xff));
    switch (bitCount % 8) {
      case 7:
        result[maskLen - 1] = (byte) 0xfe;
        break;
      case 6:
        result[maskLen - 1] = (byte) 0xfc;
        break;
      case 5:
        result[maskLen - 1] = (byte) 0xf8;
        break;
      case 4:
        result[maskLen - 1] = (byte) 0xf0;
        break;
      case 3:
        result[maskLen - 1] = (byte) 0xe0;
        break;
      case 2:
        result[maskLen - 1] = (byte) 0xc0;
        break;
      case 1:
        result[maskLen - 1] = (byte) 0x80;
        break;
    }
    return result;
  }

  /**
   * Converts the array {@code value} to a hex string. Each byte is 2 digits wide.
   *
   * @param value the array
   * @return a String with the length {@code 2*value.length}
   * @see #byteBuffer2HexString(java.nio.ByteBuffer, java.lang.StringBuilder, char)
   * @see #byteArray2HexString(@javax.validation.constraints.NotNull byte[], int, int)
   */
  public static String byteArray2HexString(@NotNull byte[] value)
  {
    Objects.requireNonNull(value,
                           "value is null");
    return byteArray2HexString(value,
                               0,
                               value.length);
  }

  /**
   * Converts the array {@code value} beginnig with {@code offset} to a hex string. Each byte is 2 digits wide. if
   * {@code value.length<=offset+len} a {@link java.lang.IndexOutOfBoundsException} is thrown.
   *
   * @param value the array
   * @param offset beginning offset
   * @param len number bytes to convert
   * @return a String with the length {@code 2*len}
   * @see #byteBuffer2HexString(java.nio.ByteBuffer, java.lang.StringBuilder, char)
   * @see #byteArray2HexString(@javax.validation.constraints.NotNull byte[])
   */
  public static String byteArray2HexString(@NotNull byte[] value,
                                           int offset,
                                           int len)
  {
    Objects.requireNonNull(value,
                           "value is null");
    if (value.length <= offset + len) {
      throw new IndexOutOfBoundsException();
    }
    ByteBuffer buffer = ByteBuffer.wrap(value,
                                        offset,
                                        len);
    return byteBuffer2HexString(buffer,
                                null,
                                (char) 0).
            toString();
  }

  /**
   * Converts the ByteBuffer {@code buffer} to a hex string and appends the result to {@code builder}. Each byte is 2 digits wide.
   * If {@code builder} is {@code null} a new {@link java.lang.StringBuilder} is created. If {@code interByteChar} is {@code !=0}
   * this character is appended between each byte.
   *
   * @param buffer The data to convert
   * @param builder The builder to append the result to.
   * @param interByteChar if {@code !=0} this character is appended between each byte.
   * @return if {@code builder!=null builder} or the created {@link java.lang.StringBuilder}
   */
  public static StringBuilder byteBuffer2HexString(@NotNull ByteBuffer buffer,
                                                   StringBuilder builder,
                                                   char interByteChar)
  {
    Objects.requireNonNull(buffer,
                           "input is null");
    StringBuilder result;
    if (builder == null) {
      result = new StringBuilder();
    } else {
      result = builder;
    }
    ByteBuffer tmp = buffer.slice();
    boolean oneAdded = tmp.hasRemaining();
    while (tmp.hasRemaining()) {
      String s = Integer.toHexString(tmp.get() & 0xff);
      if (s.length() == 1) {
        result.append("0");
      }
      result.append(s);
      if (interByteChar != 0) {
        result.append(interByteChar);
      }
    }
    if (oneAdded && interByteChar != 0) {
      result.setLength(result.length() - 1); // remove last interByteChar
    }
    return result;
  }

  private static int convertHexChar(char ch,
                                    int position) throws ParseException
  {
    if (ch >= '0' && ch <= '9') {
      return ch - '0';
    } else if (ch >= 'A' && ch <= 'F') {
      return ch - 'A' + 10;
    } else if (ch >= 'a' && ch <= 'f') {
      return ch - 'a' + 10;
    } else {
      throw new ParseException("Invalid character " + ch,
                               position);
    }
  }

  /**
   * Helper class to mask IOException in ByteSink.
   */
  private static final class IOExceptionWrapper extends Error
  {

    public IOExceptionWrapper(IOException e)
    {
      super(e);
    }

    @Override
    public IOException getCause()
    {
      return (IOException) super.getCause();
    }

  }

  /**
   * Convert the hex string {@code seq} to a {@link java.io.ByteArrayOutputStream}.
   *
   * @param seq Input hex string
   * @param out Output stream
   * @param interByteChar Optional character to separate bytes. If {@code \u0000} no separator char is expected.
   * @throws ParseException if the input cannot be parsed.
   * @see com.reder.zcan20.util.Utils#hexString2ByteConsumer(java.lang.CharSequence, com.reder.zcan20.util.ByteConsumer, char)
   */
  public static void hexString2OutputStream(@NotNull CharSequence seq,
                                            @NotNull ByteArrayOutputStream out,
                                            char interByteChar) throws ParseException
  {
    hexString2ByteConsumer(seq,
                           (b) -> out.write(b & 0xff),
                           interByteChar);
  }

  /**
   * Convert the hex string {@code seq} to a {@link java.io.OutputStream}.
   *
   * @param seq Input hex string
   * @param out Output stream
   * @param interByteChar Optional character to separate bytes. If {@code \u0000} no separator char is expected.
   * @throws IOException forwared from {@link java.io.OutputStream#write}
   * @throws ParseException if the input cannot be parsed.
   * @see com.reder.zcan20.util.Utils#hexString2ByteConsumer(java.lang.CharSequence, com.reder.zcan20.util.ByteConsumer, char)
   */
  public static void hexString2OutputStream(@NotNull CharSequence seq,
                                            @NotNull OutputStream out,
                                            char interByteChar) throws IOException, ParseException
  {
    try {
      hexString2ByteConsumer(seq,
                             (b) -> {
                               try {
                                 out.write(b & 0xff);
                               } catch (IOException ex) {
                                 throw new IOExceptionWrapper(ex);
                               }
                             },
                             interByteChar);
    } catch (IOExceptionWrapper e) {
      throw e.getCause();
    }
  }

  /**
   * Convert the hex string {@code seq} to a {@link java.nio.ByteBuffer}. If {@code buffer} is null, a {@link java.nio.ByteBuffer}
   * is allocated and returned. Otherwise {@code buffer} is returned. If {@code buffer.remaining()} is too small to hold all data,
   * {@link java.nio.BufferOverflowException} is thrown.
   *
   * @param seq Input hex string
   * @param buffer Outputbuffer. If {@code null} a indirect ByteBuffer will be allocated.
   * @param interByteChar Optional character to separate bytes. If {@code \u0000} no separator char is expected.
   * @return The buffer containing the data.
   * @throws ParseException if the input cannot be parsed.
   * @see java.nio.ByteBuffer#allocate(int)
   * @see com.reder.zcan20.util.Utils#hexString2ByteConsumer(java.lang.CharSequence, com.reder.zcan20.util.ByteConsumer, char)
   */
  public static ByteBuffer hexString2ByteBuffer(@NotNull CharSequence seq,
                                                ByteBuffer buffer,
                                                char interByteChar) throws ParseException
  {
    Objects.requireNonNull(seq,
                           "Input is null");
    ByteBuffer result;
    if (buffer != null) {
      result = buffer;
    } else if (seq.length() == 0) {
      return ByteBuffer.allocate(0);
    } else if (interByteChar != 0) {
      int len = seq.length() / 3 + 1;
      if (seq.charAt(seq.length() - 1) == interByteChar) {
        len--;
      }
      result = ByteBuffer.allocate(len);
    } else {
      result = ByteBuffer.allocate(seq.length() / 2);
    }
    hexString2ByteConsumer(seq,
                           result::put,
                           interByteChar);
    return result;
  }

  /**
   * Convert the input {@code seq} to bytes. For each converted byte {@code byteConsumer} is called.
   *
   * @param seq Input sequence
   * @param byteConsumer Consumer of data
   * @param interByteChar Optional character to separate bytes. If {@code \u0000} no separator char is expected. A trailing
   * separator char is allowed.
   * @throws ParseException if the input cannot be parsed.
   */
  public static void hexString2ByteConsumer(@NotNull CharSequence seq,
                                            @NotNull ByteConsumer byteConsumer,
                                            char interByteChar) throws ParseException
  {
    Objects.requireNonNull(seq,
                           "Input sequence is null");
    Objects.requireNonNull(byteConsumer,
                           "Byteconsumer is null");
    int i = 0;
    while (i < seq.length()) {
      int val = convertHexChar(seq.charAt(i),
                               i);
      ++i;
      if (i < seq.length()) {
        val = (val << 4) + convertHexChar(seq.charAt(i),
                                          i);
      } else {
        throw new ParseException("Invalid hexstring length",
                                 i);
      }
      ++i;
      byteConsumer.consumeByte((byte) val);
      if (interByteChar != 0) {
        if (i < seq.length()) {
          char ib = seq.charAt(i++);
          if (ib != interByteChar) {
            throw new ParseException("Invalid interbyte char " + ib,
                                     i - 1);
          }
        }
      }
    }
  }

  public static String packetToString(@NotNull Packet packet)
  {
    StringBuilder tmp = new StringBuilder();
    tmp.append("From ");
    tmp.append(Integer.toHexString(packet.getSenderNID() & 0xffff));
    tmp.append(": ");
    tmp.append("0x");
    tmp.append(Integer.toHexString(packet.getCommandGroup().
            getMagic()));
    tmp.append(", 0x");
    tmp.append(Integer.toHexString(packet.getCommand()));
    tmp.append(", ");
    tmp.append(packet.getCommandMode().
            name());
    tmp.append(" 0b");
    tmp.append(Integer.toBinaryString(packet.getCommandMode().getMagic() & 0x03));
    tmp.append(", ");
    ByteBuffer data = packet.getData();
    tmp.append(data.capacity());
    if (data.capacity() > 0) {
      tmp.append(", ");
      for (int i = 0; i < data.capacity(); ++i) {
        Utils.appendHexString(data.get(i) & 0xff,
                              tmp,
                              2);
        tmp.append(' ');
      }
    }
    return tmp.toString();
  }

  public static <C> Set<C> unmodifiableSetOf(C c1,
                                             C c2)
  {
    return Collections.unmodifiableSet(new HashSet<>(Arrays.asList(c1,
                                                                   c2)));
  }

  public static <C> Set<C> unmodifiableSetOf(C c1,
                                             C c2,
                                             C c3)
  {
    return Collections.unmodifiableSet(new HashSet<>(Arrays.asList(c1,
                                                                   c2,
                                                                   c3)));
  }

  public static <C> Set<C> unmodifiableSetOf(C c1,
                                             C c2,
                                             C c3,
                                             C c4)
  {
    return Collections.unmodifiableSet(new HashSet<>(Arrays.asList(c1,
                                                                   c2,
                                                                   c3,
                                                                   c4)));
  }

  private static OSType osType;

  public static synchronized OSType getOSType()
  {
    if (osType == null) {
      String osName = System.getProperty("os.name");
      if (osName.startsWith("Linux")) {
        osType = OSType.LINUX;
      } else if (osName.startsWith("Windows")) {
        osType = OSType.WINDOWS;
      } else {
        osType = OSType.UNKNOWN;
      }
    }
    return osType;
  }

  public static boolean isWindows()
  {
    return getOSType() == OSType.WINDOWS;
  }

  public static boolean isLinux()
  {
    return getOSType() == OSType.LINUX;
  }

  private Utils()
  {
  }

}
