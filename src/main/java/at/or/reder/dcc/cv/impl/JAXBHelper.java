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

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import org.openide.util.Exceptions;

/**
 *
 * @author Wolfgang Reder
 */
public final class JAXBHelper
{

  private static final class ContextHolder
  {

    private static final JAXBContext ctx;

    static {
      JAXBContext tmp = null;
      try {
        tmp = JAXBContext.newInstance("at.or.reder.dcc.cv.impl:" + "at.or.reder.zcan20.util");
      } catch (JAXBException ex) {
        Exceptions.printStackTrace(ex);
      }
      ctx = tmp;
    }

  }

  public static JAXBContext getJAXBContext()
  {
    return ContextHolder.ctx;
  }

  private JAXBHelper()
  {
  }

}
