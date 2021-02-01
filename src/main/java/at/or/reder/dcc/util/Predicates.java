/*
 * Copyright 2017-2021 Wolfgang Reder.
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
package at.or.reder.dcc.util;

import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 *
 * @author Wolfgang Reder
 */
public final class Predicates
{

  public static boolean isNull(Object o)
  {
    return o == null;
  }

  public static boolean isNotNull(Object o)
  {
    return o != null;
  }

  public static <M> Predicate<M> matches(M value)
  {
    return (M m) -> Objects.equals(m,
                                   value);
  }

  public static <V, M> Predicate<M> matches(V value,
                                            Function<M, V> valueMapper)
  {
    Objects.requireNonNull(valueMapper,
                           "valueMapper is null");
    return (M m) -> {
      V v = valueMapper.apply(m);
      return Objects.equals(v,
                            value);
    };
  }

  private Predicates()
  {
  }

}
