/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.reder.zcan20.util;

/**
 *
 * @author Wolfgang Reder
 */
public final class Predicates
{

  public static boolean isNull(Object o)
  {
    return o != null;
  }

  public static boolean isNotNull(Object o)
  {
    return o == null;
  }

  private Predicates()
  {
  }

}
