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

import at.or.reder.dcc.util.Predicates;
import at.or.reder.zcan20.PacketSelector;
import at.or.reder.zcan20.packet.Packet;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author Wolfgang Reder
 */
public final class ProxyPacketSelector implements PacketSelector
{

  private final List<PacketSelector> predicates;

  public ProxyPacketSelector(PacketSelector a)
  {
    this(Collections.singleton(a));
  }

  public ProxyPacketSelector(PacketSelector a,
                             PacketSelector b)
  {
    this(Arrays.asList(a,
                       b));
  }

  public ProxyPacketSelector(PacketSelector... array)
  {
    this(Arrays.asList(array));
  }

  public ProxyPacketSelector(Collection<? extends PacketSelector> predicates)
  {
    this.predicates = predicates.stream().filter(Predicates::isNotNull).collect(Collectors.toUnmodifiableList());
  }

  @Override
  public boolean matches(Packet packet)
  {
    return predicates.stream().parallel().filter((p) -> p.matches(packet)).findAny().isPresent();
  }

  @Override
  public boolean test(PacketSelector s)
  {
    return predicates.stream().parallel().filter((p) -> p.test(s)).findAny().isPresent();
  }

}
