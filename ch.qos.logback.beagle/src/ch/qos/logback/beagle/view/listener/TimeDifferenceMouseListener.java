/**
 * Logback-beagle: The logback Console Plugin for Eclipse 
 * Copyright (C) 2006-2012, QOS.ch. All rights reserved.
 *
 * This program and the accompanying materials are licensed under
 * either the terms of the Eclipse Public License v1.0 as published by
 * the Eclipse Foundation.
 */
package ch.qos.logback.beagle.view.listener;

import org.eclipse.nebula.widgets.grid.Grid;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;

import ch.qos.logback.beagle.Constants;
import ch.qos.logback.beagle.util.MouseEventUtil;
import ch.qos.logback.beagle.view.TableMediator;
import ch.qos.logback.beagle.visual.ITableItemStub;
import ch.qos.logback.beagle.visual.LoggingEventTIS;

public class TimeDifferenceMouseListener implements MouseMoveListener {

  TableMediator tableMediator;
  Grid grid;
  
  public TimeDifferenceMouseListener(TableMediator tableMediator) {
    this.tableMediator = tableMediator;
    this.grid = tableMediator.getGrid();
  }



  @Override
  public void mouseMove(MouseEvent e) {
    // it does not make sense to measure time differemce of the mouse botton
    // is held down
    if (MouseEventUtil.isButtonHeldDown(e)) {
      return;
    }

    // we can't measure the time difference for multiple selections
    int selectionCount = grid.getSelectionCount();
    if (selectionCount != 1) {
      return;
    }

    int otherIndex = MouseEventUtil.computeIndex(tableMediator.getGrid(), e);
    if (otherIndex == Constants.NA) {
      return;
    }
    int selectionIndex = grid.getSelectionIndex();
    updateTimeDifferenceLabel(selectionIndex, otherIndex);

  }

  private void updateTimeDifferenceLabel(int selectedIndex, int mouseOverIndex) {
    ITableItemStub selectedTIS = tableMediator.classicTISBuffer.get(selectedIndex);
    ITableItemStub mouseOverTIS = tableMediator.classicTISBuffer.get(mouseOverIndex);
    if (!(selectedTIS instanceof LoggingEventTIS)) {
      return;
    }
    if (!(mouseOverTIS instanceof LoggingEventTIS)) {
      return;
    }
    LoggingEventTIS selectedLETIS = (LoggingEventTIS) selectedTIS;
    LoggingEventTIS mouseOverLETIS = (LoggingEventTIS) mouseOverTIS;

    long diff = mouseOverLETIS.getILoggingEvent().getTimeStamp()
	- selectedLETIS.getILoggingEvent().getTimeStamp();

    tableMediator.setTimeDifferenceLabelText(diff + "  ms");
  }
}
