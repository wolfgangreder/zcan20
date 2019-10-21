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
package at.or.reder.zcan20.packet;

import at.or.reder.zcan20.PowerPort;
import at.or.reder.zcan20.PowerState;
import java.util.Set;

/**
 *
 * @author Wolfgang Reder
 */
public interface PowerInfo extends PacketAdapter
{

  public Set<PowerState> getState();

  public default float getOutputVoltage(PowerPort out)
  {
    return Float.NaN;
  }

  public default float getOutputCurrent(PowerPort out)
  {
    return Float.NaN;
  }

  public float getInputVoltage();

  public float getInputCurrent();

  public default float getOutputPower(PowerPort out)
  {
    float i = getOutputCurrent(out);
    float u = getOutputVoltage(out);
    if (i != Float.NaN && u != Float.NaN) {
      return i * u;
    }
    return Float.NaN;
  }

  public default float getTotalOutputPower()
  {
    float p1 = getOutputPower(PowerPort.OUT_1);
    float p2 = getOutputPower(PowerPort.OUT_2);
    float total = 0;
    if (p1 != Float.NaN) {
      total += p1;
    }
    if (p2 != Float.NaN) {
      total += p2;
    }
    return total;
  }

  public default float getInputPower()
  {
    float i = getInputCurrent();
    float u = getInputVoltage();
    if (i != Float.NaN && u != Float.NaN) {
      return i * u;
    }
    return Float.NaN;
  }

  public default float getEfficiency()
  {
    return getTotalOutputPower() / getInputPower();
  }

}
