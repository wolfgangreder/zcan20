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
package at.or.reder.dcc.cv.impl;

import at.or.reder.dcc.cv.CVEntry;
import at.or.reder.dcc.cv.CVSet;
import at.or.reder.dcc.cv.CVSetBuilder;
import at.or.reder.dcc.cv.CVSetProvider;
import at.or.reder.dcc.util.XmlDescripted;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 *
 * @author Wolfgang Reder
 */
@XmlRootElement(name = "cv-set")
public final class XmlCVSet extends XmlDescripted
{

  private UUID id;
  private final List<CVEntry> entries = new ArrayList<>();

  public XmlCVSet()
  {
  }

  public XmlCVSet(CVSet set)
  {
    super(set.getLocalized());
    id = set.getId();
    entries.addAll(set.getEntries());
  }

  public CVSet toCVSet(CVSetProvider provider)
  {
    CVSetBuilder builder = new CVSetBuilderImpl();
    builder.id(id);
    builder.addEntries(entries);
    builder.addDescriptions(toModel());
    builder.provider(provider);
    return builder.build();
  }

  @XmlAttribute(name = "id", required = true)
  @XmlJavaTypeAdapter(UUIDXmlAdapter.class)
  public UUID getId()
  {
    return id;
  }

  public void setId(UUID id)
  {
    this.id = id;
  }

  @XmlElement(name = "entry")
  @XmlJavaTypeAdapter(XmlCVEntry.Adapter.class)
  public List<CVEntry> getEntries()
  {
    return entries;
  }

}
