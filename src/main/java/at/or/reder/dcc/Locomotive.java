/*
 * Copyright 2019-2020 Wolfgang Reder.
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

import at.or.reder.zcan20.ZCAN;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.SortedMap;
import java.util.concurrent.TimeoutException;

public interface Locomotive extends AutoCloseable
{

  public Controller getController();

  @Override
  public void close() throws IOException;

  public boolean isOwner();

  public void takeOwnership() throws IOException;

  public int getAddress();

  public Integer getCurrentSpeed();

  public Direction getDirection();

  public void control(Direction dir,
                      int speed) throws IOException;

  public SortedMap<Integer, Integer> getFunctions();

  public default boolean isFunction(int iFunction)
  {
    if (iFunction < 0 || iFunction >= ZCAN.NUM_FUNCTION) {
      throw new IndexOutOfBoundsException();
    }
    Integer v = getFunctions().get(iFunction);
    return v != null && v != 0;
  }

  public default Integer getFunction(int iFunction)
  {
    if (iFunction < 0 || iFunction >= ZCAN.NUM_FUNCTION) {
      throw new IndexOutOfBoundsException();
    }
    return getFunctions().get(iFunction);
  }

  public default void setFunction(int iFunction,
                                  boolean val) throws IOException
  {
    if (iFunction < 0 || iFunction >= ZCAN.NUM_FUNCTION) {
      throw new IndexOutOfBoundsException();
    }
    setFunctions(Collections.singletonMap(iFunction,
                                          val ? 1 : 0));
  }

  public default void setFunction(int iFunction,
                                  int val) throws IOException
  {
    if (iFunction < 0 || iFunction >= ZCAN.NUM_FUNCTION) {
      throw new IndexOutOfBoundsException();
    }
    setFunctions(Collections.singletonMap(iFunction,
                                          val));
  }

  public void setFunctions(Map<Integer, Integer> functions) throws IOException;

  public default void toggleFunction(int iFunction) throws IOException
  {
    setFunction(iFunction,
                !isFunction(iFunction));
  }

  public byte readCV(int cvIndex,
                     int timeout) throws IOException, TimeoutException;

  public void clearCV() throws IOException;

}
