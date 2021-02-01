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
package at.or.reder.dcc.cv;

import at.or.reder.dcc.cv.impl.CVBitDescriptorBuilderImpl;
import at.or.reder.dcc.cv.impl.CVEntryBuilderImpl;
import at.or.reder.dcc.cv.impl.CVSetBuilderImpl;
import at.or.reder.dcc.cv.impl.CVValueImpl;
import at.or.reder.dcc.cv.impl.JAXBHelper;
import at.or.reder.dcc.cv.impl.XmlCVSet;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.validation.constraints.NotNull;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

/**
 *
 * @author Wolfgang Reder
 */
public final class CVFactories
{

  public static CVValue createValue(@NotNull CVValueState state,
                                    int value,
                                    CVEntry entry)
  {
    return new CVValueImpl(state,
                           value,
                           entry);
  }

  public static CVBitDescriptorBuilder createBitDescriptorBuilder()
  {
    return new CVBitDescriptorBuilderImpl();
  }

  public static CVEntryBuilder createEntryBuilder()
  {
    return new CVEntryBuilderImpl();
  }

  public static CVSetBuilder createCVSetBuilder()
  {
    return new CVSetBuilderImpl();
  }

  public static void storeCVSetToXML(CVSet set,
                                     OutputStream out) throws IOException
  {
    try {
      XmlCVSet xs = new XmlCVSet(set);
      JAXBContext ctx = JAXBHelper.getJAXBContext();
      Marshaller m = ctx.createMarshaller();
      m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT,
                    true);
      m.marshal(xs,
                out);
    } catch (JAXBException ex) {
      throw new IOException(ex);
    }
  }

  public static CVSet loadCVSetFromXML(CVSetProvider provider,
                                       InputStream is) throws IOException
  {
    try {
      JAXBContext ctx = JAXBHelper.getJAXBContext();
      Unmarshaller u = ctx.createUnmarshaller();
      Object tmp = u.unmarshal(is);
      if (tmp instanceof XmlCVSet) {
        return ((XmlCVSet) tmp).toCVSet(provider);
      }
      return null;
    } catch (JAXBException ex) {
      throw new IOException(ex);
    }
  }

  private CVFactories()
  {
  }

}
