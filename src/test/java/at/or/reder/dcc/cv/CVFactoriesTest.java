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

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import static org.testng.AssertJUnit.assertNotNull;
import org.testng.annotations.Test;

/**
 *
 * @author Wolfgang Reder
 */
public class CVFactoriesTest
{

  @Test
  public void testLoadBasic() throws Exception
  {
    CVSet set = null;
    try (InputStream is = getClass().getResourceAsStream("/at/or/reder/dcc/cv/impl/basic.xml")) {
      set = CVFactories.loadCVSetFromXML(is);
      assertNotNull(set);
    }
    ByteArrayOutputStream os = new ByteArrayOutputStream();
    CVFactories.storeCVSetToXML(set,
                                os);
    String tmp = new String(os.toByteArray(),
                            StandardCharsets.UTF_8);
    System.err.println(tmp);
  }

}
