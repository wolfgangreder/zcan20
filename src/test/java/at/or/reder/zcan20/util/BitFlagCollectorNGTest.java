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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import static org.testng.AssertJUnit.assertEquals;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 *
 * @author Wolfgang Reder
 */
public class BitFlagCollectorNGTest
{

  public BitFlagCollectorNGTest()
  {
  }

  @BeforeClass
  public static void setUpClass() throws Exception
  {
  }

  @Test
  public void test1()
  {
    List<Integer> list = Arrays.asList(0x01,
                                       0x02,
                                       0x04,
                                       0x08,
                                       0x10,
                                       0x20,
                                       0x40,
                                       0x80);
    int value = list.stream().collect(new IntBitFlagCollector());
    assertEquals(value,
                 0xff);
    for (int i = 0, v = 1; i < Integer.SIZE; ++i) {
      list = Collections.singletonList(v);
      value = list.stream().collect(new IntBitFlagCollector());
      assertEquals(v,
                   value);
      v <<= 1;
      list = Collections.singletonList(~v);
      value = list.stream().collect(new IntBitFlagCollector());
      assertEquals(~v,
                   value);
      v <<= 1;
    }
  }

}
