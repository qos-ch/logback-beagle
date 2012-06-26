/**
 * Logback-beagle: The logback Console Plugin for Eclipse 
 * Copyright (C) 2006-2012, QOS.ch. All rights reserved.
 *
 * This program and the accompanying materials are licensed under
 * either the terms of the Eclipse Public License v1.0 as published by
 * the Eclipse Foundation.
 */
package ch.qos.logback.beagle.util;

import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Table;

public class MetricsUtil {

  public static int computeCharWidth(Table table) {
    GC gc = new GC(table);
    FontMetrics fm = gc.getFontMetrics();
    return fm.getAverageCharWidth();
  }

  public static int computeCharHeight(Table table) {
    GC gc = new GC(table);
    FontMetrics fm = gc.getFontMetrics();
    return fm.getHeight();
  }
}
