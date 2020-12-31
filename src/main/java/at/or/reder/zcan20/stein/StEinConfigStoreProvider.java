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

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import javax.validation.constraints.NotNull;

/**
 *
 * @author Wolfgang Reder
 */
public interface StEinConfigStoreProvider
{

  public static enum Features
  {
    LOAD, STORE;
  }
  public static final String KEY_WRITECAPTIONS = "StEinConfigStoreProvider.writeCaptions";

  public UUID getId();

  public Set<Features> getSupportedFeatures();

  public default StEin load(@NotNull Object input) throws IOException
  {
    return load(input,
                null);
  }

  public StEin load(@NotNull Object input,
                    Map<String, Object> params) throws IOException;

  public default void store(@NotNull StEin stein,
                            @NotNull Object output) throws IOException
  {
    store(stein,
          output,
          null);
  }

  public void store(@NotNull StEin stein,
                    @NotNull Object output,
                    Map<String, Object> storeParams) throws IOException;

  public ConfigFormat getSupportedFormat();

  public default String getGenericFlavor()
  {
    return null;
  }

}
