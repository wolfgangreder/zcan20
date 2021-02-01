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

import at.or.reder.dcc.Decoder;
import at.or.reder.dcc.cv.CVAddress;
import at.or.reder.dcc.cv.CVFactories;
import at.or.reder.dcc.cv.CVSet;
import at.or.reder.dcc.cv.CVSetProvider;
import at.or.reder.dcc.cv.CVValue;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.NbBundle.Messages;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Wolfgang Reder
 */
@ServiceProvider(service = CVSetProvider.class)
@Messages({"BasicCVSetProvider_name=DCC Basic CV Set",
           "BasicCVSetProvider_desc=Basis CV Set nach RCN-225"})
public final class BasicCVSetProvider implements CVSetProvider
{

  private List<CVSet> cvsets;

  @Override
  public String getName()
  {
    return Bundle.BasicCVSetProvider_name();
  }

  @Override
  public String getDescription()
  {
    return Bundle.BasicCVSetProvider_desc();
  }

  @Override
  public List<Integer> getSupportedManufacturers()
  {
    return Collections.emptyList();
  }

  @Override
  public List<CVSet> getCVSets()
  {
    synchronized (this) {
      if (cvsets == null) {
        try (InputStream is = getClass().getResourceAsStream("basic.xml")) {
          CVSet set = CVFactories.loadCVSetFromXML(this,
                                                   is);
          if (set != null) {
            cvsets = Collections.singletonList(set);
          } else {
            cvsets = Collections.emptyList();
          }
        } catch (IOException ex) {
          Exceptions.printStackTrace(ex);
          cvsets = Collections.emptyList();
        }
      }
      return cvsets;
    }
  }

  @Override
  public List<Decoder> getDecoders()
  {
    return Collections.emptyList();
  }

  @Override
  public Decoder findMatchingDecoder(Map<CVAddress, CVValue> values)
  {
    return null;
  }

  @Override
  public Lookup getLookup()
  {
    return Lookup.EMPTY;
  }

}
