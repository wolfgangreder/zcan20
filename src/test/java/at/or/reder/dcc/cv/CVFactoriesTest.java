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
package at.or.reder.dcc.cv;

import at.or.reder.dcc.util.SimpleCVAddress;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import static org.testng.AssertJUnit.*;
import org.testng.annotations.Test;

/**
 *
 * @author Wolfgang Reder
 */
public class CVFactoriesTest
{

  @Test(enabled = false)
  public void testLoadBasic() throws Exception
  {
    CVSet set = null;
    try (InputStream is = getClass().getResourceAsStream("/at/or/reder/dcc/cv/impl/zimo.xml")) {
      set = CVFactories.loadCVSetFromXML(null,
                                         is);
      assertNotNull(set);
    }
    ByteArrayOutputStream os = new ByteArrayOutputStream();
    CVFactories.storeCVSetToXML(set,
                                os);
    String tmp = new String(os.toByteArray(),
                            StandardCharsets.UTF_8);
    System.err.println(tmp);
  }

  @Test(enabled = false)
  public void testLoadAllowedValues() throws Exception
  {
    CVSet set;
    try (InputStream is = getClass().getResourceAsStream("/at/or/reder/dcc/cv/impl/test_allowed_value.xml")) {
      set = CVFactories.loadCVSetFromXML(null,
                                         is);
      assertNotNull(set);
    }
    CVEntry entry = set.getEntry(SimpleCVAddress.valueOf(155));
    assertNotNull(entry);
    List<CVBitDescriptor> bitDescriptors = entry.getBitDescriptors();
    assertNotNull(bitDescriptors);
    assertEquals(2,
                 bitDescriptors.size());
  }

}
