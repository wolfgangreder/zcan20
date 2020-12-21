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

import at.or.reder.mx1.MX1;
import at.or.reder.mx1.MX1Packet;
import at.or.reder.mx1.MX1PacketAdapter;
import at.or.reder.mx1.MX1PacketListener;
import at.or.reder.mx1.MX1PacketObject;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Function;
import java.util.function.Predicate;
import javax.validation.constraints.NotNull;

public final class PacketListenerFuture<V> implements AutoCloseable, Future<V>
{

  private final MX1 mx1;
  private final Predicate<MX1Packet> packetFilter;
  private final Function<MX1Packet, V> valueExtractor;
  private final CompletableFuture<V> future;
  private final MX1PacketListener listener = this::onMX1Packet;

  public static <A extends MX1PacketAdapter> PacketListenerFuture<A> createFuture(@NotNull MX1 mx1,
                                                                                  @NotNull Class<? extends A> adapterClazz)
  {
    return new PacketListenerFuture<>(mx1,
                                      (MX1Packet p) -> p.getAdapter(adapterClazz) != null,
                                      (MX1Packet p) -> p.getAdapter(adapterClazz));

  }

  public static <A extends MX1PacketAdapter, R> PacketListenerFuture<R> createFuture(@NotNull MX1 mx1,
                                                                                     @NotNull Class<? extends A> adapterClazz,
                                                                                     @NotNull Function<A, R> resultAdapter)
  {
    Function<MX1Packet, A> afunc = (MX1Packet p) -> p.getAdapter(adapterClazz);
    Function<MX1Packet, R> func;
    func = afunc.andThen(resultAdapter);
    return new PacketListenerFuture<>(mx1,
                                      (MX1Packet p) -> p.getAdapter(adapterClazz) != null,
                                      func);
  }

  public static PacketListenerFuture<MX1Packet> createFuture(@NotNull MX1 mx1,
                                                             @NotNull Predicate<MX1Packet> packetFilter)
  {
    Function<MX1Packet, MX1Packet> ve = (p) -> p;
    return new PacketListenerFuture<>(mx1,
                                      packetFilter,
                                      ve);
  }

  public PacketListenerFuture(@NotNull MX1 mx1,
                              @NotNull Predicate<MX1Packet> packetFilter,
                              @NotNull Function<MX1Packet, V> valueExtractor)
  {
    this.mx1 = Objects.requireNonNull(mx1,
                                      "mx1 is null");
    this.packetFilter = Objects.requireNonNull(packetFilter,
                                               "packetFilter is null");
    this.valueExtractor = Objects.requireNonNull(valueExtractor,
                                                 "valueExtractor is null");
    this.future = new CompletableFuture<>();
    mx1.addMX1PacketListener(listener);
  }

  private void onMX1Packet(MX1PacketObject evt)
  {
    if (packetFilter.test(evt.getPacket())) {
      evt.getSource().removeMX1PacketListener(listener);
      V value = valueExtractor.apply(evt.getPacket());
      future.complete(value);
    }
  }

  @Override
  public void close()
  {
    future.cancel(true);
    mx1.removeMX1PacketListener(listener);
  }

  @Override
  public V get(long timeout,
               TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException
  {
    return future.get(timeout,
                      unit);
  }

  @Override
  public V get() throws InterruptedException, ExecutionException
  {
    return future.get();
  }

  @Override
  public boolean cancel(boolean mayInterruptIfRunning)
  {
    return future.cancel(mayInterruptIfRunning);
  }

  public boolean isCompleted()
  {
    return isDone() && !future.isCancelled() && !future.isCompletedExceptionally();
  }

  @Override
  public boolean isCancelled()
  {
    return future.isCancelled();
  }

  @Override
  public boolean isDone()
  {
    return future.isDone();
  }

}
