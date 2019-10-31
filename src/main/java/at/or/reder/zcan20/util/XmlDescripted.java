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
package at.or.reder.zcan20.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;
import javax.xml.bind.annotation.XmlElement;

/**
 *
 * @author Wolfgang Reder
 */
public class XmlDescripted
{

  final List<XmlResourceDescriptor> descriptors = new ArrayList<>();

  public XmlDescripted()
  {
  }

  public XmlDescripted(Map<Locale, ResourceDescription> map)
  {
    map.entrySet().
            stream().
            filter((e) -> e.getValue() != null).
            map(XmlResourceDescriptor::new).
            forEach(descriptors::add);
  }

  public Map<Locale, ResourceDescription> toMap()
  {
    return descriptors.stream().
            filter((x) -> x.getName() != null && !x.getName().isEmpty()).
            collect(Collectors.toMap(XmlResourceDescriptor::getLocale,
                                     (x) -> new ResourceDescription(x.getName(),
                                                                    x.getDescription()),
                                     (s1, s2) -> {
                                       if (s1.getDescrption() != null && !s1.getDescrption().isBlank()) {
                                         return s1;
                                       } else {
                                         return s2;
                                       }
                                     })
            );
  }

  @XmlElement(name = "descriptor")
  public List<XmlResourceDescriptor> getDescriptors()
  {
    return descriptors;
  }

}
