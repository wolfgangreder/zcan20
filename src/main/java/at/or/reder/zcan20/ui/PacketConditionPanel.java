/*
 * Copyright 2019 Wolfgang Reder.
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

import at.or.reder.zcan20.PacketSelector;
import java.awt.event.ActionListener;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import javax.swing.JPanel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class PacketConditionPanel extends JPanel
{

  private final Set<ActionListener> actionListener = new CopyOnWriteArraySet<>();
  private final DocumentListener docListener = new DocumentListener()
  {
    @Override
    public void insertUpdate(DocumentEvent e)
    {
      fireActionEvent();
    }

    @Override
    public void removeUpdate(DocumentEvent e)
    {
      fireActionEvent();
    }

    @Override
    public void changedUpdate(DocumentEvent e)
    {
      fireActionEvent();
    }

  };

  public PacketConditionPanel()
  {
    initComponents();
    edCanId.getDocument().addDocumentListener(docListener);
  }

  private void fireActionEvent()
  {

  }

  public void addActionListener(ActionListener al)
  {
    if (al != null) {
      actionListener.add(al);
    }
  }

  public void removeActionListener(ActionListener al)
  {
    actionListener.remove(al);
  }

  private int getBinCanId(String str)
  {
    StringBuilder builder = new StringBuilder();
    for (char ch : str.toCharArray()) {
      if (ch == 'x' || ch == 'X') {
        builder.append('0');
      } else if (ch == '1' || ch == '0') {
        builder.append(ch);
      }
    }
    return Integer.parseUnsignedInt(builder.toString(),
                                    2);
  }

  private int getBinCanIdMask(String str)
  {
    StringBuilder builder = new StringBuilder();
    for (char ch : str.toCharArray()) {
      if (ch == 'x' || ch == 'X') {
        builder.append('0');
      } else if (ch == '1' || ch == '0') {
        builder.append('1');
      }
    }
    return Integer.parseUnsignedInt(builder.toString(),
                                    2);
  }

  private int getHexCanId(String str)
  {
    StringBuilder builder = new StringBuilder();
    for (char ch : str.toCharArray()) {
      if (ch == 'x' || ch == 'X') {
        builder.append('0');
      } else if ((ch >= '0' && ch <= '9') || (ch >= 'a' && ch <= 'f') || (ch >= 'A' && ch <= 'F')) {
        builder.append(ch);
      }
    }
    return Integer.parseUnsignedInt(builder.toString(),
                                    16);
  }

  private int getHexCanIdMask(String str)
  {
    StringBuilder builder = new StringBuilder();
    for (char ch : str.toCharArray()) {
      if (ch == 'x' || ch == 'X') {
        builder.append('0');
      } else if ((ch >= '0' && ch <= '9') || (ch >= 'a' && ch <= 'f') || (ch >= 'A' && ch <= 'F')) {
        builder.append('F');
      }
    }
    return Integer.parseUnsignedInt(builder.toString(),
                                    16);
  }

  private int getCanId()
  {
    if (rdBin.isSelected()) {
      return getBinCanId(edCanId.getText());
    } else {
      return getHexCanId(edCanId.getText());
    }
  }

  private int getMask()
  {
    if (rdBin.isSelected()) {
      return getBinCanIdMask(edCanId.getText());
    } else {
      return getHexCanIdMask(edCanId.getText());
    }
  }

  public PacketSelector getSelector()
  {
    if (ckCanId.isSelected()) {
      return new PatternPacketSelector(getMask(),
                                       getCanId());
    }
    return null;
  }

  /**
   * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The content of
   * this method is always regenerated by the Form Editor.
   */
  @SuppressWarnings("unchecked")
  // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
  private void initComponents()
  {

    buttonGroup1 = new javax.swing.ButtonGroup();

    ckCanId.setText("CanId");
    ckCanId.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);

    buttonGroup1.add(rdHex);
    rdHex.setSelected(true);
    rdHex.setText("Hex");

    buttonGroup1.add(rdBin);
    rdBin.setText("Bin");

    edCanId.setText("XX XX XX XX");

    javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
    this.setLayout(layout);
    layout.setHorizontalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(layout.createSequentialGroup()
        .addContainerGap()
        .addComponent(ckCanId)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(edCanId, javax.swing.GroupLayout.DEFAULT_SIZE, 233, Short.MAX_VALUE)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(rdHex)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(rdBin)
        .addContainerGap())
    );
    layout.setVerticalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(ckCanId)
          .addComponent(rdHex)
          .addComponent(rdBin)
          .addComponent(edCanId, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
    );
  }// </editor-fold>//GEN-END:initComponents

  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.ButtonGroup buttonGroup1;
  private final javax.swing.JCheckBox ckCanId = new javax.swing.JCheckBox();
  private final javax.swing.JTextField edCanId = new javax.swing.JTextField();
  private final javax.swing.JRadioButton rdBin = new javax.swing.JRadioButton();
  private final javax.swing.JRadioButton rdHex = new javax.swing.JRadioButton();
  // End of variables declaration//GEN-END:variables
}
