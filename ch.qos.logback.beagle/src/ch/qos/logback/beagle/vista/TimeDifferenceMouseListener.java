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
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.widgets.Label;

import ch.qos.logback.beagle.Constants;
import ch.qos.logback.beagle.util.MouseEventUtil;
import ch.qos.logback.beagle.visual.ClassicTISBuffer;
import ch.qos.logback.beagle.visual.ITableItemStub;
import ch.qos.logback.beagle.visual.LoggingEventTIS;

public class TimeDifferenceMouseListener implements MouseMoveListener {

  final Grid table;
  final ClassicTISBuffer visualElementBuffer;
  final Label diffCue;

  TimeDifferenceMouseListener(ClassicTISBuffer visualElementBuffer) {
    this.visualElementBuffer = visualElementBuffer;
    this.table = visualElementBuffer.grid;
    this.diffCue = visualElementBuffer.diffCue;
  }

  private void updateLabel(int selIndex, int otherIndex) {
    ITableItemStub selVE = visualElementBuffer.get(selIndex);
    ITableItemStub otherVE = visualElementBuffer.get(otherIndex);
    if (!(selVE instanceof LoggingEventTIS)) {
      return;
    }
    if (!(otherVE instanceof LoggingEventTIS)) {
      return;
    }
    LoggingEventTIS selLE = (LoggingEventTIS) selVE;
    LoggingEventTIS otherLE = (LoggingEventTIS) otherVE;

    long diff = otherLE.getILoggingEvent().getTimeStamp()
	- selLE.getILoggingEvent().getTimeStamp();

    diffCue.setText(diff + "  ms");
  }

  @Override
  public void mouseMove(MouseEvent e) {
    if (MouseEventUtil.isButtonHeldDown(e)) {
      return;
    }

    int selectionCount = table.getSelectionCount();
    if (selectionCount != 1) {
      return;
    }

    int otherIndex = MouseEventUtil.computeIndex(table, e);
    if (otherIndex == Constants.NA) {
      return;
    }
    int selectionIndex = table.getSelectionIndex();
    updateLabel(selectionIndex, otherIndex);

  }

}
