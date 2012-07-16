/**
 * Logback-beagle: The logback Console Plugin for Eclipse 
 * Copyright (C) 2006-2012, QOS.ch. All rights reserved.
 *
 * This program and the accompanying materials are licensed under
 * either the terms of the Eclipse Public License v1.0 as published by
 * the Eclipse Foundation.
 */
package ch.qos.logback.beagle.vista;

import org.eclipse.nebula.widgets.grid.Grid;
import org.eclipse.nebula.widgets.grid.GridColumn;
import org.eclipse.nebula.widgets.grid.GridItem;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.widgets.ScrollBar;

/**
 * Reacts to changes in the window size of the table it listens to. This table
 * is assumed to have a single column. The reactions consists of updating the
 * width of this column which ultimately enables or disables a horizontal scroll
 * bar.
 * 
 * 
 * @author ceki
 * 
 */
public class TableControlListener implements ControlListener {

  final int charWidth;

  TableControlListener(int charWidth) {
    this.charWidth = charWidth;
  }

  @Override
  public void controlMoved(ControlEvent e) {
    // we don't care about moving the table
  }

  @Override
  public void controlResized(ControlEvent e) {
    Grid table = (Grid) e.widget;
    adjustTableWidth(table);
  }

  /**
   * Adjust the size of the table assuming it contains only a single column.
   * 
   * @param table
   */
  private void adjustTableWidth(Grid table) {
    int visibleTableWidth = computeVisibleTableWidth(table);
    int maxItemWidth = computeMaxItemWidth(table);

    int targetWidth = Math.max(visibleTableWidth, maxItemWidth);
    GridColumn tableCol = table.getColumn(0);
    tableCol.setWidth(targetWidth);
  }

  private int computeMaxItemWidth(Grid table) {
    int maxItemWidth = 0;
    for (GridItem ti : table.getItems()) {
      int itemWidth = ti.getText().length() * charWidth;
      if (maxItemWidth < itemWidth) {
	maxItemWidth = itemWidth;
      }
    }
    return maxItemWidth;
  }

  private int computeVisibleTableWidth(Grid table) {
    ScrollBar sb = table.getVerticalBar();
    int targetWidth = table.getSize().x;

    if (sb != null) {
      targetWidth -= sb.getSize().x;
    }
    return targetWidth;
  }

}
