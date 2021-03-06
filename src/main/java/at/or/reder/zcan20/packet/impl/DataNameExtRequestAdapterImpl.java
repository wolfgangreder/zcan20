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
package at.or.reder.zcan20.packet.impl;

import at.or.reder.zcan20.packet.DataNameExtRequestAdapter;
import at.or.reder.zcan20.packet.Packet;
import at.or.reder.dcc.util.DCCUtils;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;

/**
 *
 * @author Wolfgang Reder
 */
final class DataNameExtRequestAdapterImpl extends AbstractPacketAdapter implements DataNameExtRequestAdapter
{

//  @ServiceProvider(service = PacketAdapterFactory.class, path = Packet.LOOKUPPATH)
//  public static final class Factory implements PacketAdapterFactory
//  {
//
//    @Override
//    public boolean isValid(CommandGroup group,
//                           int command,
//                           CommandMode mode,
//                           int dlc)
//    {
//      return group == CommandGroup.DATA && (mode == CommandMode.REQUEST || mode == CommandMode.ACK) && command
//                                                                                                               == CommandGroup.DATA_NAME_EXT;
//    }
//
//    @Override
//    public DataNameExtRequestAdapter createAdapter(Packet packet)
//    {
//      return new DataNameExtRequestAdapterImpl(packet);
//    }
//
//  }
  private DataNameExtRequestAdapterImpl(Packet packet)
  {
    super(packet);
  }

  @Override
  public short getMasterNID()
  {
    return getPacket().getSenderNID();
  }

  @Override
  public short getObjectNID()
  {
    return buffer.getShort(0);
  }

  @Override
  public short getSubID()
  {
    return buffer.getShort(2);
  }

  @Override
  public int getVal1()
  {
    return buffer.getInt(4);
  }

  @Override
  public int getVal2()
  {
    return buffer.getInt(8);
  }

  @Override
  public String getText()
  {
    ByteBuffer tmp = ByteBuffer.allocate(buffer.remaining());
    buffer.mark();
    try {
      byte b;
      buffer.position(12);
      while (buffer.hasRemaining() && (b = buffer.get()) != 0) {
        tmp.put(b);
      }
    } finally {
      buffer.reset();
    }
    tmp.limit(tmp.position());
    tmp.rewind();
    CharBuffer charBuffer = Charset.forName("CP850").decode(tmp);
    return charBuffer.toString();
  }

  @Override
  public String toString()
  {
    StringBuilder builder = new StringBuilder("NAME_EXT(0x");
    DCCUtils.appendHexString(getMasterNID() & 0xffff,
                          builder,
                          4);
    builder.append(", 0x");
    DCCUtils.appendHexString(getObjectNID() & 0xffff,
                          builder,
                          4);
    builder.append(", 0x");
    DCCUtils.appendHexString(getSubID() & 0xffff,
                          builder,
                          4);
    builder.append(", 0x");
    DCCUtils.appendHexString(getVal1(),
                          builder,
                          8);
    builder.append(", 0x");
    DCCUtils.appendHexString(getVal2(),
                          builder,
                          8);
    builder.append(", \"");
    builder.append(getText());
    return builder.append("\")").toString();
  }

}
