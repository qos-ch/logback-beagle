/**
 * Logback-beagle: The logback Console Plugin for Eclipse 
 * Copyright (C) 2006-2012, QOS.ch. All rights reserved.
 *
 * This program and the accompanying materials are licensed under
 * either the terms of the Eclipse Public License v1.0 as published by
 * the Eclipse Foundation.
 */
package ch.qos.logback.beagle.tree;

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.MenuItem;

public class TreeMenuSelectionListener implements SelectionListener {

  final LoggerTree loggerTree;

  TreeMenuSelectionListener(LoggerTree loggerTree) {
    this.loggerTree = loggerTree;
    
  }

  @Override
  public void widgetSelected(SelectionEvent e) {
    MenuItem menuItem = (MenuItem) e.widget;
    loggerTree.handleMenuItemSlection(menuItem);
  }

  @Override
  public void widgetDefaultSelected(SelectionEvent e) {
  }

}
