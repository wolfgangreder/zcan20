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
import java.util.concurrent.TimeoutException;

/**
 *
 * @author Wolfgang Reder
 */
public interface PowerControl extends BaseControl
{

  public static final String PROP_POWERMODE = "powerMode";

  public PowerMode getPowerMode(PowerPort port) throws IOException, TimeoutException;

  public void postPowerModeRequest(PowerPort port) throws IOException;

  public void setPowerMode(PowerPort port,
                           PowerMode mode) throws IOException;

  public void addPowerEventListener(PowerEventListener listener);

  public void removePowerEventListener(PowerEventListener listener);

}
