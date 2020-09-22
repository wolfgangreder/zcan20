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

import at.or.reder.dcc.util.Utils;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;
import javax.validation.constraints.NotNull;

public abstract class AbstractPropertySet implements PropertySet
{

  @SuppressWarnings("UseSpecificCatch")
  protected int testInteger(String value)
  {
    try {
      return Integer.parseInt(value);
    } catch (Throwable th) {
    }
    return -1;
  }

  protected boolean testPortNum(String value)
  {
    int i = testInteger(value);
    return i > 0 && i < 0x10000;
  }

  protected boolean testSerialPort(String value)
  {
    switch (Utils.getOSType()) {
      case LINUX: {
        Pattern pattern = Pattern.compile("\\A/dev/(tty|ACM)/\\d+\\z",
                                          Pattern.CASE_INSENSITIVE);
        return pattern.matcher(value).matches();
      }
      case WINDOWS: {
        Pattern pattern = Pattern.compile("\\ACOM\\d+:?\\z");
        return pattern.matcher(value).matches();
      }
      default:
        return false;
    }
  }

  @Override
  public Map<String, String> getDefaultProperties()
  {
    Map<String, String> result = new HashMap<>();
    for (String p : getPropertyNames()) {
      result.put(p,
                 getDefaultValue(p));
    }
    return result;
  }

  @Override
  public String getStringValue(@NotNull Map<String, String> map,
                               @NotNull String propName)
  {
    String v = map.get(propName);
    if (v != null) {
      return v;
    } else {
      return getDefaultValue(propName);
    }
  }

  @Override
  public boolean isKeyValueValid(Map<String, String> map,
                                 String propertyName)
  {
    return isValueValid(propertyName,
                        getStringValue(map,
                                       propertyName));
  }

  @Override
  public int getIntValue(@NotNull Map<String, String> map,
                         @NotNull String propName)
  {
    String strProp = getStringValue(map,
                                    propName);
    return testInteger(strProp);
  }

}
