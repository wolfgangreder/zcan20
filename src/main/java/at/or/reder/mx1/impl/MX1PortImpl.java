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

import at.or.reder.dcc.util.Utils;
import at.or.reder.mx1.MX1Command;
import at.or.reder.mx1.MX1Packet;
import at.or.reder.mx1.MX1PacketFlags;
import at.or.reder.mx1.MX1Port;
import at.or.reder.zcan20.util.Counter;
import at.or.reder.zcan20.util.CounterInputStream;
import at.or.reder.zcan20.util.CounterOutputStream;
import at.or.reder.zcan20.util.IORunnable;
import gnu.io.PortInUseException;
import gnu.io.RXTXPort;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.UnsupportedCommOperationException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Objects;
import java.util.Set;
import java.util.TooManyListenersException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.validation.constraints.NotNull;
import org.openide.util.NbBundle.Messages;

@Messages({
  "# {0} - portName",
  "# {1} - baud",
  "# {2} - dataBits",
  "# {3} - stopBits",
  "# {4} - parity",
  "MX1PortImpl_settings=Port {0} opened with {1,number,0} baud, {2} data-bits, {3} stop-bits, {4} parity"})
@SuppressWarnings("ClassWithMultipleLoggers")
final class MX1PortImpl implements MX1Port
{

  private static enum State
  {
    INIT,
    ST_SOH,
    ST_DATA,
    ST_ESCAPE_START,
    ST_ESCAPED_DATA,
    ERROR;
  }
  public static final Logger LOGGER = Logger.getLogger("at.or.reder.mx1.port");
  public static final Logger READ_LOGGER = Logger.getLogger("at.or.reder.mx1.port.read");
  public static final Logger WRITE_LOGGER = Logger.getLogger("at.or.reder.mx1.port.write");
  public static final byte SOH = (byte) 0x01;
  public static final byte EOT = (byte) 0x17;
  public static final byte DLE = (byte) 0x10;
  public static final int BUFFER_SIZE = 1500;
  public static final int FRAMING_SIZE = 3;
  public static final short CRC_INIT = (short) 0xffff;
  private static final AtomicInteger threadCounter = new AtomicInteger();
  private static final Executor eventDispatcher = Executors.newCachedThreadPool(MX1PortImpl::createEventThread);
  private final String portName;
  private SerialPort port;
  private CounterOutputStream out;
  private CounterInputStream in;
  private final AtomicLong rxPacketCounter = new AtomicLong();
  private final AtomicLong txPacketCounter = new AtomicLong();
  private Consumer<MX1Packet> packetConsumer;
  private final ByteBuffer inBuffer;
  private final Object writeGate = new Object();
  private State readerState = State.INIT;

  public MX1PortImpl(@NotNull String port)
  {
    this.portName = Objects.requireNonNull(port,
                                           "port is null");
    inBuffer = Utils.allocateBEBuffer(BUFFER_SIZE);
  }

  private static Thread createEventThread(Runnable run)
  {
    Thread result = new Thread(run,
                               "MX1PortImpl-event-dispatcher" + threadCounter.incrementAndGet());
    result.setDaemon(true);
    return result;
  }

  @Override
  public void open() throws IOException
  {
    synchronized (this) {
      if (port == null) {
        try {
          readerState = State.INIT;
          LOGGER.log(Level.INFO,
                     () -> "Try to open serial Port " + portName);
          SerialPort p = new RXTXPort(portName);
          try {
            p.setSerialPortParams(9600,
                                  SerialPort.DATABITS_8,
                                  SerialPort.STOPBITS_1,
                                  SerialPort.PARITY_NONE);
            p.setFlowControlMode(SerialPort.FLOWCONTROL_RTSCTS_IN + SerialPort.FLOWCONTROL_RTSCTS_OUT);
            port = p;
            out = new CounterOutputStream(port.getOutputStream());
            in = new CounterInputStream(port.getInputStream());
            p.enableReceiveTimeout(100);
            try {
              p.setLowLatency();
            } catch (Throwable ex) {
            }
            p.addEventListener(this::onSerialEvent);
            p.notifyOnCTS(true);
            p.notifyOnDataAvailable(true);
            logSerialSettings(p);
            p = null;
            port.setDTR(true);
            port.setRTS(true);
          } finally {
            if (p != null) {
              port = null;
              p.close();
            }
          }
        } catch (UnsupportedCommOperationException | TooManyListenersException | PortInUseException ex) {
          Utils.LOGGER.log(Level.SEVERE,
                           null,
                           ex);
          throw new IOException(ex);
        }
      }
    }
  }

  private void logSerialSettings(SerialPort p)
  {
    if (LOGGER.isLoggable(Level.CONFIG)) {
      String parity;
      switch (p.getParity()) {
        case SerialPort.PARITY_EVEN:
          parity = "even";
          break;
        case SerialPort.PARITY_MARK:
          parity = "mark ";
          break;
        case SerialPort.PARITY_NONE:
          parity = "none";
          break;
        case SerialPort.PARITY_ODD:
          parity = "odd";
          break;
        case SerialPort.PARITY_SPACE:
          parity = "space";
          break;
        default:
          parity = "unknown";
      }
      String msg = Bundle.MX1PortImpl_settings(p.getName(),
                                               p.getBaudRate(),
                                               p.getDataBits(),
                                               p.getStopBits(),
                                               parity);
      LOGGER.log(Level.CONFIG,
                 msg);
    }
  }

  @Override
  public long getBytesSent()
  {
    Counter counter;
    synchronized (this) {
      counter = out;
    }
    return counter != null ? counter.getCounter() : 0;
  }

  @Override
  public long getBytesReceived()
  {
    Counter counter;
    synchronized (this) {
      counter = in;
    }
    return counter != null ? counter.getCounter() : 0;
  }

  @Override
  public long getPacketsSent()
  {
    return txPacketCounter.get();
  }

  @Override
  public long getPacketsReceived()
  {
    return rxPacketCounter.get();
  }

  static int calculateNeededBufSize(ByteBuffer in)
  {
    int result = 0;
    ByteBuffer slice = in.duplicate();
    while (slice.hasRemaining()) {
      byte b = slice.get();
      switch (b) {
        case SOH:
        case EOT:
        case DLE:
          result += 2;
          break;
        default:
          ++result;
      }
    }
    return result;
  }

  static int escapeBuffer(ByteBuffer in,
                          ByteBuffer out)
  {
    if (out == null || out.remaining() < in.remaining()) {
      return calculateNeededBufSize(in);
    }
    ByteBuffer slice = in.duplicate();
    ByteBuffer outSlice = out.duplicate();
    int pos = out.position();
    while (slice.hasRemaining()) {
      if (!outSlice.hasRemaining()) {
        return calculateNeededBufSize(in);
      }
      byte b = slice.get();
      switch (b) {
        case SOH:
        case EOT:
        case DLE:
          if ((outSlice.limit() - outSlice.position()) < 2) {
            return calculateNeededBufSize(in);
          }
          outSlice.put(DLE);
          outSlice.put((byte) (b ^ 0x20));
          break;
        default:
          outSlice.put(b);
      }
    }
    return outSlice.position() - pos;
  }

  private ByteBuffer marshalPacket(MX1Packet packet)
  {
    boolean longFrame = packet.isLongFrame();
    ByteBuffer buffer = Utils.allocateBEBuffer(packet.getDataLength()
                                               + 3// sequence,command,flags
                                               + (longFrame ? 2 : 1));
    buffer.put((byte) packet.getSequence());
    buffer.put(MX1PacketFlags.toBits(packet.getFlags()));
    buffer.put((byte) packet.getCommand().getCmd());
    buffer.put(packet.getData());
    if (longFrame) {
      buffer.putShort(Utils.crc16((short) CRC_INIT,
                                  buffer.slice(0,
                                               buffer.position())));
    } else {
      buffer.put(Utils.crc8((byte) CRC_INIT,
                            buffer.slice(0,
                                         buffer.position())));
    }
    return buffer.rewind();
  }

  @Override
  public void sendPacket(MX1Packet packet) throws IOException
  {
    ByteBuffer buffer = marshalPacket(Objects.requireNonNull(packet,
                                                             "packet is null"));
    ByteBuffer outBuffer = ByteBuffer.allocate(buffer.remaining() * 2);
    int adv = escapeBuffer(buffer,
                           outBuffer != null ? outBuffer.duplicate().position(2) : null);
    if (outBuffer == null || (adv + FRAMING_SIZE) > outBuffer.limit()) {
      outBuffer = Utils.allocateBEBuffer(adv + FRAMING_SIZE);
      adv = escapeBuffer(buffer,
                         outBuffer.duplicate().position(2));
    }
    outBuffer.clear();
    outBuffer.put(0,
                  SOH);
    outBuffer.put(1,
                  SOH);
    outBuffer.position(adv + 2);
    outBuffer.put(EOT);
    outBuffer.limit(outBuffer.position());
    outBuffer.rewind();
    WRITE_LOGGER.log(Level.FINER,
                     () -> "Sending packet " + packet.toString());
    ByteBuffer param = outBuffer;
    if (!checkCTS(() -> writeBuffer(param))) {
      throw new IOException("CTS set");
    }
  }

  private void writeBuffer(ByteBuffer buffer) throws IOException
  {
    WRITE_LOGGER.log(Level.FINEST,
                     () -> "Sending data " + Utils.byteBuffer2HexString(buffer,
                                                                        null,
                                                                        ' ').toString());
    out.write(buffer.array(),
              buffer.position(),
              buffer.remaining());
    txPacketCounter.incrementAndGet();
  }

  @Override
  public synchronized Consumer<MX1Packet> getPacketListener()
  {
    return packetConsumer;
  }

  @Override
  public synchronized void setPacketListener(Consumer<MX1Packet> consumer)
  {
    this.packetConsumer = consumer;
  }

  @Override
  public void close() throws IOException
  {
    synchronized (this) {
      if (port != null) {
        try {
          LOGGER.log(Level.INFO,
                     () -> "Closing port " + portName);
          out.close();
          in.close();
          port.close();
        } finally {
          port = null;
        }
      }
    }
    synchronized (writeGate) {
      writeGate.notifyAll();
    }
  }

  @Override
  public String toString()
  {
    return "COMPeer to MX1@" + portName;
  }

  private State processByte(State stateIn,
                            byte b)
  {
    State result = null;
    switch (stateIn) {
      case INIT:
        result = doInit(b);
        break;
      case ST_SOH:
        result = doSOH(b);
        break;
      case ST_DATA:
      case ST_ESCAPED_DATA:
        result = doData(b,
                        stateIn);
        break;
      case ST_ESCAPE_START:
        result = doEscapeStart(b);
        break;
      case ERROR:
        result = doError(b);
        break;
      default:
        throw new IllegalStateException("Unknown readerstate " + readerState);
    }
    return result;
  }

  private boolean checkCTS(IORunnable runnable) throws IOException
  {
    synchronized (writeGate) {
      while (port != null && !port.isCTS()) {
        LOGGER.log(Level.INFO,
                   "Waiting for CTS");
        try {
          writeGate.wait(1000);
        } catch (InterruptedException ex) {
          LOGGER.log(Level.INFO,
                     ex,
                     () -> "checkCTS");
          return false;
        }
      }
      if (port != null) {
        runnable.run();
      }
    }
    return true;
  }

  private void onSerialEvent(SerialPortEvent evt)
  {
    if (evt.getEventType() == SerialPortEvent.CTS) {
      LOGGER.log(Level.INFO,
                 "CTS Event");
      synchronized (writeGate) {
        writeGate.notifyAll();
      }
    } else if (evt.getEventType() == SerialPortEvent.DATA_AVAILABLE) {
      try {
        byte[] buffer = new byte[1024];
        int read = in.read(buffer,
                           0,
                           buffer.length);
        processBuffer(buffer,
                      read);
      } catch (IOException ex) {
        LOGGER.log(Level.SEVERE,
                   "rx data",
                   ex);
      }
    }
  }

  private void processBuffer(byte[] buffer,
                             int read)
  {
    synchronized (this) {
      for (int i = 0; i < read; ++i) {
        readerState = processByte(readerState,
                                  buffer[i]);
      }
    }
  }

  private State doInit(byte b)
  {
    if (b == SOH) {
      return State.ST_SOH;
    }
    return readerState;
  }

  private State doSOH(byte b)
  {
    if (b == SOH) {
      return State.ST_DATA;
    }
    return readerState;
  }

  private State checkEscape(byte b,
                            State out)
  {
    if (readerState != State.ST_ESCAPE_START && b == DLE) {
      return State.ST_ESCAPE_START;
    }
    return out;
  }

  private State doData(byte b,
                       State stateIn)
  {
    State result;
    result = checkEscape(b,
                         State.ST_DATA);
    if (result != State.ST_ESCAPE_START) {
      if (b != EOT || stateIn == State.ST_ESCAPED_DATA) {
        inBuffer.put(b);
        result = State.ST_DATA;
      } else {
        ByteBuffer receivedData = Utils.allocateBEBuffer(inBuffer.position());
        inBuffer.limit(inBuffer.position());
        inBuffer.rewind();
        receivedData.put(inBuffer);
        receivedData.rewind();
        eventDispatcher.execute(() -> {
          processPacketData(receivedData);
        });
        inBuffer.clear();
        result = State.INIT;
      }
    }
    return result;
  }

  private State doEscapeStart(byte b)
  {
    State result = State.ERROR;
    byte r = (byte) (b ^ 0x20);
    switch (r) {
      case DLE:
      case SOH:
      case EOT:
        result = processByte(State.ST_ESCAPED_DATA,
                             r);
    }
    return result;
  }

  private State doError(byte b)
  {
    inBuffer.clear();
    readerState = State.INIT;
    eventDispatcher.execute(() -> {
      if (packetConsumer != null) {
        packetConsumer.accept(null);
      }
    });
    return readerState;
  }

  private void processPacketData(ByteBuffer buffer)
  {
    READ_LOGGER.log(Level.FINEST,
                    () -> "Process data " + Utils.byteBuffer2HexString(buffer,
                                                                       null,
                                                                       ' ').toString());
    Set<MX1PacketFlags> flags = MX1PacketFlags.toSet(buffer.get(1));
    MX1Command command = MX1Command.getCommand(buffer.get(2));
    int mysequence = buffer.get(3);
    ByteBuffer payload;
    int crcIn;
    int crcCalc;
    if (flags.contains(MX1PacketFlags.LONG_FRAME)) {
      int datalen = buffer.limit() - 6;
      crcIn = buffer.getShort(buffer.limit() - 2) & 0xffff;
      payload = buffer.slice(3,
                             datalen);

//      crcCalc = Utils.crc16((short) CRC_INIT,
//                            buffer.slice(0,
//                                         buffer.limit() - 2)) & 0xffff;
      crcCalc = crcIn;
    } else {
      int datalen = buffer.limit() - 5;
      crcIn = buffer.get(buffer.limit() - 1) & 0xff;
      payload = buffer.slice(3,
                             datalen);
      crcCalc = Utils.crc8((byte) CRC_INIT,
                           buffer.slice(0,
                                        buffer.limit() - 1)) & 0xff;
    }
    if (packetConsumer != null) {
      MX1Packet packet = null;
      if (crcIn == crcCalc) {
        packet = new PacketImpl((byte) mysequence,
                                flags,
                                command,
                                payload);
        MX1Packet p = packet;
        READ_LOGGER.log(Level.FINER,
                        () -> "Dispatch Packet " + p.toString());
      } else {
        READ_LOGGER.log(Level.SEVERE,
                        "CRC mismatch");
      }
      rxPacketCounter.incrementAndGet();
      if (packetConsumer != null) {
        packetConsumer.accept(packet);
      }
    }
  }

}
