/*
 * Copyright 2020 Wolfgang Reder.
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
package at.or.reder.zcan20.stein;

import at.or.reder.zcan20.ZCAN;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;

/**
 *
 * @author Wolfgang Reder
 */
public final class StEinFactory
{

  public static StEin createDefault()
  {
    try {
      return load(StEinFactory.class.getResourceAsStream("default.csv"),
                  ConfigFormat.CSV);
    } catch (IOException ex) {
      Exceptions.printStackTrace(ex);
    }
    throw new AssertionError("Cannot load default config; check software integrity");
  }

  public static StEin load(InputStream strm,
                           ConfigFormat fmt) throws IOException
  {
    StEinConfigStoreProvider provider = getFirstProvider(fmt);
    if (provider == null) {
      throw new IOException("Cannot find provider for configFormat " + fmt.name());
    }
    return provider.load(strm);
  }

  public static StEin load(ZCAN mx10,
                           int moduleNumber,
                           long timeOut,
                           TimeUnit unit) throws IOException
  {
    throw new UnsupportedOperationException();
  }

  public static List<Integer> enumModules(ZCAN mx10,
                                          long timeOut,
                                          TimeUnit unit) throws IOException
  {
    throw new UnsupportedOperationException();
  }

  public static void store(StEin stein,
                           OutputStream os,
                           ConfigFormat fmt) throws IOException
  {
    StEinConfigStoreProvider provider = getFirstProvider(fmt);
    if (provider == null) {
      throw new IOException("Cannot find provider for configFormat " + fmt.name());
    }
    provider.store(stein,
                   os);
  }

  public static void store(StEin stein,
                           ZCAN mx10,
                           long timeOut,
                           TimeUnit unit) throws IOException
  {
    throw new UnsupportedOperationException();
  }

  public static Collection<? extends StEinConfigStoreProvider> getAllConfigProvider()
  {
    return Lookup.getDefault().lookupAll(StEinConfigStoreProvider.class);
  }

  public static List<StEinConfigStoreProvider> getConfigProvider(ConfigFormat format)
  {
    return getAllConfigProvider().stream().filter((p) -> p.getSupportedFormat() == format).collect(Collectors.toList());
  }

  public static StEinConfigStoreProvider getFirstProvider(ConfigFormat format)
  {
    return getAllConfigProvider().stream().filter((p) -> p.getSupportedFormat() == format).findFirst().orElse(null);
  }

  public static StEinConfigStoreProvider getProvider(UUID id)
  {
    return getAllConfigProvider().stream().filter((p) -> p.getId().equals(id)).findFirst().orElse(null);
  }

}
