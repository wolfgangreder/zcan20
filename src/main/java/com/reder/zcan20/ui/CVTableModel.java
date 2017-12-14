/*
 * Copyright 2017 Wolfgang Reder.
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
package com.reder.zcan20.ui;

import com.reder.zcan20.CommandGroup;
import com.reder.zcan20.PacketListener;
import com.reder.zcan20.ZCAN;
import com.reder.zcan20.packet.CVInfoAdapter;
import com.reder.zcan20.packet.Packet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.swing.SwingUtilities;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;
import org.openide.util.NbBundle.Messages;

@Messages({"CVTableModel_COL_NUMBER=#CV",
           "CVTableModel_COL_VALUE=Wert",
           "CVTableModel_COL_DESCRIPTION=Beschreibung"})
public final class CVTableModel implements TableModel
{

  public static final int COL_NUMBER = 0;
  public static final int COL_VALUE = 1;
  public static final int COL_DESCRIPTION = 2;

  private static final class Record implements CVInfoAdapter, Cloneable
  {

    private final int number;
    private final Short valueOnDevice;
    private final short decoderAddress;
    private final short systemID;
    private short value;

    private Record(CVInfoAdapter info)
    {
      this(info.getSystemID(),
           info.getDecoderID(),
           info.getNumber(),
           info.getValue());
    }

    private Record(short systemID,
                   short decoderAddress,
                   int number,
                   Short valueOnDevice)
    {
      this.systemID = systemID;
      this.number = number;
      this.valueOnDevice = valueOnDevice;
      if (valueOnDevice != null) {
        value = valueOnDevice;
      }
      this.decoderAddress = decoderAddress;
    }

    @Override
    public short getSystemID()
    {
      return systemID;
    }

    @Override
    public short getDecoderID()
    {
      return decoderAddress;
    }

    @Override
    public Packet getPacket()
    {
      return null;
    }

    @Override
    public int getNumber()
    {
      return number;
    }

    public Short getValueOnDevice()
    {
      return valueOnDevice;
    }

    @Override
    public short getValue()
    {
      return value;
    }

    public void setValue(short value)
    {
      this.value = value;
    }

    public boolean isChanged()
    {
      return valueOnDevice == null || !valueOnDevice.equals(value);
    }

    @Override
    public Record clone()
    {
      try {
        return (Record) super.clone();
      } catch (CloneNotSupportedException ex) {
      }
      return null;
    }

  }
  private ZCAN device;
  private short locoAddress;
  private Function<Integer, String> descriptorFunction;
  private final PacketListener pl = this::onPacket;
  private final List<Record> data = new ArrayList<>();
  private final Comparator<CVInfoAdapter> infoComparator = Comparator.comparing(CVInfoAdapter::getNumber);
  private final Set<TableModelListener> listener = new CopyOnWriteArraySet<>();

  private void onPacket(ZCAN device,
                        Packet packet)
  {
    CVInfoAdapter info = packet.getAdapter(CVInfoAdapter.class);
    if (info != null && info.getDecoderID() == locoAddress) {
      SwingUtilities.invokeLater(() -> updateInfo(info));
    }
  }

  private void updateInfo(CVInfoAdapter info)
  {
    assert SwingUtilities.isEventDispatchThread();
    if (info == null) {
      return;
    }
    int index = Collections.binarySearch(data,
                                         info,
                                         infoComparator);
    if (index >= 0) {
      CVInfoAdapter old = data.set(index,
                                   new Record(info));
      if (old.getValue() != info.getValue()) {
        fireRowUpdated(index);
      }
    } else {
      index = 1 - index;
      data.add(index,
               new Record(info));
      fireRowInserted(index);
    }
  }

  public ZCAN getDevice()
  {
    return device;
  }

  public void setDevice(ZCAN device)
  {
    if (this.device != device) {
      if (this.device != null) {
        this.device.removePacketListener(CommandGroup.TRACK_CONFIG_PUBLIC,
                                         pl);
      }
      data.clear();
      this.device = device;
      if (this.device != null) {
        this.device.addPacketListener(CommandGroup.TRACK_CONFIG_PUBLIC,
                                      pl);
      }
      fireContentsChanged();
    }
  }

  public short getLocoAddress()
  {
    return locoAddress;
  }

  public void setLocoAddress(short a)
  {
    if (locoAddress != a) {
      locoAddress = a;
      data.clear();
      fireContentsChanged();
    }
  }

  public List<CVInfoAdapter> getAllRecords()
  {
    return new ArrayList<>(data);
  }

  public List<CVInfoAdapter> getModifiedRecords()
  {
    return data.
            stream().
            filter(Record::isChanged).
            collect(Collectors.toList());
  }

  public CVInfoAdapter getRecord(int number)
  {
    Record result = new Record(device.getNID(),
                               locoAddress,
                               number,
                               null);
    int index = Collections.binarySearch(data,
                                         result,
                                         infoComparator);
    if (index >= 0) {
      return data.get(index).clone();
    } else {
      index = 1 - index;
      data.add(index,
               result);
      fireRowInserted(index);
      return result;
    }
  }

  @Override
  public int getRowCount()
  {
    return data.size();
  }

  @Override
  public int getColumnCount()
  {
    return 3;
  }

  @Override
  public String getColumnName(int columnIndex)
  {
    switch (columnIndex) {
      case COL_NUMBER:
        return Bundle.CVTableModel_COL_NUMBER();
      case COL_VALUE:
        return Bundle.CVTableModel_COL_VALUE();
      case COL_DESCRIPTION:
        return Bundle.CVTableModel_COL_DESCRIPTION();
      default:
        return Integer.toString(columnIndex);
    }
  }

  @Override
  public Class<?> getColumnClass(int columnIndex)
  {
    switch (columnIndex) {
      case COL_NUMBER:
        return Integer.class;
      case COL_VALUE:
        return Integer.class;
      case COL_DESCRIPTION:
        return String.class;
      default:
        return null;
    }
  }

  @Override
  public boolean isCellEditable(int rowIndex,
                                int columnIndex)
  {
    return columnIndex == COL_VALUE;
  }

  @Override
  public Object getValueAt(int rowIndex,
                           int columnIndex)
  {
    CVInfoAdapter rec = data.get(rowIndex);
    switch (columnIndex) {
      case COL_NUMBER:
        return rec.getNumber();
      case COL_VALUE:
        return rec.getValue();
      case COL_DESCRIPTION:
        if (descriptorFunction != null) {
          return descriptorFunction.apply(rec.getNumber());
        } else {
          return null;
        }
      default:
        return null;
    }
  }

  @Override
  public void setValueAt(Object aValue,
                         int rowIndex,
                         int columnIndex)
  {
    if (isCellEditable(rowIndex,
                       columnIndex)) {
      if (columnIndex == COL_VALUE && aValue instanceof Number) {
        Record rec = data.get(rowIndex);
        short newValue = ((Number) aValue).shortValue();
        if (rec.getValue() != newValue) {
          rec.setValue(newValue);
          fireCellChanged(rowIndex,
                          columnIndex);
        }
      }
    }
  }

  private void fireCellChanged(int rowIndex,
                               int columnIndex)
  {
    if (listener.isEmpty()) {
      return;
    }
    TableModelEvent event = new TableModelEvent(this,
                                                rowIndex,
                                                rowIndex,
                                                columnIndex,
                                                TableModelEvent.UPDATE);
    for (TableModelListener l : listener) {
      l.tableChanged(event);
    }
  }

  private void fireContentsChanged()
  {
    if (listener.isEmpty()) {
      return;
    }
    TableModelEvent event = new TableModelEvent(this);
    for (TableModelListener l : listener) {
      l.tableChanged(event);
    }
  }

  private void fireRowInserted(int row)
  {
    if (listener.isEmpty()) {
      return;
    }
    TableModelEvent event = new TableModelEvent(this,
                                                row,
                                                row,
                                                TableModelEvent.ALL_COLUMNS,
                                                TableModelEvent.INSERT);
    for (TableModelListener l : listener) {
      l.tableChanged(event);
    }
  }

  private void fireRowUpdated(int row)
  {
    if (listener.isEmpty()) {
      return;
    }
    TableModelEvent event = new TableModelEvent(this,
                                                row,
                                                row,
                                                TableModelEvent.ALL_COLUMNS,
                                                TableModelEvent.UPDATE);
    for (TableModelListener l : listener) {
      l.tableChanged(event);
    }
  }

  @Override
  public void addTableModelListener(TableModelListener l
  )
  {
    if (l != null) {
      listener.add(l);
    }
  }

  @Override
  public void removeTableModelListener(TableModelListener l
  )
  {
    listener.remove(l);
  }

}
