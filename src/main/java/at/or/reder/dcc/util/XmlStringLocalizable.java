/*
 * Copyright 2021 Wolfgang Reder.
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;

/**
 *
 * @author Wolfgang Reder
 */
@XmlRootElement(name = "localizable")
public final class XmlStringLocalizable
{

  public static final class Entry
  {

    @XmlAttribute(name = "lang")
    private String lang;
    @XmlValue
    private String value;

    public Entry()
    {
    }

    public Entry(Map.Entry<String, ? extends String> e)
    {
      lang = e.getKey();
      if (lang != null && lang.isBlank()) {
        lang = null;
      }
      value = e.getValue();
    }

  }
  private List<Entry> entries;

  public XmlStringLocalizable()
  {
    entries = new ArrayList<>();
  }

  public XmlStringLocalizable(Localizable<? extends String> init)
  {
    entries = init.getValues().entrySet().stream().map(Entry::new).collect(Collectors.toList());
  }

  public Localizable<String> toLocalizable(boolean mutable)
  {
    Localizable<String> result = new StringLocalizable(true);
    for (Entry e : entries) {
      result.addValue(e.lang,
                      e.value);
    }
    if (!mutable) {
      return result.toImutable();
    }
    return result;
  }

  @XmlElement(name = "entry")
  public List<Entry> getEntry()
  {
    return entries;
  }

  public void setEntry(List<Entry> e)
  {
    this.entries = e;
  }

}
