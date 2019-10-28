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

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * Stellt Funktionen zur Kontrolle von Fahrzeugen zur Verfügung.
 *
 * @author Wolfgang Reder
 */
public interface DriveControl extends BaseControl
{

  /**
   * Versucht Informationen zu einem Fahrzeugdekoder zu ermitteln. Das Ergebnis wird auch an alle registrierten
   * LocomotiveEventListener übermittelt.
   *
   * @param address Adresse des Dekoders
   * @return Informationen zum Fahrzeug.
   * @throws IOException Im Falle von Kommunikationsfehlern
   * @throws TimeoutException Wenn nach ablauf des Kommunikationstimouts keine Informationen erhalten werden konnten.
   * @see #postLocomitiveInfoRequest(int)
   */
  public Locomotive getLocomotive(int address) throws IOException, TimeoutException;

  /**
   * Setzt einen Request zum Ermitteln von Fahrzeuginformationen ab. Das (eventuelle) Ergebnis wird an die registrierten
   * LocomotiveEventListener übermittelt.
   *
   * @param address Adresse des Fahrzeugs.
   * @throws IOException Im Falle von Kommunikationsfehlern.
   */
  public void postLocomitiveInfoRequest(int address) throws IOException;

  public void setSpeed(int locomotive,
                       int newSpeed) throws IOException;

  public void setDirection(int locomotive,
                           Direction direction) throws IOException;

  public void emergencyStop(int locomotive) throws IOException;

  public void setFunction(int locomotive,
                          int function,
                          boolean state) throws IOException;

  public void addLocomotiveListener(LocomotiveEventListener listener);

  public void removeLocomotiveListener(LocomotiveEventListener listener);

}
