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
package at.or.reder.zcan20.util;

import java.util.EventListener;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public final class IndexedListenerSupport<I, L extends EventListener>
{

  private final Map<I, Set<L>> listenerMap = new HashMap<>();
  private final I wildcardValue;

  public IndexedListenerSupport(I wildcardValue)
  {
    this.wildcardValue = wildcardValue;
  }

  public Set<L> getListener(I index)
  {
    Set<L> result = new HashSet<>();
    synchronized (listenerMap) {
      Set<L> tmp = listenerMap.get(index);
      if (tmp != null) {
        result.addAll(tmp);
      }
      if (wildcardValue != null) {
        tmp = listenerMap.get(wildcardValue);
        if (tmp != null) {
          result.addAll(tmp);
        }
      }
    }
    return result;
  }

  public void addEventListener(I index,
                               L listener)
  {
    if (listener != null && index != null) {
      synchronized (listenerMap) {
        listenerMap.computeIfAbsent(index,
                                    (i) -> new HashSet<>()).add(listener);
      }
    }
  }

  public void removeEventListener(I index,
                                  L listener)
  {
    if (listener != null && index != null) {
      synchronized (listenerMap) {
        Set<L> set = listenerMap.get(index);
        if (set != null) {
          set.remove(listener);
          if (set.isEmpty()) {
            listenerMap.remove(index);
          }
        }
      }
    }
  }

  public boolean isEmpty()
  {
    synchronized (listenerMap) {
      return listenerMap.isEmpty();
    }
  }

}
