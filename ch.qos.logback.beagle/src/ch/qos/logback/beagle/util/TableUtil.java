/**
 * Logback-beagle: The logback Console Plugin for Eclipse 
 * Copyright (C) 2006-2012, QOS.ch. All rights reserved.
 *
 * This program and the accompanying materials are licensed under
 * either the terms of the Eclipse Public License v1.0 as published by
 * the Eclipse Foundation.
 */
package ch.qos.logback.beagle.util;

import org.eclipse.swt.widgets.Table;

public class TableUtil {

  public static int getVisibleItemCount(Table table) {
    int start = table.getTopIndex();
    int itemCount = table.getItemCount();
    return Math.min(table.getBounds().height / table.getItemHeight() + 2,
        itemCount - start);
  }
}
