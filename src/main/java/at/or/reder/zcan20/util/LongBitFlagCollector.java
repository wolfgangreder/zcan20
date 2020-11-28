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

import java.util.EnumSet;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

/**
 *
 * @author Wolfgang Reder
 */
public class LongBitFlagCollector implements Collector<Long, LongBitFlagCollector.LongCombiner, Long>
{

  protected static final class LongCombiner
  {

    private long value;

    synchronized long getValue()
    {
      return value;
    }

    synchronized LongCombiner apply(long i)
    {
      value |= i;
      return this;
    }

  }

  @Override
  public Set<Characteristics> characteristics()
  {
    return EnumSet.of(Characteristics.UNORDERED,
                      Characteristics.CONCURRENT);
  }

  @Override
  public Supplier<LongCombiner> supplier()
  {
    return LongCombiner::new;
  }

  @Override
  public BiConsumer<LongCombiner, Long> accumulator()
  {
    return (c, i) -> c.apply(i);
  }

  @Override
  public BinaryOperator<LongCombiner> combiner()
  {
    return (a, b) -> a.apply(b.getValue());
  }

  @Override
  public Function<LongCombiner, Long> finisher()
  {
    return LongCombiner::getValue;
  }

}
