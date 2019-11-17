/*
 * Copyright 2019 Wolfgang Reder.
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

import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Function;

/**
 *
 * @author Wolfgang Reder
 * @param <V>
 * @param <A>
 */
public final class ScalarFuture<V, A> implements Future<V>
{

  private final Future<A> wrapped;
  private final Function<A, V> converter;

  public ScalarFuture(Future<A> wrapped,
                      Function<A, V> converter)
  {
    this.wrapped = Objects.requireNonNull(wrapped,
                                          "wrapped future is null");
    this.converter = Objects.requireNonNull(converter,
                                            "converter is null");
  }

  @Override
  public boolean cancel(boolean mayInterruptIfRunning)
  {
    return wrapped.cancel(mayInterruptIfRunning);
  }

  @Override
  public boolean isCancelled()
  {
    return wrapped.isCancelled();
  }

  @Override
  public boolean isDone()
  {
    return wrapped.isDone();
  }

  @Override
  public V get() throws InterruptedException, ExecutionException
  {
    return converter.apply(wrapped.get());
  }

  @Override
  public V get(long timeout,
               TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException
  {
    return converter.apply(wrapped.get(timeout,
                                       unit));
  }

}
