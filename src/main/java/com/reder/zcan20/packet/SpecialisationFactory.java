/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.reder.zcan20.packet;

import com.reder.zcan20.CommandMode;
import com.reder.zcan20.CommandGroup;
import javax.validation.constraints.NotNull;

/**
 *
 * @author Wolfgang Reder
 */
public interface SpecialisationFactory
{

  public boolean isValid(CommandGroup group,
                         int command,
                         CommandMode mode);

  public Object createSpecialisation(@NotNull Packet packet);

}
