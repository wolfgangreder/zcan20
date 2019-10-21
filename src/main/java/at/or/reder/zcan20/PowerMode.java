/*
 * Copyright 2017 Wolfgang Reder.
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
package at.or.reder.zcan20;

import org.openide.util.NbBundle.Messages;

/**
 *
 * @author Wolfgang Reder
 */
@Messages({"PowerMode_PENDING=Unbekannt",
           "PowerMode_ON=Aktiv",
           "PowerMode_SSP0=SSP FS0",
           "PowerMode_SSPE=SSP Em",
           "PowerMode_OFF=Aus",
           "PowerMode_SERVICE=Service",
           "PowerMode_OVERCURRENT=Strom!",
           "PowerMode_OVERVOLTAGE=Spannung!",
           "PowerMode_SUPPLYVOLTAGE=Versorgung!"})
public enum PowerMode
{
  PENDING(0,
          Bundle.PowerMode_PENDING()),
  ON(1,
     Bundle.PowerMode_ON()),
  SSP0(2,
       Bundle.PowerMode_SSP0()),
  SSPE(3,
       Bundle.PowerMode_SSPE()),
  OFF(4,
      Bundle.PowerMode_OFF()),
  SERVICE(5,
          Bundle.PowerMode_SERVICE()),
  OVERCURRENT(0xa,
              Bundle.PowerMode_OVERCURRENT()),
  UNDERVOLTAGE(0x20,
               Bundle.PowerMode_OVERVOLTAGE()),
  SUPPLYVOLTAGE(0x40,
                Bundle.PowerMode_SUPPLYVOLTAGE()),;
  private final int magic;
  private final String label;

  private PowerMode(int magic,
                    String label)
  {
    this.magic = magic;
    this.label = label;
  }

  public String getLabel()
  {
    return label;
  }

  public int getMagic()
  {
    return magic;
  }

  public static PowerMode valueOfMagic(int magic)
  {
    int tmp = magic & 0xff;
    for (PowerMode m : values()) {
      if (m.magic == tmp) {
        return m;
      }
    }
    throw new IllegalArgumentException("invalid magic 0x" + magic);
  }

}
