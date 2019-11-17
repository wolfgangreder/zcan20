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

import at.or.reder.dcc.PowerMode;
import org.openide.util.NbBundle.Messages;

/**
 *
 * @author Wolfgang Reder
 */
@Messages({"ZimoPowerMode_PENDING=Unbekannt",
           "ZimoPowerMode_ON=Aktiv",
           "ZimoPowerMode_SSP0=SSP FS0",
           "ZimoPowerMode_SSPE=SSP Em",
           "ZimoPowerMode_OFF=Aus",
           "ZimoPowerMode_SERVICE=Service",
           "ZimoPowerMode_OVERCURRENT=Strom!",
           "ZimoPowerMode_OVERVOLTAGE=Spannung!",
           "ZimoPowerMode_SUPPLYVOLTAGE=Versorgung!"})
public enum ZimoPowerMode
{
  PENDING(0,
          Bundle.ZimoPowerMode_PENDING(),
          PowerMode.PENDING),
  ON(1,
     Bundle.ZimoPowerMode_ON(),
     PowerMode.ON),
  SSP0(2,
       Bundle.ZimoPowerMode_SSP0(),
       PowerMode.SSPF0),
  SSPE(3,
       Bundle.ZimoPowerMode_SSPE(),
       PowerMode.SSPEM),
  OFF(4,
      Bundle.ZimoPowerMode_OFF(),
      PowerMode.OFF),
  SERVICE(5,
          Bundle.ZimoPowerMode_SERVICE(),
          PowerMode.SERVICE),
  OVERCURRENT(0xa,
              Bundle.ZimoPowerMode_OVERCURRENT(),
              PowerMode.OVERCURRENT),
  UNDERVOLTAGE(0x20,
               Bundle.ZimoPowerMode_OVERVOLTAGE(),
               PowerMode.UNDERVOLTAGE),
  SUPPLYVOLTAGE(0x40,
                Bundle.ZimoPowerMode_SUPPLYVOLTAGE(),
                PowerMode.SUPPLYVOLTAGE),
  UNKNOWN(0xff,
          "Unknown",
          PowerMode.ERROR);
  private final int magic;
  private final String label;
  private final PowerMode sysMode;

  private ZimoPowerMode(int magic,
                        String label,
                        PowerMode sysMode)
  {
    this.magic = magic;
    this.label = label;
    this.sysMode = sysMode;
  }

  public String getLabel()
  {
    return label;
  }

  public int getMagic()
  {
    return magic;
  }

  public static ZimoPowerMode valueOfMagic(int magic)
  {
    int tmp = magic & 0xff;
    for (ZimoPowerMode m : values()) {
      if (m.magic == tmp) {
        return m;
      }
    }
    return UNKNOWN;
    //throw new IllegalArgumentException("invalid magic 0x" + magic);
  }

  public static ZimoPowerMode valueOf(PowerMode mode)
  {
    for (ZimoPowerMode m : values()) {
      if (m.getSysteMode() == mode) {
        return m;
      }
    }
    throw new IllegalArgumentException("invalid power mode " + mode);
  }

  public PowerMode getSysteMode()
  {
    return sysMode;
  }

}
