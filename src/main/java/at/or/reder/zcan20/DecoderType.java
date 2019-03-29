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

import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 *
 * @author Wolfgang Reder
 */
public final class DecoderType implements Serializable
{

  private static final Map<Short, DecoderType> INSTANCES = new HashMap<>();
  public static final DecoderType MX62 = valueOf(202,
                                                 "MX62");
  public static final DecoderType MX63 = valueOf(203,
                                                 "MX63");
  public static final DecoderType MX64 = valueOf(204,
                                                 "MX64");
  public static final DecoderType MX64H = valueOf(205,
                                                  "MX64H");
  public static final DecoderType MX64D = valueOf(206,
                                                  "MX64D");
  public static final DecoderType MX69 = valueOf(209,
                                                 "MX69");
  public static final DecoderType MX82 = valueOf(200,
                                                 "MX82");
  public static final DecoderType MX600 = valueOf(199,
                                                  "MX600");
  public static final DecoderType MX617 = valueOf(197,
                                                  "MX617");
  public static final DecoderType MX621 = valueOf(201,
                                                  "MX621");
  public static final DecoderType MX630P2520 = valueOf(211,
                                                       "MX630-P2520");
  public static final DecoderType MX630P25K22 = valueOf(218,
                                                        "MX630-P25K22");
  public static final DecoderType MX631 = valueOf(213,
                                                  "MX631");
  public static final DecoderType MX632 = valueOf(212,
                                                  "MX632");
  public static final DecoderType MX640 = valueOf(210,
                                                  "MX640");
  public static final DecoderType MX642 = valueOf(214,
                                                  "MX642");
  public static final DecoderType MX643 = valueOf(215,
                                                  "MX643");
  public static final DecoderType MX646 = valueOf(217,
                                                  "MX646");
  public static final DecoderType MX647 = valueOf(216,
                                                  "MX647");
  public static final DecoderType MX680 = valueOf(207,
                                                  "MX680");
  public static final DecoderType MX690 = valueOf(208,
                                                  "MX690");

  public static DecoderType valueOf(int val,
                                    final String label)
  {
    Objects.requireNonNull(label,
                           "label is null");
    return INSTANCES.computeIfAbsent((short) val,
                                     (v) -> new DecoderType(v,
                                                            label));

  }

  public static DecoderType valueOf(short val)
  {
    return INSTANCES.computeIfAbsent(val,
                                     (v) -> new DecoderType(v,
                                                            Integer.toString(Short.toUnsignedInt(val))));
  }

  private final short id;
  private final String name;

  private DecoderType(short val,
                      String name)
  {
    this.id = val;
    this.name = name;
  }

  public short getId()
  {
    return id;
  }

  public String getName()
  {
    return name;
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
