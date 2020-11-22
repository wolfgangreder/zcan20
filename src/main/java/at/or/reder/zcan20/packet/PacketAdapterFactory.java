/*
 * Copyright 2017 Wolfgang Reder.
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
package at.or.reder.zcan20.packet;

import at.or.reder.zcan20.PacketSelector;
import org.openide.util.lookup.InstanceContent;

public interface PacketAdapterFactory<A extends PacketAdapter> extends InstanceContent.Convertor<Packet, A>
{

  public boolean isValid(PacketSelector selector);

  @Override
  public default String id(Packet obj)
  {
    return obj.toString();
  }

  @Override
  public default String displayName(Packet obj)
  {
    return obj.toString();
  }

}
