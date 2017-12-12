/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.reder.zcan20;

import java.io.IOException;

/**
 *
 * @author reder
 */
public interface TrackConfig
{

  public void readCV(int address,
                     int cv) throws IOException;

  public void writeCV(int address,
                      int cv,
                      int value) throws IOException;

}
