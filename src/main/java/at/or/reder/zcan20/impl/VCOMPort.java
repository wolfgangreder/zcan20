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
package at.or.reder.zcan20.impl;

import at.or.reder.dcc.util.BufferPool;
import at.or.reder.dcc.util.BufferPool.BufferItem;
import at.or.reder.dcc.util.CanIdMatcher;
import at.or.reder.dcc.util.Utils;
import at.or.reder.zcan20.CanId;
import at.or.reder.zcan20.CommandGroup;
import at.or.reder.zcan20.CommandMode;
import at.or.reder.zcan20.packet.Packet;
import at.or.reder.zcan20.packet.Ping;
import gnu.io.PortInUseException;
import gnu.io.RXTXPort;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.UnsupportedCommOperationException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.util.Objects;
import java.util.TooManyListenersException;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedTransferQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TransferQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Wolfgang Reder
 */
@SuppressWarnings("ClassWithMultipleLoggers")
public final class VCOMPort implements ZPort
{

  private enum ReaderState
  {
    INIT,
    ZB_READ,
    TB_READ,
    DLC_READ,
    TE_READ;
  }
  public static final int BUFFER_SIZE = 1500;
  public static final Logger READ_LOGGER = Logger.getLogger("at.or.reder.zcan.zport.read");
  public static final Logger WRITE_LOGGER = Logger.getLogger("at.or.reder.zcan.zport.write");

  private final String portName;
  private final BufferPool bufferPool;
  private RXTXPort port;
  private WritableByteChannel out;
  private ReadableByteChannel in;
  private final ByteBuffer receiveBuffer = ByteBuffer.allocate(BUFFER_SIZE);
  private final TransferQueue<Packet> receiveQueue = new LinkedTransferQueue<>();
  private ReaderState readerState;

  public VCOMPort(String portName)
  {
    this.portName = portName;
    bufferPool = new BufferPool(BUFFER_SIZE,
                                Runtime.getRuntime().availableProcessors());
  }

  @Override
  public String getName()
  {
    return "VCOMPeer to MX10@" + portName;
  }

  @Override
  public void start() throws IOException
  {
    synchronized (this) {
      if (port == null) {
        try {
          RXTXPort p = new RXTXPort(portName);
          try {
            p.setSerialPortParams(38400,
                                  SerialPort.DATABITS_8,
                                  SerialPort.STOPBITS_1,
                                  SerialPort.PARITY_NONE);
            p.setFlowControlMode(SerialPort.FLOWCONTROL_RTSCTS_IN + SerialPort.FLOWCONTROL_RTSCTS_OUT);
            p.addEventListener(this::onSerialEvent);
            out = Channels.newChannel(port.getOutputStream());
            in = Channels.newChannel(port.getInputStream());
            port = p;
            p = null;
          } finally {
            if (p != null) {
              p.close();
            }
          }
        } catch (PortInUseException | UnsupportedCommOperationException | TooManyListenersException ex) {
          Utils.LOGGER.log(Level.SEVERE,
                           null,
                           ex);
          throw new IOException(ex);
        }
      }
    }
  }

  @Override
  public void close() throws IOException
  {
    synchronized (this) {
      if (port != null) {
        try {
          port.close();
        } finally {
          port = null;
          out = null;
          in = null;
        }
      }
    }
  }

  @Override
  public void sendPacket(Packet packet) throws IOException
  {
    Objects.requireNonNull(packet,
                           "packet is null");
    WRITE_LOGGER.log(Level.FINEST,
                     "Sending {0}",
                     packet.toString());

    try (BufferItem bufferItem = bufferPool.getBuffer()) {
      ByteBuffer buffer = bufferItem.getBuffer();
      int numBytes = VCOMMarshaller.marshalPacket(packet,
                                                  buffer);
      buffer.limit(numBytes);
      buffer.position(numBytes);
      synchronized (this) {
        if (out == null) {
          throw new IOException("port closed");
        }
        out.write(buffer);
      }
    }
  }

  @Override
  public void sendRaw(ByteBuffer buffer) throws IOException
  {
    Objects.requireNonNull(buffer,
                           "buffer is null");
    WRITE_LOGGER.log(Level.FINEST,
                     () -> {
                       StringBuilder builder = new StringBuilder("Sending raw ");
                       Utils.byteBuffer2HexString(buffer,
                                                  builder,
                                                  ' ');
                       return builder.toString();
                     });
    synchronized (this) {
      if (out == null) {
        throw new IOException("port closed");
      }
      out.write(buffer);
    }
  }

  private void onSerialEvent(SerialPortEvent event)
  {
    if (event.getEventType() == SerialPortEvent.DATA_AVAILABLE) {
      ByteBuffer bytesRead = ByteBuffer.allocate(128);
      while (true) {
        synchronized (this) {
          try {
            in.read(bytesRead);
          } catch (IOException ex) {
            Utils.LOGGER.log(Level.SEVERE,
                             null,
                             ex);
            return;
          }
        }
        if (!bytesRead.hasRemaining()) {
          return;
        }
        while (bytesRead.hasRemaining()) {
          byte b = bytesRead.get();
          switch (readerState) {
            case INIT:
              readerState = doInit(b);
              break;
            case ZB_READ:
              readerState = doZB_READ(b);
              break;
            case TB_READ:
              readerState = doTB_READ(b);
              break;
            case DLC_READ:
              readerState = doDLC_READ(b);
              break;
            case TE_READ:
              readerState = doTE_READ(b);
              break;
            default:
              throw new IllegalStateException("Unknown readerstate " + readerState);
          }
        }
        bytesRead.rewind();
      }
    }
  }

  private ReaderState resetReader()
  {
    receiveBuffer.limit(receiveBuffer.capacity());
    receiveBuffer.rewind();
    return ReaderState.INIT;
  }

  private boolean checkCRC()
  {
    ByteBuffer b = receiveBuffer.flip();
    b.limit(b.limit() - 2);
    short crc = Utils.crc16((short) 0,
                            b);
    short crcRead = receiveBuffer.getShort(receiveBuffer.limit() - 2);
    return crc == crcRead;
  }

  private ReaderState doInit(byte b)
  {
    if (b == 0x5a) {
      return ReaderState.ZB_READ;
    }
    return ReaderState.INIT;
  }

  private ReaderState doZB_READ(byte b)
  {
    if (b == 0x32) {
      return ReaderState.TB_READ;
    }
    return ReaderState.INIT;
  }

  private ReaderState doTB_READ(byte b)
  {
    receiveBuffer.limit(b + 6); // DLC + CMD + GRP + CRC
    return ReaderState.DLC_READ;
  }

  private ReaderState doDLC_READ(byte b)
  {
    if (receiveBuffer.hasRemaining()) {
      receiveBuffer.put(b);
      return ReaderState.DLC_READ;
    } else if (b == 0x32) {
      receiveBuffer.limit(receiveBuffer.position());
      if (checkCRC()) {
        return ReaderState.TE_READ;
      } else {
        READ_LOGGER.log(Level.WARNING,
                        () -> "CRC error :" + Utils.byteArray2HexString(receiveBuffer.array()));
        return resetReader();
      }
    } else {
      READ_LOGGER.log(Level.WARNING,
                      () -> "Framing error :" + Utils.byteArray2HexString(receiveBuffer.array()) + " byteTo append:0x" + Integer.
                      toHexString(b & 0xff));
      return resetReader();
    }
  }

  private ReaderState doTE_READ(byte b)
  {
    if (b == 0x5a) {
      receiveBuffer.rewind();
      try {
        Packet packet = VCOMMarshaller.unmarshalPacket(receiveBuffer);
        if (packet != null) {
          if (!receiveQueue.offer(packet)) {
            READ_LOGGER.log(Level.SEVERE,
                            "receiveQueue overrun");
          }
        }
      } catch (IOException ex) {
        READ_LOGGER.log(Level.SEVERE,
                        "unmarshalling error",
                        ex);
      }
    }
    return resetReader();
  }

  @Override
  public Packet readPacket() throws IOException
  {
    Packet result = null;
    try {
      result = receiveQueue.poll(5,
                                 TimeUnit.SECONDS);
    } catch (InterruptedException ex) {
      Thread.currentThread().interrupt();
    }
    if (result == null) {
      READ_LOGGER.log(Level.FINE,
                      "Packet Timeout");
    } else {
      READ_LOGGER.log(Level.FINEST,
                      "Reading {0}",
                      result.toString());
    }
    return result;
  }

  @Override
  public Future<Ping> sendInitPacket(ZCANImpl zcan) throws IOException
  {
    return zcan.doSendRaw(ByteBuffer.wrap(new byte[]{0x5a, 0x32, 0x32, 0x5a}),
                          new CanIdMatcher(CanId.valueOf(
                                  CommandGroup.NETWORK,
                                  CommandGroup.NETWORK_PING,
                                  CommandMode.EVENT,
                                  (short) 0),
                                           CanIdMatcher.MASK_NO_ADDRESS),
                          Ping.class
    );
  }

}
