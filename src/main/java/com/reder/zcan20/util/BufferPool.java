/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.reder.zcan20.util;

import java.nio.ByteBuffer;
import java.util.Deque;
import java.util.LinkedList;
import java.util.concurrent.Semaphore;

/**
 * Implements a bool of ByteBuffer with a fixed buffer capacity
 *
 * @author reder
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
