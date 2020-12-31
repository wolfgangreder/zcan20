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
package at.or.reder.zcan20.stein;

import at.or.reder.dcc.DCCConstants;
import java.util.BitSet;
import javax.xml.bind.annotation.adapters.XmlAdapter;

public final class FunctionConfigConverter extends XmlAdapter<String, BitSet>
{

  @Override
  @SuppressWarnings("UseSpecificCatch")
  public BitSet unmarshal(String v)
  {
    BitSet result = new BitSet(DCCConstants.NUM_FUNCTION);
    if (v != null) {
      int l = 0;
      try {
        l = Integer.parseUnsignedInt(v);
      } catch (Throwable th) {
      }
      if (l != 0) {
        int mask = 1;
        for (int i = 1; i < result.size(); ++i) {
          result.set(i,
                     (l & mask) != 0);
          mask <<= 1;
        }
      }
    }
    return result;
  }

  @Override
  public String marshal(BitSet v)
  {
    if (v != null) {
      long l = v.toLongArray()[0];
      return Long.toString(l);
    }
    return "0";
  }

}
