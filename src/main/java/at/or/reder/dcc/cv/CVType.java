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

/**
 *
 * @author Wolfgang Reder
 */
public enum CVType
{
  /**
   * Allgemeine numerische CV.
   */
  NUMERIC,
  /**
   * Bitfeld CV.
   */
  BITFIELD,
  /**
   * Niederwertigstes Bankregister.
   */
  BANKREGISTER_0,
  /**
   * Bankregister.
   * Wenn es nur zwei Bankregister gibt, ist dieses Register das höherwertige.
   */
  BANKREGISTER_1,
  /**
   * Bankregister.
   */
  BANKREGISTER_2,
  /**
   * Höherwertiges Bankregisters.
   */
  BANKREGISTER_3;
}
