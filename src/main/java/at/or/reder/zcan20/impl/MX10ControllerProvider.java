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
package at.or.reder.zcan20.impl;

import at.or.reder.dcc.Controller;
import at.or.reder.dcc.ControllerProvider;
import at.or.reder.dcc.PropertySet;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import org.openide.util.NbBundle.Messages;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Wolfgang Reder
 */
@Messages({"MX10ControllerProvider_name=Zimo MX10",
           "MX10ControllerProvider_description=Stellt eine Verbindung mit einem Zimo MX10 her."})
@ServiceProvider(service = ControllerProvider.class)
public final class MX10ControllerProvider implements ControllerProvider
{

  public static final UUID ID = UUID.fromString("761e3de9-d8b0-46ee-b44b-738671f15b88");

  @Override
  public Controller createController(Map<String, String> properties) throws IllegalArgumentException, IOException
  {
    return new MX10Control(properties);
  }

  @Override
  public UUID getId()
  {
    return ID;
  }

  @Override
  public String getName()
  {
    return Bundle.MX10ControllerProvider_name();
  }

  @Override
  public String getDescription()
  {
    return Bundle.MX10ControllerProvider_description();
  }

  @Override
  public PropertySet getPropertySet()
  {
    return new MX10PropertiesSet();
  }

}
