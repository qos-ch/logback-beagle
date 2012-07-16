/**
 * Logback-beagle: The logback Console Plugin for Eclipse 
 * Copyright (C) 2006-2012, QOS.ch. All rights reserved.
 *
 * This program and the accompanying materials are licensed under
 * either the terms of the Eclipse Public License v1.0 as published by
 * the Eclipse Foundation.
 */
package ch.qos.logback.beagle.util;

import org.eclipse.nebula.widgets.grid.Grid;
import org.eclipse.nebula.widgets.grid.GridItem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Point;

final public class MouseEventUtil {

  public static void dump(String prefix, MouseEvent e) {
    System.out.println(prefix + " e.x=" + e.x + ", e.y=" + e.y);
  }

  public static boolean isButtonHeldDown(MouseEvent e) {
    return ((e.stateMask & SWT.BUTTON1) != 0);
  }

  public static int computeIndex(Grid table, MouseEvent e) {
    GridItem ti = MouseEventUtil.getTableItemForEvent_XFree(table, e);
    if (ti == null) {
      return -1;
    }
    return table.indexOf(ti);
  }

  private static GridItem getTableItemForEvent_XFree(Grid table, MouseEvent e) {
    Point point = new Point(10, e.y);
    GridItem ti = table.getItem(point);
    return ti;
  }
}
