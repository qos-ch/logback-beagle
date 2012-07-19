/**
 * Logback-beagle: The logback Console Plugin for Eclipse 
 * Copyright (C) 2006-2012, QOS.ch. All rights reserved.
 *
 * This program and the accompanying materials are licensed under
 * either the terms of the Eclipse Public License v1.0 as published by
 * the Eclipse Foundation.
 */
package ch.qos.logback.beagle.vista;

import org.eclipse.nebula.widgets.grid.Grid;
import org.eclipse.nebula.widgets.grid.GridItem;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.ToolItem;

import ch.qos.logback.beagle.visual.ClassicTISBuffer;

public class TableItemSelectionListener implements SelectionListener {

  Grid grid;
  ClassicTISBuffer classicTISBuffer;
  GridItem lastSelection;
  ToolItem unfreezeButton;

  
  TableItemSelectionListener(Grid table,
      ClassicTISBuffer visualElementBuffer, ToolItem unfreezeButton,
      UnfreezeToolItemListener unfreezeButtonListener) {
    this.grid = table;
    this.classicTISBuffer = visualElementBuffer;
    this.unfreezeButton = unfreezeButton;
    
  }

  @Override
  public void widgetDefaultSelected(SelectionEvent e) {
  }

  @Override
  public void widgetSelected(SelectionEvent e) {
    GridItem currentlySelectedTI = (GridItem) e.item;
    if(currentlySelectedTI == null) 
      return;
    
    classicTISBuffer.setSCrollingEnabled(false);
    unfreezeButton.setEnabled(true);
    lastSelection = currentlySelectedTI;
    classicTISBuffer.clearCues();
    
  }
}
