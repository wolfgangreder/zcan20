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
package at.or.reder.dcc;

import org.openide.util.NbBundle;

/**
 *
 * @author Wolfgang Reder
 */
@NbBundle.Messages({"PowerMode_PENDING=Unbekannt",
                    "PowerMode_ON=Aktiv",
                    "PowerMode_SSPF0=SSP FS0",
                    "PowerMode_SSPEM=SSP Em",
                    "PowerMode_OFF=Aus",
                    "PowerMode_SERVICE=Service",
                    "PowerMode_OVERCURRENT=Strom!",
                    "PowerMode_OVERVOLTAGE=Überspannung!",
                    "PowerMode_UNDERVOLTAGE=Unterspannung!",
                    "PowerMode_SUPPLYVOLTAGE=Versorgung!",
                    "PowerMode_ERROR=Fehler!"})

public enum PowerMode
{
  ON(Bundle.PowerMode_ON()),
  OFF(Bundle.PowerMode_OFF()),
  SSPEM(Bundle.PowerMode_SSPEM()),
  SSPF0(Bundle.PowerMode_SSPF0()),
  ERROR(Bundle.PowerMode_ERROR()),
  OVERCURRENT(Bundle.PowerMode_OVERCURRENT()),
  UNDERVOLTAGE(Bundle.PowerMode_UNDERVOLTAGE()),
  OVERVOLTAGE(Bundle.PowerMode_OVERVOLTAGE()),
  SUPPLYVOLTAGE(Bundle.PowerMode_SUPPLYVOLTAGE()),
  SERVICE(Bundle.PowerMode_SERVICE()),
  PENDING(Bundle.PowerMode_PENDING());

  private final String label;

  private PowerMode(String label)
  {
    this.label = label;
  }

  public String getLabel()
  {
    return label;
  }

}
