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

@Messages({"ObjectClass_GATYP_magic=GATYP",
           "ObjectClass_GATYP_name=Gleisanschluss Typbeschreibung",
           "ObjectClass_GATYP_desc=",
           "ObjectClass_GA_magic=GA",
           "ObjectClass_GA_name=Gleisanschluss",
           "ObjectClass_GA_desc="})
public enum ObjectClass
{
  GATYP(Bundle.ObjectClass_GATYP_magic(),
        true,
        Bundle.ObjectClass_GATYP_name()),
  GA(Bundle.ObjectClass_GA_magic(),
     false,
     Bundle.ObjectClass_GA_name());
  private final String sheetName;
  private final String name;
  private final boolean template;

  private ObjectClass(String sheetName,
                      boolean template,
                      String name)
  {
    this.sheetName = sheetName;
    this.template = template;
    this.name = name;
  }

  public String getMagic()
  {
    return sheetName;
  }

  public String getName()
  {
    return name;
  }

  public boolean isTemplate()
  {
    return template;
  }

  public String getDescription()
  {
    return NbBundle.getMessage(ObjectClass.class,
                               "ObjectClass_" + name() + "_desc");
  }

  public static ObjectClass valueOfMagic(String magic)
  {
    for (ObjectClass c : values()) {
      if (c.getMagic().equals(magic)) {
        return c;
      }
    }
    return null;
  }

}
