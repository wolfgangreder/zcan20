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
package at.or.reder.zcan20.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.atomic.AtomicLong;

public final class CounterInputStream extends InputStream implements Counter
{

  private final AtomicLong counter = new AtomicLong();
  private final InputStream in;

  public CounterInputStream(InputStream in)
  {
    this.in = in;
  }

  @Override
  public void resetCounter()
  {
    counter.set(0);
  }

  @Override
  public long getCounter()
  {
    return counter.get();
  }

  @Override
  public int read() throws IOException
  {
    int result = in.read();
    if (result != -1) {
      counter.incrementAndGet();
    }
    return result;
  }

  @Override
  public int read(byte[] b) throws IOException
  {
    int result = in.read(b);
    if (result != -1) {
      counter.addAndGet(result);
    }
    return result;
  }

  @Override
  public int read(byte[] b,
                  int off,
                  int len) throws IOException
  {
    int result = in.read(b,
                         off,
                         len);
    if (result != -1) {
      counter.addAndGet(result);
    }
    return result;
  }

  @Override
  public byte[] readAllBytes() throws IOException
  {
    byte[] result = in.readAllBytes();
    if (result != null) {
      counter.addAndGet(result.length);
    }
    return result;
  }

  @Override
  public byte[] readNBytes(int len) throws IOException
  {
    byte[] result = in.readNBytes(len);
    if (result != null) {
      counter.addAndGet(result.length);
    }
    return result;
  }

  @Override
  public int readNBytes(byte[] b,
                        int off,
                        int len) throws IOException
  {
    int result = in.readNBytes(b,
                               off,
                               len);
    if (result != -1) {
      counter.addAndGet(result);
    }
    return result;
  }

  @Override
  public long transferTo(OutputStream out) throws IOException
  {
    long result = in.transferTo(out);
    counter.addAndGet(result);
    return result;
  }

  @Override
  public long skip(long n) throws IOException
  {
    return in.skip(n);
  }

  @Override
  public void skipNBytes(long n) throws IOException
  {
    in.skipNBytes(n);
  }

  @Override
  public int available() throws IOException
  {
    return in.available();
  }

  @Override
  public void close() throws IOException
  {
    in.close();
  }

  @Override
  public synchronized void mark(int readlimit)
  {
    in.mark(readlimit);
  }

  @Override
  public synchronized void reset() throws IOException
  {
    in.reset();
  }

  @Override
  public boolean markSupported()
  {
    return in.markSupported();
  }

}
