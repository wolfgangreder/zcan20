/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.reder.zcan20;

import java.io.IOException;

/**
 *
 * @author Wolfgang Reder
 */
public interface LocoManagement
{

  public void getMode(int address) throws IOException;

  public void takeOwnership(int address) throws IOException;

}
