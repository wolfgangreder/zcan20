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
package at.or.reder.z21;

import at.or.reder.dcc.Controller;
import at.or.reder.dcc.ControllerProvider;
import at.or.reder.dcc.PropertySet;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import org.openide.util.NbBundle;
import org.openide.util.lookup.ServiceProvider;

@NbBundle.Messages({"Z21ControllerProvider_name=Roco Z21",
                    "Z21ControllerProvider_description=Stellt eine Verbindung mit einer Roco Z21 Basisstation her."})
@ServiceProvider(service = ControllerProvider.class)
public class Z21ControllerProvider implements ControllerProvider
{

  public static final UUID ID = UUID.fromString("bca0baed-6b73-4b3a-92af-82c91b394bcc");

  @Override
  public Controller createController(Map<String, String> properties) throws IllegalArgumentException, IOException
  {
    return new Z21Controller(properties);
  }

  @Override
  public UUID getId()
  {
    return ID;
  }

  @Override
  public String getName()
  {
    return Bundle.Z21ControllerProvider_name();
  }

  @Override
  public String getDescription()
  {
    return Bundle.Z21ControllerProvider_description();
  }

  @Override
  public PropertySet getPropertySet()
  {
    return new Z21PropertySet();
  }

}
