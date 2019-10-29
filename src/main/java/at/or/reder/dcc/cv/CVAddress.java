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

import java.util.Map;

/**
 *
 * @author Wolfgang Reder
 */
public interface CVAddress
{

  /**
   * Adresse der CV.
   *
   * @return Adresse der CV
   */
  public int getAddress();

  /**
   * Ermittle die Registerbankadresse dieser CV
   *
   * @param type Bestimmt für welches Bankregister der Wert ermittelt werden soll
   * @return die einzutragende Adresse oder {@code -1} wenn es sich um keine Bank-CV handelt, oder dieses BankRegister nicht verwendet wird.
   * @see at.or.reder.dcc.cv.CVType
   */
  public default int getBankAddress(CVType type)
  {
    return getBankAddresses().getOrDefault(type,
                                           -1);
  }

  public Map<CVType, Integer> getBankAddresses();

}
