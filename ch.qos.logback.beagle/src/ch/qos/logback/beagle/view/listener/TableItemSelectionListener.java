/**
 * Logback-beagle: The logback Console Plugin for Eclipse 
 * Copyright (C) 2006-2012, QOS.ch. All rights reserved.
 *
 * This program and the accompanying materials are licensed under
 * either the terms of the Eclipse Public License v1.0 as published by
 * the Eclipse Foundation.
 */
package ch.qos.logback.beagle.view.listener;

import org.eclipse.nebula.widgets.grid.GridItem;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.ToolItem;

import ch.qos.logback.beagle.view.TableMediator;
import ch.qos.logback.beagle.visual.ClassicTISBuffer;

public class TableItemSelectionListener implements SelectionListener {

  ClassicTISBuffer classicTISBuffer;
  GridItem lastSelection;
  ToolItem unfreezeButton;
  final TableMediator tableMediator;
  
  public TableItemSelectionListener(TableMediator tableMediator,
      ToolItem unfreezeButton) {
    this.tableMediator = tableMediator;
    this.unfreezeButton = unfreezeButton;
    
  }

  @Override
  public void widgetDefaultSelected(SelectionEvent e) {
  }

  @Override
  public void widgetSelected(SelectionEvent e) {
    GridItem currentlySelectedGridItem = (GridItem) e.item;
    if(currentlySelectedGridItem == null) 
      return;
    
    tableMediator.classicTISBuffer.setScrollingEnabled(false);
    unfreezeButton.setEnabled(true);
    lastSelection = currentlySelectedGridItem;
    tableMediator.setTimeDifferenceLabelText("");
    
  }
}
