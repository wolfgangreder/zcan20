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
package at.or.reder.zcan20;

import static org.testng.AssertJUnit.*;
import org.testng.annotations.Test;

/**
 *
 * @author Wolfgang Reder
 */
public class CanIdNGTest
{

  public CanIdNGTest()
  {
  }

  /**
   * Test of valueOf method, of class CanId.
   */
  @Test
  public void testValueOf_int()
  {
    int val = 1 << 28;
    CanId result = CanId.valueOf(val);
    assertEquals(val,
                 result.intValue());
    val = (1 << 28) + 2344546;
    result = CanId.valueOf(val);
    assertEquals(val,
                 result.intValue());
  }

  /**
   * Test of valueOf method, of class CanId.
   */
  @Test
  public void testValueOf_4args()
  {
    CommandGroup grp = CommandGroup.TRACK_CONFIG_PRIVATE;
    CommandMode mode = CommandMode.EVENT;
    byte command = CommandGroup.DATA_ITEM_IMAGE;
    short sendNid = (short) 0xfeca;
    CanId result = CanId.valueOf(grp,
                                 command,
                                 mode,
                                 sendNid);
    assertEquals(0x164afeca,
                 result.intValue());
    assertSame(grp,
               result.getCommandGroup());
    assertSame(mode,
               result.getCommandMode());
    assertEquals(command,
                 result.getCommand());
    assertEquals(sendNid,
                 result.getSenderNid());
  }

  @Test
  public void test()
  {
    CanId id = CanId.valueOf(0x1600C3C9);
    byte command = id.getCommand();
    CommandGroup group = id.getCommandGroup();
    CommandMode mode = id.getCommandMode();
    short senderNid = id.getSenderNid();
    assertEquals((byte) 0,
                 command);
    assertSame(CommandGroup.TRACK_CONFIG_PRIVATE,
               group);
    assertSame(CommandMode.REQUEST,
               mode);
    assertEquals((short) 0xc3c9,
                 senderNid);
  }

}
