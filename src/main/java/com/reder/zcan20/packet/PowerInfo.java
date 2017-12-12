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
package com.reder.zcan20.packet;

import com.reder.zcan20.PowerOutput;
import com.reder.zcan20.PowerState;
import java.util.Set;

/**
 *
 * @author Wolfgang Reder
 */
public interface PowerInfo extends PacketAdapter
{

  public PowerOutput getOutput();

  public Set<PowerState> getState();

  public default float getVoltage()
  {
    return Float.NaN;
  }

  public default float getCurrent()
  {
    return Float.NaN;
  }

}
