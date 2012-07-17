/**
 * Logback-beagle: The logback Console Plugin for Eclipse 
 * Copyright (C) 2006-2012, QOS.ch. All rights reserved.
 *
 * This program and the accompanying materials are licensed under
 * either the terms of the Eclipse Public License v1.0 as published by
 * the Eclipse Foundation.
 */
package ch.qos.logback.beagle.vista;

import static ch.qos.logback.beagle.util.ResourceUtil.JUMP_IMG_KEY;

import org.eclipse.nebula.widgets.grid.Grid;
import org.eclipse.nebula.widgets.grid.GridItem;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.ToolItem;

import ch.qos.logback.beagle.util.ResourceUtil;
import ch.qos.logback.beagle.visual.ClassicTISBuffer;
import ch.qos.logback.beagle.visual.ITableItemStub;

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
    //System.out.println("MySelectionListener.widgetDefaultSelected called with "
	//+ e);
  }

  @Override
  public void widgetSelected(SelectionEvent e) {
    GridItem currentlySelectedTI = (GridItem) e.item;
    if(currentlySelectedTI == null) 
      return;
    final int indexOfCurrentSel = grid.indexOf(currentlySelectedTI);
    
    classicTISBuffer.setActive(false);
    unfreezeButton.setEnabled(true);
    lastSelection = currentlySelectedTI;

    classicTISBuffer.clearCues();
    ITableItemStub iTableItemStub = classicTISBuffer.get(indexOfCurrentSel);
    if(iTableItemStub == null)
      return;
    System.out.println("iTableItemStub:"+iTableItemStub.getText());
    
    if (iTableItemStub.supportsJump() && grid.getSelectionCount() == 1) {
      classicTISBuffer.jumpCue.setImage(ResourceUtil.getImage(JUMP_IMG_KEY));
    }
  }
}
