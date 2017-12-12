/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.reder.zcan20;

import com.reder.zcan20.packet.Packet;
import javax.validation.constraints.NotNull;

/**
 *
 * @author Wolfgang Reder
 */
@FunctionalInterface
public interface PacketListener
{

  public void onPacket(@NotNull ZCAN connection,
                       @NotNull Packet packet);

}
