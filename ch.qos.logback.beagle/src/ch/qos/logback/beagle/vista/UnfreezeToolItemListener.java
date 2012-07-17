/**
 * Logback-beagle: The logback Console Plugin for Eclipse 
 * Copyright (C) 2006-2012, QOS.ch. All rights reserved.
 *
 * This program and the accompanying materials are licensed under
 * either the terms of the Eclipse Public License v1.0 as published by
 * the Eclipse Foundation.
 */
package ch.qos.logback.beagle.vista;

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.ToolItem;

import ch.qos.logback.beagle.visual.ClassicTISBuffer;

public class UnfreezeToolItemListener implements SelectionListener {

  final ClassicTISBuffer classicTISBuffer;

  UnfreezeToolItemListener(ClassicTISBuffer classicTISBuffer) {
    this.classicTISBuffer = classicTISBuffer;
  }

  @Override
  public void widgetSelected(SelectionEvent event) {
    ToolItem toolItem = (ToolItem) event.widget;
    toolItem.setEnabled(false);
    classicTISBuffer.grid.deselectAll();
    classicTISBuffer.setActive(true);
    classicTISBuffer.clearCues();
  }

  @Override
  public void widgetDefaultSelected(SelectionEvent e) {
    throw new UnsupportedOperationException();
  }

}
