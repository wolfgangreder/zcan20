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
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Stellt Funktionen zur Kontrolle von Zubehöhrdekodern zur Verfügung.
 *
 * @author Wolfgang Reder
 */
public interface AccessoryControl extends BaseControl
{

  public void addAccessoryEventListener(AccessoryEventListener l);

  public void removeAccessoryEventListener(AccessoryEventListener l);

  public default int getAccessoryState(short decoder,
                                       byte port,
                                       long timeout) throws IOException, TimeoutException
  {
    Future<Byte> tmp = getAccessoryState(decoder,
                                         port);
    try {
      Byte result = tmp.get(timeout,
                            TimeUnit.MILLISECONDS);
      if (result != null) {
        return result;
      }
    } catch (InterruptedException ex) {
      Thread.currentThread().interrupt();
    } catch (ExecutionException ex) {
      if (ex.getCause() instanceof IOException) {
        throw (IOException) ex.getCause();
      } else {
        throw new IOException(ex.getCause());
      }
    }
    return -1;
  }

  public Future<Byte> getAccessoryState(short decoder,
                                        byte port) throws IOException;

  public void setAccessoryState(short decoder,
                                byte port,
                                byte state) throws IOException;

  public default int setAccessoryStateChecked(short decoder,
                                              byte port,
                                              byte state,
                                              long timeout) throws IOException, TimeoutException
  {
    Future<Byte> tmp = setAccessoryStateChecked(decoder,
                                                port,
                                                state);
    try {
      Byte result = tmp.get(timeout,
                            TimeUnit.MILLISECONDS);
      if (result != null) {
        return result;
      }
    } catch (InterruptedException ex) {
      Thread.currentThread().interrupt();
    } catch (ExecutionException ex) {
      if (ex.getCause() instanceof IOException) {
        throw (IOException) ex.getCause();
      } else {
        throw new IOException(ex.getCause());
      }
    }
    return -1;
  }

  public Future<Byte> setAccessoryStateChecked(short decoder,
                                               byte port,
                                               byte state) throws IOException;

}
