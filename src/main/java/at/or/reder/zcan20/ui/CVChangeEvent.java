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
package at.or.reder.zcan20.ui;

/**
 *
 * @author Wolfgang Reder
 */
public interface CVChangeEvent
{

  /**
   * On of
   * {@link javax.swing.event.TableModelEvent#INSERT}, {@link javax.swing.event.TableModelEvent#UPDATE}, {@link javax.swing.event.TableModelEvent#DELETE}
   *
   * @return eventcode
   * @see javax.swing.event.ListDataEvent
   */
  public int getEvent();

  /**
   * Source of event
   *
   * @return eventsource
   */
  public CVTableModel getSource();

  /**
   * CV Number
   *
   * @return cvNumber
   */
  public int getCVNumber();

  /**
   * Index of record in the Model. If {@code -1} all data has changed and {@code getSource()} returns {@code null}
   *
   * @return index
   */
  public int getModelIndex();

  /**
   * Value of the CV
   *
   * @return value
   */
  public short getValue();

}
