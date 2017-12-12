/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.reder.zcan20.ui;

import java.util.Locale;
import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertFalse;
import static org.testng.AssertJUnit.assertNotNull;
import org.testng.annotations.Test;

/**
 *
 * @author Wolfgang Reder
 */
public class CVTableModelNGTest
{

  public CVTableModelNGTest()
  {
  }

  @Test
  public void testGetRowCount()
  {
  }

  @Test
  public void testGetColumnCount()
  {
    CVTableModel model = new CVTableModel();
    assertEquals(3,
                 model.getColumnCount());
  }

  @Test
  public void testGetColumnName_de()
  {
    Locale old = Locale.getDefault();
    try {
      Locale loc = Locale.GERMAN;
      Locale.setDefault(loc);
      CVTableModel model = new CVTableModel();
      for (int i = 0; i < model.getColumnCount(); ++i) {
        String s = model.getColumnName(i);
        assertNotNull(s);
        assertFalse(s.trim().isEmpty());
      }
    } finally {
      Locale.setDefault(old);
    }
  }

  @Test
  public void testGetColumnName_en()
  {
    Locale old = Locale.getDefault();
    try {
      Locale loc = Locale.ENGLISH;
      Locale.setDefault(loc);
      CVTableModel model = new CVTableModel();
      for (int i = 0; i < model.getColumnCount(); ++i) {
        String s = model.getColumnName(i);
        assertNotNull(s);
        assertFalse(s.trim().isEmpty());
      }
    } finally {
      Locale.setDefault(old);
    }
  }

  @Test
  public void testGetColumnName()
  {
    CVTableModel model = new CVTableModel();
    for (int i = 0; i < model.getColumnCount(); ++i) {
      String s = model.getColumnName(i);
      assertNotNull(s);
      assertFalse(s.trim().isEmpty());
    }
  }

  @Test
  public void testGetColumnClass()
  {
  }

  @Test
  public void testIsCellEditable()
  {
  }

  @Test
  public void testGetValueAt()
  {
  }

  @Test
  public void testSetValueAt()
  {
  }

  @Test
  public void testAddTableModelListener()
  {
  }

  @Test
  public void testRemoveTableModelListener()
  {
  }

}
