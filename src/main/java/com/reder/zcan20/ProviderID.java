/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.reder.zcan20;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public final class ProviderID
{

  private static final ConcurrentMap<Integer, ProviderID> VALUES = new ConcurrentHashMap<>();

  public static final ProviderID ZIMO = valueOf(0);
  public static final ProviderID ESTWGJ = valueOf(0x10);
  public static final ProviderID STP = valueOf(0x20);
  public static final ProviderID PFUSCH = valueOf(0x21);
  public static final ProviderID TRAINCONTROLLER = valueOf(0x30);
  public static final ProviderID TRAINPROGRAMMER = valueOf(0x31);
  public static final ProviderID RAILMANAGER = valueOf(0x40);
  public static final ProviderID WOLFI = valueOf(0xff00);

  public static ProviderID valueOf(int magic)
  {
    return VALUES.computeIfAbsent(magic,
                                  ProviderID::new);
  }

  private final int magic;

  private ProviderID(int magic)
  {
    this.magic = magic;
  }

  public int getMagic()
  {
    return magic;
  }

  @Override
  public int hashCode()
  {
    int hash = 5;
    hash = 43 * hash + this.magic;
    return hash;
  }

  @Override
  @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
  public boolean equals(Object obj)
  {
    return this == obj;
  }

  @Override
  public String toString()
  {
    return "ProviderID{" + "magic=0x" + Integer.toHexString(magic) + '}';
  }

}
