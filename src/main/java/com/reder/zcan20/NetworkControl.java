/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.reder.zcan20;

import java.io.IOException;
import javax.validation.constraints.NotNull;

/**
 * Functions of the NetworkGroup (0x0a).
 *
 * @author reder
 */
public interface NetworkControl
{

  public long getLastPingTimestamp();

  public void setInterfaceOption(@NotNull InterfaceOptionType type,
                                 @NotNull ProviderID provider) throws IOException;

  public default void setInterfaceProviderID(@NotNull ProviderID provider) throws IOException
  {
    setInterfaceOption(InterfaceOptionType.PROVIDER,
                       provider);
  }

  public void getInterfaceOption(@NotNull InterfaceOptionType type) throws IOException;

}
