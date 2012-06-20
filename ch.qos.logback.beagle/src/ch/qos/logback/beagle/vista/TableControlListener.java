/**
 * Logback-beagle: The logback Console Plugin for Eclipse 
 * Copyright (C) 2006-2012, QOS.ch. All rights reserved.
 *
 * This program and the accompanying materials are licensed under
 * either the terms of the Eclipse Public License v1.0 as published by
 * the Eclipse Foundation.
 */
package ch.qos.logback.beagle.vista;

import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

public class TableControlListener implements ControlListener {

  int targetWidth = 0;
  int charWidth;
  
  TableControlListener(int charWidth) {
    this.charWidth = charWidth;
    System.out.println("charWidth="+charWidth);
  }

  @Override
  public void controlMoved(ControlEvent e) {

  }

  @Override
  public void controlResized(ControlEvent e) {
    Table table = (Table) e.widget;

    ScrollBar sb = table.getVerticalBar();
    int tableWidth = table.getSize().x;

    if (sb != null) {
      // System.out.println("x="+sb.getSize().x);
      tableWidth -= sb.getSize().x + 5;
    }

    targetWidth = tableWidth;
    for (TableItem ti : table.getItems()) {
      int itemWidth =  (3 + ti.getText().length()) * charWidth;
      if (targetWidth <itemWidth) {
        targetWidth = itemWidth;
      }
    }

    TableColumn tableCol = table.getColumn(0);

    tableCol.setWidth(targetWidth);

  }

}
