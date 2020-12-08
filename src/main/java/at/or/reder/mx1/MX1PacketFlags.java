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
package at.or.reder.mx1;

import java.util.Collection;
import java.util.EnumSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 *
 * @author Wolfgang Reder
 */
public enum MX1PacketFlags
{
  SHORT_FRAME(0),
  LONG_FRAME(0x80),
  PRIMARY(0),
  ACK_1(0x40),
  REPLY(0x20),
  ACK_2(0x60),
  FROM_PC(0x10),
  FROM_COMMANDSTATION(0x0),
  TO_COMMANDSTATION(0x0),
  TO_MX8(0x1),
  TO_MX9(0x2);
  private final int mask;

  private MX1PacketFlags(int mask)
  {
    this.mask = mask;
  }

  public static byte toBits(Collection<? extends MX1PacketFlags> coll)
  {
    if (coll != null) {
      return coll.stream().collect(Collectors.summingInt(MX1PacketFlags::getBits)).byteValue();
    }
    return 0;
  }

  public static Set<MX1PacketFlags> toSet(byte bits)
  {
    EnumSet<MX1PacketFlags> result = EnumSet.noneOf(MX1PacketFlags.class);
    if ((bits & LONG_FRAME.mask) != 0) {
      result.add(LONG_FRAME);
    } else {
      result.add(SHORT_FRAME);
    }
    switch (bits & 0x60) {
      case 0x40:
        result.add(ACK_1);
        break;
      case 0x20:
        result.add(REPLY);
        break;
      case 0x60:
        result.add(ACK_2);
        break;
      default:
        result.add(PRIMARY);
        break;
    }
    if ((bits & FROM_PC.mask) != 0) {
      result.add(FROM_PC);
    } else {
      result.add(FROM_COMMANDSTATION);
    }
    switch (bits & 0x7) {
      case 0x0:
        result.add(TO_COMMANDSTATION);
        break;
      case 0x1:
        result.add(TO_MX8);
        break;
      case 0x2:
        result.add(TO_MX9);
        break;
    }
    return result;
  }

  public int getBits()
  {
    return mask;
  }

}
