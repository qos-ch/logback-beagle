/**
 * Logback-beagle: The logback Console Plugin for Eclipse 
 * Copyright (C) 2006-2012, QOS.ch. All rights reserved.
 *
 * This program and the accompanying materials are licensed under
 * either the terms of the Eclipse Public License v1.0 as published by
 * the Eclipse Foundation.
 */
package ch.qos.logback.beagle.util;

import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.nebula.widgets.grid.Grid;
import org.eclipse.nebula.widgets.grid.GridColumn;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.swt.widgets.Table;

import ch.qos.logback.beagle.Activator;
import ch.qos.logback.beagle.Constants;

public class TableUtil {

  public static int getVisibleItemCount(Table table) {
    int start = table.getTopIndex();
    int itemCount = table.getItemCount();
    return Math.min(table.getBounds().height / table.getItemHeight() + 2,
	itemCount - start);
  }
  
  public static void saveColumnWidth(String columnName, int width) {
    IDialogSettings dialogSettings = Activator.INSTANCE.getDialogSettings();
    dialogSettings.put(Constants.COLUMN_SIZE_DIALOG_SETTINGS_PREFIX+columnName, width);
  }
  
  public static int adjustWidthOfLastColumn(Grid grid) {
    int lastColumnIndex = grid.getColumnCount() - 1;
    if (lastColumnIndex <= 0)
      return 0;

    //System.out.println("Adjusting width of last column");
    int visibleTableWidth = computeVisibleTableWidth(grid);
    int totalWidthOfOtherColumns = computeWidthOfAllColumnExceptLast(grid);
    
    int lastColumnAvailableWidth = visibleTableWidth - totalWidthOfOtherColumns - 4;
    
    // the target width should have a minimum. it should also be a few pixels smaller than
    // the available width so as to avoid a horizontal scrollbar.
    int lastColumnTargetWidth = Math.max(100, lastColumnAvailableWidth -4);
    GridColumn gridColumn = grid.getColumn(lastColumnIndex);
    gridColumn.setWidth(lastColumnTargetWidth);
    return lastColumnTargetWidth;
  }

  public static int computeVisibleTableWidth(Grid grid) {
    ScrollBar sb = grid.getVerticalBar();
    int targetWidth = grid.getSize().x;

    if (sb != null) {
      targetWidth -= sb.getSize().x;
    }
    return targetWidth;
  }
  
  public static int computeWidthOfAllColumnExceptLast(Grid grid) {
    int total = 0;

    for (int i = 0; i < grid.getColumnCount() - 1; i++) {
      GridColumn gridColumn = grid.getColumn(i);
      total += gridColumn.getWidth();
    }

    return total;
  }
}
