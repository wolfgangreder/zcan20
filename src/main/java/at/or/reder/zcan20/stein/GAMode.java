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

import org.openide.util.NbBundle;
import org.openide.util.NbBundle.Messages;

@Messages({"GAMode_MODE_0_name=Fixe Geschwindigkeit",
           "GAMode_MODE_0_desc=",
           "GAMode_MODE_1_name=Automatische Gleiseinfahrt",
           "GAMode_MODE_1_desc=",
           "GAMode_MODE_3_name=Computergesteuert",
           "GAMode_MODE_3_desc=",
           "GAMode_MODE_4_name=extern gesteuert MX9",
           "GAMode_MODE_4_desc="})
public enum GAMode
{

  MODE_0(0,
         Bundle.GAMode_MODE_0_name()),
  MODE_1(1,
         Bundle.GAMode_MODE_1_name()),
  MODE_3(3,
         Bundle.GAMode_MODE_3_name()),
  MODE_4(4,
         Bundle.GAMode_MODE_4_name());
  private final int magic;
  private final String name;

  private GAMode(int magic,
                 String name)
  {
    this.magic = magic;
    this.name = name;
  }

  public int getMagic()
  {
    return magic;
  }

  public String getName()
  {
    return name;
  }

  public String getDescription()
  {
    return NbBundle.getMessage(GAMode.class,
                               "GAMode_" + name() + "_desc");
  }

}
