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

import at.or.reder.dcc.util.Utils;
import at.or.reder.zcan20.packet.Packet;
import at.or.reder.zcan20.packet.PacketAdapter;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;
import org.openide.util.NbBundle.Messages;

/**
 *
 * @author Wolfgang Reder
 */
@Messages({"PacketTableModel_col_TS=Zeit",
           "PacketTableModel_col_CanId=Can Id",
           "PacketTableModel_col_CommandGroup=Command Group",
           "PacketTableModel_col_Command=Command",
           "PacketTableModel_col_CommandMode=Mode",
           "PacketTableModel_col_Sender=Sender",
           "PacketTableModel_col_DLC=DLC",
           "PacketTableModel_col_Data=Data",
           "PacketTableModel_col_Adapter=Adapter",
           "# {0} - name",
           "# {1} - hexMagic",
           "PacketTableModel_fmt_CommandGroup={0} (0x{1})",
           "# {0} - hexMagic",
           "PacketTableModel_fmt_Command=0x{0}",
           "# {0} - name",
           "# {1} - binMagic",
           "PacketTableModel_fmt_CommandMode={0} (0b{1})"})
public final class PacketTableModel implements TableModel
{

  private final List<TSPacket> data = new ArrayList<>();
  private final Set<TableModelListener> listener = new CopyOnWriteArraySet<>();

  @Override
  public int getRowCount()
  {
    return data.size();
  }

  @Override
  public int getColumnCount()
  {
    return 9;
  }

  @Override
  public String getColumnName(int columnIndex)
  {
    switch (columnIndex) {
      case 0:
        return Bundle.PacketTableModel_col_TS();
      case 1:
        return Bundle.PacketTableModel_col_CanId();
      case 2:
        return Bundle.PacketTableModel_col_CommandGroup();
      case 3:
        return Bundle.PacketTableModel_col_Command();
      case 4:
        return Bundle.PacketTableModel_col_CommandMode();
      case 5:
        return Bundle.PacketTableModel_col_Sender();
      case 6:
        return Bundle.PacketTableModel_col_DLC();
      case 7:
        return Bundle.PacketTableModel_col_Data();
      case 8:
        return Bundle.PacketTableModel_col_Adapter();
      default:
        return "???";
    }
  }

  @Override
  public Class<?> getColumnClass(int columnIndex)
  {
    return String.class;
  }

  @Override
  public boolean isCellEditable(int rowIndex,
                                int columnIndex)
  {
    return false;
  }

  @Override
  public Object getValueAt(int rowIndex,
                           int columnIndex)
  {
    TSPacket packet = data.get(rowIndex);
    switch (columnIndex) {
      case 0:
        return packet.getLabelMap().computeIfAbsent(columnIndex,
                                                    (i) -> packet.getTimestamp().toString());
      case 1:
        return packet.getLabelMap().computeIfAbsent(columnIndex,
                                                    (i) -> Utils.appendHexString(packet.getCanId().intValue(),
                                                                                 new StringBuilder(),
                                                                                 4).toString());
      case 2:
        return packet.getLabelMap().computeIfAbsent(columnIndex,
                                                    (i) -> Bundle.PacketTableModel_fmt_CommandGroup(packet.getCommandGroup().
                                                            getName(),
                                                                                                    Integer.toHexString(packet.
                                                                                                            getCommandGroup().
                                                                                                            getMagic() & 0xff)));
      case 3:
        return packet.getLabelMap().computeIfAbsent(columnIndex,
                                                    (i) -> Bundle.PacketTableModel_fmt_Command(Integer.toHexString(packet.
                                                            getCommand())));
      case 4:
        return packet.getLabelMap().computeIfAbsent(columnIndex,
                                                    (i) -> Bundle.PacketTableModel_fmt_CommandMode(packet.getCommandMode().name(),
                                                                                                   Integer.toBinaryString(packet.
                                                                                                           getCommandMode().
                                                                                                           getMagic() & 0x3)));
      case 5:
        return packet.getLabelMap().computeIfAbsent(columnIndex,
                                                    (i) -> Utils.appendHexString(packet.getSenderNID() & 0xffff,
                                                                                 new StringBuilder(),
                                                                                 4).toString());
      case 6:
        return packet.getLabelMap().computeIfAbsent(columnIndex,
                                                    (i) -> Integer.toString(packet.getDLC()));
      case 7:
        return packet.getLabelMap().computeIfAbsent(columnIndex,
                                                    (i) -> Utils.byteBuffer2HexString(packet.getData(),
                                                                                      new StringBuilder(),
                                                                                      ' ').toString());
      case 8:
        return packet.getLabelMap().computeIfAbsent(columnIndex,
                                                    (i) -> {
                                                      PacketAdapter adapter = packet.getAdapter(PacketAdapter.class);
                                                      if (adapter != null) {
                                                        return adapter.toString();
                                                      } else {
                                                        return "";
                                                      }
                                                    });
      default:
        return null;
    }
  }

  @Override
  public void setValueAt(Object aValue,
                         int rowIndex,
                         int columnIndex
  )
  {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public void addTableModelListener(TableModelListener l)
  {
    if (l != null) {
      listener.add(l);
    }
  }

  @Override
  public void removeTableModelListener(TableModelListener l)
  {
    listener.remove(l);
  }

  public void appendPacket(LocalDateTime ts,
                           Packet packet)
  {
    if (packet != null && ts != null) {
      data.add(new TSPacket(packet,
                            ts));
      TableModelEvent evt = new TableModelEvent(this,
                                                data.size(),
                                                data.size(),
                                                TableModelEvent.ALL_COLUMNS,
                                                TableModelEvent.INSERT);
      for (TableModelListener l : listener) {
        l.tableChanged(evt);
      }
    }
  }

  public void clear()
  {
    data.clear();
    TableModelEvent evt = new TableModelEvent(this);
    for (TableModelListener l : listener) {
      l.tableChanged(evt);
    }
  }

}
