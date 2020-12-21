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

import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.openide.util.NbBundle.Messages;

@Messages({"# {0} - id",
           "Manufacturer_unknown=Unbekannt {0,number,0}"})
public final class Manufacturer
{

  private static Map<Integer, String> values;

  private Manufacturer()
  {
  }

  private static synchronized void checkInit()
  {
    if (values == null) {
      Map<Integer, String> tmp = new ConcurrentHashMap<>();
      try {
        Properties props = new Properties();
        props.load(Manufacturer.class.getResourceAsStream("manufacturer.properties"));
        for (Map.Entry<Object, Object> e : props.entrySet()) {
          String key = e.getKey().toString();
          if (key.endsWith("_name")) {
            int num = -1;
            try {
              num = Integer.parseInt(key,
                                     0,
                                     key.length() - 5,
                                     10);
            } catch (Throwable th) {
            }
            if (num > 0 && e.getValue() != null && !e.getValue().toString().isBlank()) {
              tmp.put(num,
                      e.getValue().toString());
            }
          }
        }
      } catch (IOException ex) {
        Logger.getLogger("at.or.reder.dcc").log(Level.SEVERE,
                                                "Manufacturer.checkInit",
                                                ex);
      }
      values = Collections.unmodifiableMap(tmp);
    }
  }

  public static String getManufacturerName(int i)
  {
    checkInit();
    String result = values.get(i);
    if (result == null) {
      return Bundle.Manufacturer_unknown(i);
    }
    return result;
  }

  public static Map<Integer, String> getAllManufacturerNames()
  {
    checkInit();
    return values;
  }

}
