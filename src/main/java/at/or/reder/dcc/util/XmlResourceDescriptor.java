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
package at.or.reder.dcc.util;

import java.util.Map;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlValue;

/**
 *
 * @author Wolfgang Reder
 */
public final class XmlResourceDescriptor
{

  private String loc;
  private String name;
  private String description;

  public XmlResourceDescriptor()
  {
  }

  public XmlResourceDescriptor(Map.Entry<String, ? extends ResourceDescription> e)
  {
    this(e.getKey(),
         e.getValue().getName(),
         e.getValue().getDescrption());
  }

  public XmlResourceDescriptor(String loc,
                               String name,
                               String desc)
  {
    this.loc = loc;
    this.name = name;
    this.description = desc;
  }

  @XmlAttribute(name = "lang")
  public String getLoc()
  {
    return loc;
  }

  public void setLoc(String loc)
  {
    this.loc = loc;
  }

  @XmlAttribute(name = "name")
  public String getName()
  {
    return name;
  }

  public void setName(String name)
  {
    this.name = name;
  }

  @XmlValue
  public String getDescription()
  {
    return description;
  }

  public void setDescription(String description)
  {
    this.description = description;
  }

  public ResourceDescription toResourceDescription()
  {
    return new ResourceDescription(name,
                                   description);
  }

}
