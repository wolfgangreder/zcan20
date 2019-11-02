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
package at.or.reder.dcc.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 *
 * @author Wolfgang Reder
 */
public final class XmlIntAdapter extends XmlAdapter<String, Integer>
{

  private static final Pattern patternHex = Pattern.compile("\\A0[xX](?:([a-fA-F0-9]{1,2}))\\z");
  private static final Pattern patternBin = Pattern.compile("\\A0[bB](?:([01]{1,8}))\\z");

  @Override
  public Integer unmarshal(String v) throws Exception
  {
    if (v != null) {
      Matcher m = patternHex.matcher(v);
      int radix = 10;
      String s = v;
      if (m.matches()) {
        radix = 16;
        s = m.group(1);
      }
      m = patternBin.matcher(v);
      if (m.matches()) {
        radix = 2;
        s = m.group(1);
      }
      return Integer.parseInt(s,
                              radix);
    }
    return 0;
  }

  @Override
  public String marshal(Integer v)
  {
    if (v != null) {
      return Integer.toString(v);
    }
    return null;
  }

}
