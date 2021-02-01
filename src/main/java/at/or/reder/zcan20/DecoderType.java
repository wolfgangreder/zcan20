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
package at.or.reder.zcan20;

import static at.or.reder.dcc.util.DCCUtils.LOGGER;
import java.io.IOException;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.stream.Collectors;

/**
 *
 * @author Wolfgang Reder
 */
public final class DecoderType implements Serializable
{

  private static final Map<Integer, DecoderType> INSTANCES = new HashMap<>();

  public static DecoderType valueOf(int val,
                                    final String label)
  {
    Objects.requireNonNull(label,
                           "label is null");
    return INSTANCES.computeIfAbsent(val,
                                     (v) -> new DecoderType(v,
                                                            label,
                                                            null));

  }

  public static DecoderType valueOf(int val)
  {
    return INSTANCES.computeIfAbsent(val,
                                     (v) -> new DecoderType(v,
                                                            Integer.toUnsignedString(v),
                                                            null));
  }

  static {
    try {
      Properties props = new Properties();
      props.load(DecoderType.class.getResourceAsStream("decoder.properties"));
      for (Object k : props.keySet()) {
        if (k != null) {
          int i = -1;
          try {
            i = Integer.parseInt(k.toString());
          } catch (Throwable th) {
          }
          if (i != -1) {
            String val = props.getProperty(k.toString());
            if (val != null && !val.isBlank()) {
              INSTANCES.put(i,
                            new DecoderType(i,
                                            val,
                                            null));
            }
          }
        }
      }
    } catch (IOException ex) {
      LOGGER.log(Level.SEVERE,
                 "DecoderType:init",
                 ex);
    }
  }

  private final int id;
  private final String name;
  private final List<String> alternateNames;
  private final boolean sound;

  private DecoderType(int val,
                      String name,
                      Collection<? extends CharSequence> alternateNames)
  {
    this.id = val;
    if (name.endsWith("_S")) {
      sound = true;
      this.name = name.substring(0,
                                 name.length() - 2);
    } else {
      this.name = name;
      sound = false;
    }
    TreeSet<String> tmpNames = null;
    if (alternateNames != null && !alternateNames.isEmpty()) {
      Comparator<Object> comp = Collator.getInstance();
      tmpNames = alternateNames.stream().
              filter((n) -> n != null).
              map(Object::toString).
              collect(Collectors.toCollection(() -> new TreeSet<>(comp)));
      tmpNames.add(name);
    }
    if (tmpNames == null || tmpNames.isEmpty()) {
      this.alternateNames = Collections.singletonList(name);
    } else {
      this.alternateNames = Collections.unmodifiableList(new ArrayList<>(tmpNames));
    }
  }

  public int getId()
  {
    return id;
  }

  public boolean isSound()
  {
    return sound;
  }

  public String getName()
  {
    return name;
  }

  public List<String> getAlternateNames()
  {
    return alternateNames;
  }

  @Override
  public int hashCode()
  {
    int hash = 7;
    hash = 73 * hash + this.id;
    return hash;
  }

  @Override
  public boolean equals(Object obj)
  {
    return this == obj;
  }

  private Object readResolve() throws ObjectStreamException
  {
    return valueOf(id);
  }

  @Override
  public String toString()
  {
    return "Decoder " + name;
  }

}
