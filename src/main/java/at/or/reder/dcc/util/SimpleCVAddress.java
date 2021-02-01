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
package at.or.reder.dcc.util;

import at.or.reder.dcc.cv.CVAddress;
import at.or.reder.dcc.cv.CVType;
import at.or.reder.dcc.cv.CVUtils;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 *
 * @author Wolfgang Reder
 */
public final class SimpleCVAddress implements CVAddress
{

  private static final Map<Integer, CVAddress> cache = new ConcurrentHashMap<>();
  private final int address;
  private final Map<CVType, Integer> bankAddresses;
  private final long flatAddress;

  public static CVAddress valueOf(int address)
  {
    if (address <= 1024) {
      return cache.computeIfAbsent(address,
                                   SimpleCVAddress::new);
    } else {
      return new SimpleCVAddress(address);
    }
  }

  public static CVAddress valueOf(int address,
                                  Map<CVType, Integer> bankAddresses)
  {
    if (bankAddresses == null || bankAddresses.isEmpty() || bankAddresses.values().stream().filter((i) -> i != 0).findAny().
            isEmpty()) {
      return valueOf(address);
    } else {
      return new SimpleCVAddress(address,
                                 bankAddresses);
    }
  }

  private SimpleCVAddress(int address)
  {
    this(address,
         null);
  }

  private SimpleCVAddress(int address,
                          Map<CVType, Integer> bankAddresses)
  {
    this.address = address;
    if (bankAddresses != null && !bankAddresses.isEmpty()) {
      Map<CVType, Integer> tmp = bankAddresses.entrySet().stream().
              filter(this::isValidBankAddress).
              collect(Collectors.toMap((Map.Entry<CVType, Integer> e) -> e.getKey(),
                                       (Map.Entry<CVType, Integer> e) -> e.getValue()));
      if (tmp.isEmpty()) {
        this.bankAddresses = Collections.emptyMap();
      } else {
        this.bankAddresses = Collections.unmodifiableMap(tmp);
      }
    } else {
      this.bankAddresses = Collections.emptyMap();
    }
    flatAddress = buildFlatAddress();
  }

  private long buildFlatAddress()
  {
    long result = 0;
    int b = getBankAddress(CVType.INDEX_3);
    if (b != -1) {
      result += b & 0xff;
    }
    result <<= 8;
    b = getBankAddress(CVType.INDEX_2);
    if (b != -1) {
      result += b & 0xff;
    }
    result <<= 8;
    b = getBankAddress(CVType.INDEX_1);
    if (b != -1) {
      result += b & 0xff;
    }
    result <<= 8;
    b = getBankAddress(CVType.INDEX_0);
    if (b != -1) {
      result += b & 0xff;
    }
    result <<= 16;
    return result + (address & 0xffff);
  }

  private boolean isValidBankAddress(Map.Entry<CVType, Integer> e)
  {
    return e != null && CVUtils.isBankAddress(e.getKey()) && e.getValue() != null && e.getValue() > 0;
  }

  @Override
  public int getAddress()
  {
    return address;
  }

  @Override
  public long getFlatAddress()
  {
    return flatAddress;
  }

  @Override
  public Map<CVType, Integer> getBankAddresses()
  {
    return bankAddresses;
  }

  @Override
  public String toString()
  {
    return "CVAddress{" + address + '}';
  }

  @Override
  public int hashCode()
  {
    int hash = 3;
    hash = 79 * hash + (int) (this.flatAddress ^ (this.flatAddress >>> 32));
    return hash;
  }

  @Override
  public boolean equals(Object obj)
  {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final SimpleCVAddress other = (SimpleCVAddress) obj;
    if (this.flatAddress != other.flatAddress) {
      return false;
    }
    return true;
  }

}
