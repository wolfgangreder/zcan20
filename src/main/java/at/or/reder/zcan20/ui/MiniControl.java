/*
 * Copyright 2019 wolfi.
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
package at.or.reder.zcan20.ui;

import at.or.reder.zcan20.CommandGroup;
import at.or.reder.dcc.PowerPort;
import at.or.reder.zcan20.ZCAN;
import at.or.reder.zcan20.packet.Packet;
import at.or.reder.zcan20.packet.PowerInfo;
import javax.swing.JFrame;
import org.openide.util.NbBundle.Messages;

@Messages({"# {0} - voltage1",
           "# {1} - current1",
           "# {2} - power1",
           "# {3} - voltage2",
           "# {4} - current2",
           "# {5} - power2",
           "# {6} - voltageIn",
           "# {7} - currentIn",
           "# {8} - powerIn",
           "MiniControl_fmt_state=<html>Schiene: {0,number,0.00}V {1,number,0.000}A "
           + "{2,number,0.00}W<br>Service: {3,number,0.00}V {4,number,0.000}A "
           + "{5,number,0.00}W<br>Eingang: {6,number,0.00}V {7,number,0.000}A " + "{8,number,0.00}W"})
public class MiniControl extends JFrame
{

  private final ZCAN device;
  float current1 = -1;
  float current2 = -1;
  float voltage1 = -1;
  float voltage2 = -1;

  public MiniControl(ZCAN device)
  {
    this.device = device;
    initComponents();
    device.addPacketListener(CommandGroup.CONFIG,
                             this::onPowerNotify);
  }

  private void onPowerNotify(ZCAN device,
                             Packet packet)
  {
    PowerInfo power = packet.getAdapter(PowerInfo.class);
    if (power != null) {
      String s = Bundle.MiniControl_fmt_state(power.getOutputVoltage(PowerPort.OUT_1),
                                              power.getOutputCurrent(PowerPort.OUT_1),
                                              power.getOutputPower(PowerPort.OUT_1),
                                              power.getOutputVoltage(PowerPort.OUT_2),
                                              power.getOutputCurrent(PowerPort.OUT_2),
                                              power.getOutputPower(PowerPort.OUT_2),
                                              power.getInputVoltage(),
                                              power.getInputCurrent(),
                                              power.getInputPower());
      lbPower.setText(s);
    }
  }

  /**
   * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The content of
   * this method is always regenerated by the Form Editor.
   */
  @SuppressWarnings("unchecked")
  // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
  private void initComponents()
  {

    jLabel1 = new javax.swing.JLabel();

    jLabel1.setText(org.openide.util.NbBundle.getMessage(MiniControl.class, "MiniControl.jLabel1.text")); // NOI18N

    setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
    setTitle(org.openide.util.NbBundle.getMessage(MiniControl.class, "MiniControl.title")); // NOI18N

    lbPower.setText(org.openide.util.NbBundle.getMessage(MiniControl.class, "MiniControl.lbPower.text")); // NOI18N
    getContentPane().add(lbPower, java.awt.BorderLayout.PAGE_START);

    setSize(new java.awt.Dimension(408, 333));
    setLocationRelativeTo(null);
  }// </editor-fold>//GEN-END:initComponents

  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.JLabel jLabel1;
  private final javax.swing.JLabel lbPower = new javax.swing.JLabel();
  // End of variables declaration//GEN-END:variables
}
