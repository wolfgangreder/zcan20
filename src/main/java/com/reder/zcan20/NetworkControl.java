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

import java.io.IOException;
import javax.validation.constraints.NotNull;

/**
 * Functions of the NetworkGroup (0x0a).
 *
 * @author Wolfgang Reder
 */
public interface NetworkControl
{

  public long getLastPingTimestamp();

  public void setInterfaceOption(@NotNull InterfaceOptionType type,
                                 @NotNull ProviderID provider) throws IOException;

  public default void setInterfaceProviderID(@NotNull ProviderID provider) throws IOException
  {
    setInterfaceOption(InterfaceOptionType.PROVIDER,
                       provider);
  }

  public void getInterfaceOption(@NotNull InterfaceOptionType type) throws IOException;

}
