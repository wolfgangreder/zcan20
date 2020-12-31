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

import org.openide.util.NbBundle.Messages;

@Messages({"HLU_F_name=Fahren",
           "HLU_FL_name=Langsame Fahrt",
           "HLU_L_name=Langsam",
           "HLU_LU_name=Langsamer",
           "HLU_U_name=Ultra langsam",
           "HLU_UH_name=Schritttempo",
           "HLU_H_name=Halt"})
public enum HLU
{
  F("F",
    Bundle.HLU_F_name()),
  FL("FL",
     Bundle.HLU_FL_name()),
  L("L",
    Bundle.HLU_L_name()),
  LU("LU",
     Bundle.HLU_LU_name()),
  U("U",
    Bundle.HLU_U_name()),
  UH("UH",
     Bundle.HLU_UH_name()),
  H("H",
    Bundle.HLU_H_name());
  private final String magic;
  private final String name;

  private HLU(String magic,
              String name)
  {
    this.magic = magic;
    this.name = name;
  }

  public String getMagic()
  {
    return magic;
  }

  public String getName()
  {
    return name;
  }

  public static HLU valueOfMagic(String magic)
  {
    for (HLU v : values()) {
      if (v.getMagic().equals(magic)) {
        return v;
      }
    }
    return null;
  }

}
