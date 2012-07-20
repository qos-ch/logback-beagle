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
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;

import ch.qos.logback.beagle.util.TableUtil;

/**
 * Reacts to changes in the window size of the grid it listens to. This grid is
 * assumed to have multiple columns. 
 * @see adjustWidthOfLastColumn
 *
 * @author ceki
 */
public class TableControlListener implements ControlListener {


  public TableControlListener() {
  }

  @Override
  public void controlMoved(ControlEvent e) {
    // we don't care about moving the grid
  }

  @Override
  public void controlResized(ControlEvent e) {
    Grid grid = (Grid) e.widget;
    TableUtil.adjustWidthOfLastColumn(grid);
  }

  /**
   * Adjust the size of the last column so that it occupies all remaining space.
   * 
   * @param grid
   */


//  private int computeMaxItemWidth(Grid grid) {
//    int maxItemWidth = 0;
//    for (GridItem ti : grid.getItems()) {
//      int itemWidth = ti.getText().length() * charWidth;
//      if (maxItemWidth < itemWidth) {
//	maxItemWidth = itemWidth;
//      }
//    }
//    return maxItemWidth;
//  }


}
