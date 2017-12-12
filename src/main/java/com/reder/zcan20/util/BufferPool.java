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
package com.reder.zcan20.util;

import java.nio.ByteBuffer;
import java.util.Deque;
import java.util.LinkedList;
import java.util.concurrent.Semaphore;

/**
 * Implements a Pool of ByteBuffer with a fixed buffer capacity
 *
 * @author Wolfgang Reder
 */
public final class BufferPool
{

  public final class BufferItem implements AutoCloseable
  {

    private final ByteBuffer buffer;
    private long lastUsed;

    private BufferItem(ByteBuffer buffer)
    {
      this.buffer = buffer;
      touch();
    }

    public ByteBuffer getBuffer()
    {
      return buffer;
    }

    @Override
    public void close()
    {
      buffer.rewind();
      putBack(this);
    }

    private void touch()
    {
      lastUsed = System.currentTimeMillis();
    }

  }
  private final Deque<BufferItem> freePool = new LinkedList<>();
  private final Semaphore sem;
  private final int capacity;

  public BufferPool(int bufferCapacity,
                    int maxBuffers)
  {
    this.capacity = bufferCapacity;
    sem = new Semaphore(maxBuffers);
  }

  public BufferItem getBuffer()
  {
    sem.acquireUninterruptibly();
    BufferItem result;
    synchronized (freePool) {
      if (freePool.isEmpty()) {
        result = new BufferItem(ByteBuffer.allocate(capacity));
      } else {
        result = freePool.getFirst();
      }
    }
    result.buffer.clear();
    return result;
  }

  private void putBack(BufferItem item)
  {
    if (item != null) {
      synchronized (freePool) {
        freePool.addFirst(item);
        item.touch();
        long oldestEntry = System.currentTimeMillis() - 3_600_000L;
        freePool.removeIf((i) -> i.lastUsed < oldestEntry);
      }
      sem.release();
    }
  }

}
