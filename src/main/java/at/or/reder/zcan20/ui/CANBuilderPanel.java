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

import at.or.reder.dcc.util.DCCUtils;
import at.or.reder.zcan20.CanId;
import at.or.reder.zcan20.CommandGroup;
import at.or.reder.zcan20.CommandMode;
import at.or.reder.zcan20.ZCANFactory;
import at.or.reder.zcan20.impl.CANMarshaller;
import at.or.reder.zcan20.packet.Packet;
import at.or.reder.zcan20.packet.PacketBuilder;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.ParseException;
import javax.swing.ButtonGroup;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.LayoutStyle;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;

public final class CANBuilderPanel extends JPanel
{

  private final DocumentListener docListener = new DocumentListener()
  {
    @Override
    public void insertUpdate(DocumentEvent e)
    {
      setControlState();
    }

    @Override
    public void removeUpdate(DocumentEvent e)
    {
      setControlState();
    }

    @Override
    public void changedUpdate(DocumentEvent e)
    {
      setControlState();
    }

  };

  public CANBuilderPanel()
  {
    initComponents();
    edCommand.getDocument().addDocumentListener(docListener);
    edCommandGroup.getDocument().addDocumentListener(docListener);
    edData.getDocument().addDocumentListener(docListener);
    edPacket.getDocument().addDocumentListener(docListener);
    edSender.getDocument().addDocumentListener(docListener);
    setControlState();
  }

  private ByteBuffer parseEdit(JTextField field)
  {
    try {
      return DCCUtils.hexString2ByteBuffer(field.getText(),
                                        null,
                                        ' ').rewind();
    } catch (ParseException ex) {
    }
    return null;
  }

  private CommandMode getCommandMode()
  {
    if (rdAck.isSelected()) {
      return CommandMode.ACK;
    }
    if (rdCommand.isSelected()) {
      return CommandMode.COMMAND;
    }
    if (rdEvent.isSelected()) {
      return CommandMode.EVENT;
    }
    if (rdRequest.isSelected()) {
      return CommandMode.REQUEST;
    }
    return null;
  }

  private void setCommandMode(CommandMode mode)
  {
    rdAck.setSelected(mode == CommandMode.ACK);
    rdCommand.setSelected(mode == CommandMode.COMMAND);
    rdEvent.setSelected(mode == CommandMode.EVENT);
    rdRequest.setSelected(mode == CommandMode.REQUEST);
  }

  private void setEdit(ByteBuffer buffer,
                       JTextField field)
  {
    StringBuilder builder = new StringBuilder();
    DCCUtils.byteBuffer2HexString(buffer,
                               builder,
                               ' ');
    field.setText(builder.toString());
  }

  private boolean checkCommandGroup()
  {
    ByteBuffer buffer = parseEdit(edCommandGroup);
    return buffer != null && buffer.limit() == 1;
  }

  private boolean checkCommand()
  {
    ByteBuffer buffer = parseEdit(edCommand);
    return buffer != null && buffer.limit() == 1;
  }

  private boolean checkSender()
  {
    ByteBuffer buffer = parseEdit(edSender);
    return buffer != null && buffer.limit() == 2;
  }

  private boolean checkMode()
  {
    return getCommandMode() != null;
  }

  private boolean checkDLC()
  {
    int dlc = ((Number) spDLC.getValue()).intValue();
    return dlc >= 0 && dlc <= 8;
  }

  private boolean checkData()
  {
    ByteBuffer buffer = parseEdit(edData);
    return buffer == null || buffer.limit() <= 8;
  }

  private boolean checkComponentsValid()
  {
    return checkCommandGroup() && checkCommand() && checkMode() && checkDLC() && checkData() && checkSender();
  }

  private boolean checkPacketValid()
  {
    ByteBuffer buffer = parseEdit(edPacket);
    return buffer != null && buffer.limit() > 4;
  }

  private void setControlState()
  {
    btToPacket.setEnabled(checkComponentsValid());
    btToComponents.setEnabled(checkPacketValid());
  }

  private void toPacket()
  {
    ByteBuffer buffer = parseEdit(edSender);
    PacketBuilder builder = ZCANFactory.createPacketBuilder(buffer.getShort());
    buffer = parseEdit(edCommand);
    builder.command(buffer.get());
    buffer = parseEdit(edCommandGroup);
    builder.commandGroup(CommandGroup.valueOf(buffer.get()));
    builder.commandMode(getCommandMode());
    buffer = parseEdit(edData);
    builder.data(buffer);
    buffer = ByteBuffer.allocate(1500);
    Packet packet = builder.build();
    int numBytes = CANMarshaller.marshalPacket(packet,
                                               buffer);
    buffer.limit(numBytes);
    buffer.rewind();
    setEdit(buffer,
            edPacket);
    buffer.rewind();
    buffer.limit(4);
    CanId canId = packet.getCanId();
    buffer.clear();
    buffer.putInt(canId.intValue());
    buffer.flip();
    setEdit(buffer,
            edCanId);
    setControlState();
  }

  private void setEdit(int i,
                       int numDigits,
                       JTextField field)
  {
    StringBuilder builder = new StringBuilder();
    ByteBuffer buffer = ByteBuffer.allocate(4);
    buffer.putInt(i);
    buffer.rewind();
    buffer.position(4 - (numDigits / 2));
    DCCUtils.byteBuffer2HexString(buffer,
                               builder,
                               ':');
    field.setText(builder.toString());
  }

  private void toComponents()
  {
    try {
      ByteBuffer buffer = parseEdit(edPacket);
      Packet packet = CANMarshaller.unmarshalPacket(buffer);
      setEdit(packet.getCommand(),
              2,
              edCommand);
      spDLC.setValue(packet.getDLC());
      setEdit(packet.getSenderNID() & 0xffff,
              4,
              edSender);
      setEdit(packet.getCommandGroup().getMagic(),
              2,
              edCommandGroup);
      setEdit(packet.getData(),
              edData);
      setEdit(packet.getCanId().intValue(),
              8,
              edCanId);
      setCommandMode(packet.getCommandMode());
    } catch (IOException ex) {
      Exceptions.printStackTrace(ex);
    } finally {
      setControlState();
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

    final ButtonGroup buttonGroup1 = new ButtonGroup();
    JLabel jLabel1 = new JLabel();
    JLabel jLabel2 = new JLabel();
    JLabel jLabel3 = new JLabel();
    JLabel jLabel4 = new JLabel();
    JLabel jLabel5 = new JLabel();
    JLabel jLabel6 = new JLabel();
    JLabel jLabel7 = new JLabel();


    jLabel1.setText(NbBundle.getMessage(CANBuilderPanel.class, "CANBuilderPanel.jLabel1.text")); // NOI18N

    edCommandGroup.setColumns(8);

    jLabel2.setText(NbBundle.getMessage(CANBuilderPanel.class, "CANBuilderPanel.jLabel2.text")); // NOI18N

    edCommand.setColumns(8);

    buttonGroup1.add(rdCommand);
    rdCommand.setText(NbBundle.getMessage(CANBuilderPanel.class, "CANBuilderPanel.rdCommand.text")); // NOI18N

    buttonGroup1.add(rdRequest);
    rdRequest.setText(NbBundle.getMessage(CANBuilderPanel.class, "CANBuilderPanel.rdRequest.text")); // NOI18N

    buttonGroup1.add(rdEvent);
    rdEvent.setText(NbBundle.getMessage(CANBuilderPanel.class, "CANBuilderPanel.rdEvent.text")); // NOI18N

    buttonGroup1.add(rdAck);
    rdAck.setText(NbBundle.getMessage(CANBuilderPanel.class, "CANBuilderPanel.rdAck.text")); // NOI18N

    jLabel3.setText(NbBundle.getMessage(CANBuilderPanel.class, "CANBuilderPanel.jLabel3.text")); // NOI18N

    jLabel4.setText(NbBundle.getMessage(CANBuilderPanel.class, "CANBuilderPanel.jLabel4.text")); // NOI18N

    edSender.setColumns(8);

    btToPacket.setText(NbBundle.getMessage(CANBuilderPanel.class, "CANBuilderPanel.btToPacket.text")); // NOI18N
    btToPacket.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent evt)
      {
        btToPacketActionPerformed(evt);
      }
    });

    jLabel5.setText(NbBundle.getMessage(CANBuilderPanel.class, "CANBuilderPanel.jLabel5.text")); // NOI18N

    spDLC.setModel(new SpinnerNumberModel(0, 0, 8, 1));

    edData.setColumns(25);

    jLabel6.setText(NbBundle.getMessage(CANBuilderPanel.class, "CANBuilderPanel.jLabel6.text")); // NOI18N

    btToComponents.setText(NbBundle.getMessage(CANBuilderPanel.class, "CANBuilderPanel.btToComponents.text")); // NOI18N
    btToComponents.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent evt)
      {
        btToComponentsActionPerformed(evt);
      }
    });

    jLabel7.setText(NbBundle.getMessage(CANBuilderPanel.class, "CANBuilderPanel.jLabel7.text")); // NOI18N

    edCanId.setEditable(false);
    edCanId.setColumns(8);

    GroupLayout layout = new GroupLayout(this);
    setLayout(layout);
    layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
      .addGroup(layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
          .addGroup(layout.createSequentialGroup()
            .addComponent(btToPacket)
            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(btToComponents)
            .addGap(0, 0, Short.MAX_VALUE))
          .addGroup(layout.createSequentialGroup()
            .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
              .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                  .addComponent(jLabel2)
                  .addComponent(jLabel1)
                  .addComponent(jLabel4))
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                  .addGroup(layout.createSequentialGroup()
                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                      .addComponent(edCommandGroup, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                      .addComponent(edCommand, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addGap(18, 18, 18)
                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                      .addComponent(rdCommand)
                      .addComponent(rdEvent))
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                      .addComponent(rdRequest)
                      .addComponent(rdAck)))
                  .addGroup(layout.createSequentialGroup()
                    .addComponent(edSender, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                    .addComponent(jLabel5)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(spDLC, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
                .addGap(0, 73, Short.MAX_VALUE))
              .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                  .addComponent(jLabel7)
                  .addComponent(jLabel3))
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                  .addComponent(edPacket)
                  .addComponent(edCanId)))
              .addGroup(layout.createSequentialGroup()
                .addComponent(jLabel6)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(edData)))
            .addContainerGap())))
    );
    layout.setVerticalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
      .addGroup(layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
          .addComponent(jLabel1)
          .addComponent(edCommandGroup, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
          .addComponent(rdCommand)
          .addComponent(rdRequest))
        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
          .addComponent(jLabel2)
          .addComponent(edCommand, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
          .addComponent(rdEvent)
          .addComponent(rdAck))
        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
          .addComponent(jLabel4)
          .addComponent(edSender, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
          .addComponent(jLabel5)
          .addComponent(spDLC, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
        .addGap(9, 9, 9)
        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
          .addComponent(jLabel6)
          .addComponent(edData, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
        .addGap(18, 18, 18)
        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
          .addComponent(btToPacket)
          .addComponent(btToComponents))
        .addGap(18, 18, 18)
        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
          .addComponent(jLabel3)
          .addComponent(edPacket, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
          .addComponent(jLabel7)
          .addComponent(edCanId, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
        .addContainerGap(81, Short.MAX_VALUE))
    );

  }// </editor-fold>//GEN-END:initComponents

  private void btToPacketActionPerformed(ActionEvent evt)//GEN-FIRST:event_btToPacketActionPerformed
  {//GEN-HEADEREND:event_btToPacketActionPerformed
    toPacket();
  }//GEN-LAST:event_btToPacketActionPerformed

  private void btToComponentsActionPerformed(ActionEvent evt)//GEN-FIRST:event_btToComponentsActionPerformed
  {//GEN-HEADEREND:event_btToComponentsActionPerformed
    toComponents();
  }//GEN-LAST:event_btToComponentsActionPerformed

  // Variables declaration - do not modify//GEN-BEGIN:variables
  private final JButton btToComponents = new JButton();
  private final JButton btToPacket = new JButton();
  private final JTextField edCanId = new JTextField();
  private final JTextField edCommand = new JTextField();
  private final JTextField edCommandGroup = new JTextField();
  private final JTextField edData = new JTextField();
  private final JTextField edPacket = new JTextField();
  private final JTextField edSender = new JTextField();
  private final JRadioButton rdAck = new JRadioButton();
  private final JRadioButton rdCommand = new JRadioButton();
  private final JRadioButton rdEvent = new JRadioButton();
  private final JRadioButton rdRequest = new JRadioButton();
  private final JSpinner spDLC = new JSpinner();
  // End of variables declaration//GEN-END:variables
}
