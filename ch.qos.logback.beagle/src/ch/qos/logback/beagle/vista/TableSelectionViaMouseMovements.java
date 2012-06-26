/**
 * Logback-beagle: The logback Console Plugin for Eclipse 
 * Copyright (C) 2006-2012, QOS.ch. All rights reserved.
 *
 * This program and the accompanying materials are licensed under
 * either the terms of the Eclipse Public License v1.0 as published by
 * the Eclipse Foundation.
 */
package ch.qos.logback.beagle.vista;

import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.widgets.Table;

import ch.qos.logback.beagle.Constants;
import ch.qos.logback.beagle.util.MouseEventUtil;
import ch.qos.logback.beagle.util.SelectionUtil;

public class TableSelectionViaMouseMovements implements MouseListener,
    MouseMoveListener, MouseTrackListener {

  final Table table;
  final ClassicTISBuffer visualElementBuffer;
  int anchorIndex = Constants.NA;
  int lastIndex = Constants.NA;
  Scroller scroller;

  TableSelectionViaMouseMovements(ClassicTISBuffer visualElementBuffer) {
    this.visualElementBuffer = visualElementBuffer;
    this.table = visualElementBuffer.table;
  }

  @Override
  public void mouseDoubleClick(MouseEvent e) {
  }

  @Override
  public void mouseDown(MouseEvent e) {
    int result = MouseEventUtil.computeIndex(table, e);
    if (result == Constants.NA) {
      return;
    }
    anchorIndex = result;
    System.out.println("mouseDown anchorIndex " + anchorIndex);
  }

  void disableScroller() {
    System.out.println("disabling scroller");
    table.setCapture(false);
    table.getDisplay().timerExec(-1, scroller);
    scroller = null;
  }

  @Override
  public void mouseUp(MouseEvent e) {
    // MouseEventUtil.dump("************** mouseUp ", e);
    if (scroller != null) {
      disableScroller();
    }
  }

  @Override
  public void mouseEnter(MouseEvent e) {
  }

  @Override
  public void mouseMove(MouseEvent e) {
    if (!MouseEventUtil.isButtonHeldDown(e)) {
      return;
    }
    int currentIndex = MouseEventUtil.computeIndex(table, e);
    if (currentIndex == Constants.NA) {
      // certain time we are unable to compute the tableitem
      // index corresponding to a mouse event, typically when the
      // mouse goes beyond the text range of the item
      // we ignore such misfires
      return;
    }
    if (scroller != null) {
      disableScroller();
    }
    if (currentIndex == lastIndex) {
      return;
    }

    if (table.getSelectionCount() > 1) {
      visualElementBuffer.clearCues();
    }

    lastIndex = currentIndex;
    doSelect(anchorIndex, currentIndex);
  }

  void doSelect(int x, int y) {
    int rangeStart = Math.min(x, y);
    int rangeEnd = Math.max(x, y);
    SelectionUtil.selectRange(table, rangeStart, rangeEnd);
  }

  @Override
  public void mouseExit(MouseEvent e) {
    MouseEventUtil.dump("************** mouseExit ", e);

    if (MouseEventUtil.isButtonHeldDown(e)) {
      if (scroller != null) {
	System.out.println("already in capture mode");
	return;
      }
      System.out.println("entering capture mode");
      Scroller.Direction direction = e.y < 0 ? Scroller.Direction.UP
	  : Scroller.Direction.DOWN;
      table.setCapture(true);
      scroller = new Scroller(table, anchorIndex, direction);
      table.getDisplay().timerExec(Scroller.DELAY, scroller);
    }
  }

  @Override
  public void mouseHover(MouseEvent e) {
  }
}
