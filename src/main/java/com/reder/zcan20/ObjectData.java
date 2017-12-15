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
package com.reder.zcan20;

import com.reder.zcan20.packet.DataGroupCountPacketAdapter;
import java.io.IOException;
import javax.validation.constraints.NotNull;

/**
 *
 * @author Wolfgang Reder
 */
public interface ObjectData
{

  /**
   * Post a request to get the count of object in the DataGroup {@code group}.
   *
   * @param group DataGroup to query
   * @throws IOException
   */
  public void getObjectCount(@NotNull DataGroup group) throws IOException;

  /**
   * Retries the count of objects in the DataGroup {@code group}. If there is no answer in {@code timeout} ms, {@code -1} is
   * returned.
   *
   * @param group DataGroup to query
   * @param timeout milliseconds to wait
   * @return The objectcount or -1
   * @throws IOException
   */
  public DataGroupCountPacketAdapter getObjectCount(@NotNull DataGroup group,
                                                    long timeout) throws IOException;

  public void getObjectInfoByIndex(@NotNull DataGroup group,
                                   short index) throws IOException;

  public void getObjectInfoByNid(short nid) throws IOException;

}
