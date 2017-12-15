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
package com.reder.zcan20;

import com.reder.zcan20.util.CanIdMatcher;
import static org.testng.AssertJUnit.assertTrue;
import org.testng.annotations.Test;

/**
 *
 * @author Wolfgang Reder
 */
public class CanIdMatcherTest
{

  @Test
  public void testMatches1()
  {
    CanIdMatcher matcher = new CanIdMatcher(CanId.valueOf(CommandGroup.TRACK_CONFIG_PRIVATE,
                                                          CommandGroup.TSE_PROG_READ,
                                                          CommandMode.ACK,
                                                          (short) 0xc0a6),
                                            CanIdMatcher.MASK_NO_ADDRESS);
    CanId canId = CanId.valueOf(0x1623a6c0);
    assertTrue(matcher.matchesId(canId));
  }

}
