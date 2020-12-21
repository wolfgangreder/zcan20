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
package at.or.reder.dcc;

import static org.testng.AssertJUnit.assertEquals;
import org.testng.annotations.Test;

/**
 *
 * @author Wolfgang Reder
 */
public class SpeedstepSystemNGTest
{

  public SpeedstepSystemNGTest()
  {
  }

  @Test
  public void testSpeedToSystem_UNKNOWN()
  {
    SpeedstepSystem ss = SpeedstepSystem.UNKNOWN;
    assertEquals(0,
                 ss.normalizedToSystem(0));
    assertEquals(10,
                 ss.normalizedToSystem(10));
    assertEquals(512,
                 ss.normalizedToSystem(512));
    assertEquals(1024,
                 ss.normalizedToSystem(1024));
  }

  @Test
  public void testSpeedToSystem_SPEED_14()
  {
    SpeedstepSystem ss = SpeedstepSystem.SPEED_14;
    assertEquals(0,
                 ss.normalizedToSystem(0));
    assertEquals(7,
                 ss.normalizedToSystem(512));
    assertEquals(14,
                 ss.normalizedToSystem(1024));
  }

  @Test
  public void testSpeedToSystem_SPEED_28()
  {
    SpeedstepSystem ss = SpeedstepSystem.SPEED_28;
    assertEquals(0,
                 ss.normalizedToSystem(0));
    assertEquals(14,
                 ss.normalizedToSystem(512));
    assertEquals(28,
                 ss.normalizedToSystem(1024));
  }

  @Test
  public void testSpeedToSystem_SPEED_128()
  {
    SpeedstepSystem ss = SpeedstepSystem.SPEED_128;
    assertEquals(0,
                 ss.normalizedToSystem(0));
    assertEquals(63,
                 ss.normalizedToSystem(512));
    assertEquals(126,
                 ss.normalizedToSystem(1024));
  }

  @Test
  public void testSystemToNormalized_UNKNOWN()
  {
    SpeedstepSystem ss = SpeedstepSystem.UNKNOWN;
    assertEquals(0,
                 ss.systemToNormalized(0));
    assertEquals(10,
                 ss.systemToNormalized(10));
    assertEquals(512,
                 ss.systemToNormalized(512));
    assertEquals(1024,
                 ss.systemToNormalized(1024));
  }

  @Test
  public void testSystemToNormalized_SPEED14()
  {
    SpeedstepSystem ss = SpeedstepSystem.SPEED_14;
    assertEquals(0,
                 ss.systemToNormalized(0));
    assertEquals(512,
                 ss.systemToNormalized(7));
    assertEquals(1024,
                 ss.systemToNormalized(14));
  }

  @Test
  public void testSystemToNormalized_SPEED28()
  {
    SpeedstepSystem ss = SpeedstepSystem.SPEED_28;
    assertEquals(0,
                 ss.systemToNormalized(0));
    assertEquals(512,
                 ss.systemToNormalized(14));
    assertEquals(1024,
                 ss.systemToNormalized(28));
  }

  @Test
  public void testSystemToNormalized_SPEED128()
  {
    SpeedstepSystem ss = SpeedstepSystem.SPEED_128;
    assertEquals(0,
                 ss.systemToNormalized(0));
    assertEquals(512,
                 ss.systemToNormalized(63));
    assertEquals(1024,
                 ss.systemToNormalized(126));
  }

}
