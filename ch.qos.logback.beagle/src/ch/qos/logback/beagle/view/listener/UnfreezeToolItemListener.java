/**
 * Logback-beagle: The logback Console Plugin for Eclipse 
 * Copyright (C) 2006-2012, QOS.ch. All rights reserved.
 *
 * This program and the accompanying materials are licensed under
 * either the terms of the Eclipse Public License v1.0 as published by
 * the Eclipse Foundation.
 */
package ch.qos.logback.beagle.view.listener;

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.ToolItem;

import ch.qos.logback.beagle.view.TableMediator;

public class UnfreezeToolItemListener implements SelectionListener {

  final TableMediator tableMediator;
  
  public UnfreezeToolItemListener(TableMediator tableMediator) {
    this.tableMediator = tableMediator;
  }

  @Override
  public void widgetSelected(SelectionEvent event) {
    ToolItem toolItem = (ToolItem) event.widget;
    toolItem.setEnabled(false);
    tableMediator.getGrid().deselectAll();
    tableMediator.classicTISBuffer.setScrollingEnabled(true);
    tableMediator.setTimeDifferenceLabelText("");
  }

  @Override
  public void widgetDefaultSelected(SelectionEvent e) {
    throw new UnsupportedOperationException();
  }

}
