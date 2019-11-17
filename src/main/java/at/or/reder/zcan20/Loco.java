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
package at.or.reder.zcan20;

import at.or.reder.zcan20.packet.CVInfoAdapter;
import java.io.IOException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeoutException;

/**
 *
 * @author Wolfgang Reder
 */
public interface Loco extends AutoCloseable
{

  public LocoMode getMode();

  public short getLoco();

  @Override
  public void close() throws IOException;

  public byte readCV(int cv,
                     int timeout) throws IOException, TimeoutException;

  public Future<CVInfoAdapter> readCV(int cv) throws IOException;

  public void clearCV() throws IOException;

}
