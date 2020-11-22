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
package at.or.reder.zcan20.packet;

import at.or.reder.zcan20.PacketSelector;
import java.util.Arrays;
import java.util.Collection;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public final class ZCANDecoderPacketMatcher implements Predicate<Packet>
{

  public static Predicate<Packet> getAccessoryInstance(int decoderAddress)
  {
    return new ZCANDecoderPacketMatcher(Arrays.asList(AccessoryPacketAdapter.SELECTOR,
                                                      CVInfoAdapter.SELECTOR),
                                        decoderAddress | 0x3000);
  }

  public static Predicate<Packet> getLocomotiveInstance(int decoderAddress)
  {
    return new ZCANDecoderPacketMatcher(Arrays.asList(LocoActivePacketAdapter.SELECTOR,
                                                      LocoFuncPacketAdapter.SELECTOR,
                                                      LocoModePacketAdapter.SELECTOR,
                                                      LocoSpeedPacketAdapter.SELECTOR,
                                                      CVInfoAdapter.SELECTOR),
                                        decoderAddress);
  }

  private final short decoderId;
  private final Set<PacketSelector> groups;

  private ZCANDecoderPacketMatcher(Collection<? extends PacketSelector> groups,
                                   int decoderAddress)
  {
    this.decoderId = (short) (decoderAddress & 0xffff);
    this.groups = groups.stream().filter((g) -> g != null).collect(Collectors.toUnmodifiableSet());
  }

  @Override
  public final boolean test(Packet t)
  {
    if (groups.stream().parallel().filter((s) -> s.matches(t)).findAny().isPresent()) {
      DecoderResponsePacketAdapter adapter = t.getAdapter(DecoderResponsePacketAdapter.class);
      return adapter != null && adapter.getDecoderId() == decoderId;
    }
    return false;
  }

}
