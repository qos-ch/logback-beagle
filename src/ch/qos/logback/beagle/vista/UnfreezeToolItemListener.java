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

public class UnfreezeToolItemListener implements SelectionListener {

  final VisualElementBuffer visualElementBuffer;

  UnfreezeToolItemListener(VisualElementBuffer visualElementBuffer) {
    this.visualElementBuffer = visualElementBuffer;
  }

  @Override
  public void widgetSelected(SelectionEvent event) {
    ToolItem toolItem = (ToolItem) event.widget;
    toolItem.setEnabled(false);
    visualElementBuffer.table.deselectAll();
    visualElementBuffer.setActive(true);
    visualElementBuffer.clearCues();
  }

  @Override
  public void widgetDefaultSelected(SelectionEvent e) {
    throw new UnsupportedOperationException();
  }

}
