/**
 * Logback-beagle: The logback Console Plugin for Eclipse 
 * Copyright (C) 2006-2012, QOS.ch. All rights reserved.
 *
 * This program and the accompanying materials are licensed under
 * either the terms of the Eclipse Public License v1.0 as published by
 * the Eclipse Foundation.
 */
package ch.qos.logback.beagle.view.listener;

import org.eclipse.nebula.widgets.grid.Grid;
import org.eclipse.nebula.widgets.grid.GridColumn;
import org.eclipse.nebula.widgets.grid.GridItem;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.widgets.ScrollBar;

/**
 * Reacts to changes in the window size of the grid it listens to. This grid is
 * assumed to have multiple columns. The reactions consists of updating the
 * width of this column which ultimately enables or disables a horizontal scroll
 * bar.
 * 
 * 
 * @author ceki
 * 
 */
public class TableControlListener implements ControlListener {

  final int charWidth;

  public TableControlListener(int charWidth) {
    this.charWidth = charWidth;
  }

  @Override
  public void controlMoved(ControlEvent e) {
    // we don't care about moving the grid
  }

  @Override
  public void controlResized(ControlEvent e) {
    Grid grid = (Grid) e.widget;
    adjustTableWidth(grid);
  }

  /**
   * Adjust the size of the grid assuming it contains only a single column.
   * 
   * @param grid
   */
  private void adjustTableWidth(Grid grid) {
    int lastColumnIndex = grid.getColumnCount() - 1;
    if (lastColumnIndex <= 0)
      return;

    int visibleTableWidth = computeVisibleTableWidth(grid);
    int totalWidthOfOtherColumns = computeWidthOfAllColumnExceptLast(grid);
    
    //int maxItemWidth = computeMaxItemWidth(grid);

    int targetWidth = Math.max(100, visibleTableWidth - totalWidthOfOtherColumns);
    GridColumn gridColumn = grid.getColumn(lastColumnIndex);
    gridColumn.setWidth(targetWidth);
  }

  private int computeWidthOfAllColumnExceptLast(Grid grid) {
    int total = 0;

    for (int i = 0; i < grid.getColumnCount() - 1; i++) {
      GridColumn gridColumn = grid.getColumn(i);
      total += gridColumn.getWidth();
    }

    return total;
  }

  private int computeMaxItemWidth(Grid grid) {
    int maxItemWidth = 0;
    for (GridItem ti : grid.getItems()) {
      int itemWidth = ti.getText().length() * charWidth;
      if (maxItemWidth < itemWidth) {
	maxItemWidth = itemWidth;
      }
    }
    return maxItemWidth;
  }

  private int computeVisibleTableWidth(Grid grid) {
    ScrollBar sb = grid.getVerticalBar();
    int targetWidth = grid.getSize().x;

    if (sb != null) {
      targetWidth -= sb.getSize().x;
    }
    return targetWidth;
  }

}
