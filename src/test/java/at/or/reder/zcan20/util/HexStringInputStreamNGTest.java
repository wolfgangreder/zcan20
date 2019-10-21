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

import java.io.IOException;
import static org.testng.AssertJUnit.*;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 *
 * @author Wolfgang Reder
 */
public class HexStringInputStreamNGTest
{

  public HexStringInputStreamNGTest()
  {
  }

  @BeforeClass
  public static void setUpClass() throws Exception
  {
  }

  @Test
  public void testRead() throws Exception
  {
    HexStringInputStream in = new HexStringInputStream("");
    int read = in.read();
    assertEquals(-1,
                 read);
    in = new HexStringInputStream("cd");
    read = in.read();
    assertEquals(0xcd,
                 read);
    read = in.read();
    assertEquals(-1,
                 read);
    in = new HexStringInputStream("cdef");
    read = in.read();
    assertEquals(0xcd,
                 read);
    read = in.read();
    assertEquals(0xef,
                 read);
    read = in.read();
    assertEquals(-1,
                 read);
    in = new HexStringInputStream("cd12ea4f");
    read = in.read();
    assertEquals(0xcd,
                 read);
    read = in.read();
    assertEquals(0x12,
                 read);
    read = in.read();
    assertEquals(0xea,
                 read);
    read = in.read();
    assertEquals(0x4f,
                 read);
    read = in.read();
    assertEquals(-1,
                 read);
  }

  @Test(expectedExceptions = {IllegalArgumentException.class})
  public void testReadFail_1() throws Exception
  {
    HexStringInputStream in = new HexStringInputStream("c");
    int read = in.read();
  }

  @Test(expectedExceptions = {NullPointerException.class})
  public void testReadFail_2() throws Exception
  {
    HexStringInputStream in = new HexStringInputStream(null);
    int read = in.read();
  }

  @Test(expectedExceptions = {IOException.class})
  public void testReadFail_3() throws Exception
  {
    HexStringInputStream in = new HexStringInputStream("cdxf");
    int read = in.read();
    in.read();
  }

}
