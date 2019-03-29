/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.or.reder.zcan20;

import at.or.reder.zcan20.CommandGroup;
import at.or.reder.zcan20.ZCANError;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;
import static org.testng.AssertJUnit.assertTrue;
import org.testng.annotations.Test;

/**
 *
 * @author reder
 */
public class ZCANErrorNGTest
{

  public ZCANErrorNGTest()
  {
  }

  @org.testng.annotations.BeforeClass
  public static void setUpClass() throws Exception
  {
  }

  @org.testng.annotations.AfterClass
  public static void tearDownClass() throws Exception
  {
  }

  @org.testng.annotations.BeforeMethod
  public void setUpMethod() throws Exception
  {
  }

  @org.testng.annotations.AfterMethod
  public void tearDownMethod() throws Exception
  {
  }

  private static final class TestException extends ZCANError
  {

    public TestException(int nid,
                         CommandGroup commandGroup,
                         int command,
                         ByteBuffer value,
                         String message)
    {
      super(nid,
            commandGroup,
            command,
            value.array(),
            message);
    }

  }

  @Test
  public void testStreaming() throws IOException, ClassNotFoundException
  {
    int nid = 1;
    CommandGroup grp = CommandGroup.FILE_CONTROL;
    int command = 0xff;
    ByteBuffer value = ByteBuffer.allocate(4);
    value.putInt(0x12345678);
    value.rewind();
    String message = "TestMessage";
    TestException ex = new TestException(nid,
                                         grp,
                                         command,
                                         value,
                                         message);
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    try (ObjectOutputStream oos = new ObjectOutputStream(out)) {
      oos.writeObject(ex);
    }
    try (ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(out.toByteArray()))) {
      Object tmp = ois.readObject();
      assertTrue(tmp instanceof ZCANError);
    } catch (IOException excp) {
      excp.printStackTrace(System.err);
    }
  }

}
