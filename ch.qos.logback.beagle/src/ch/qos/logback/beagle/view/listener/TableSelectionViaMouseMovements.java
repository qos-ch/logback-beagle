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
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.MouseTrackListener;

import ch.qos.logback.beagle.Constants;
import ch.qos.logback.beagle.util.MouseEventUtil;
import ch.qos.logback.beagle.util.SelectionUtil;
import ch.qos.logback.beagle.view.SelectionScroller;
import ch.qos.logback.beagle.visual.ClassicTISBuffer;

public class TableSelectionViaMouseMovements implements MouseListener,
    MouseMoveListener, MouseTrackListener {

  final Grid table;
  final ClassicTISBuffer classicTISBuffer;
  int anchorIndex = Constants.NA;
  int lastIndex = Constants.NA;
  SelectionScroller scroller;

  public TableSelectionViaMouseMovements(ClassicTISBuffer classicTISBuffer) {
    this.classicTISBuffer = classicTISBuffer;
    this.table = classicTISBuffer.grid;
  }

  @Override
  public void mouseDoubleClick(MouseEvent e) {
    
    int clickedIndex = MouseEventUtil.computeIndex(table, e);
    
    int[] currentSelectionIndices = table.getSelectionIndices();
    if(currentSelectionIndices.length == 1) {
      if(currentSelectionIndices[0] == clickedIndex) {
	  System.out.println("********* unlocked scroll");
	 classicTISBuffer.setScrollingEnabled(true);
	 // TODO disable the scroll toolitem
      }
    }
  }

  @Override
  public void mouseDown(MouseEvent e) {
    int result = MouseEventUtil.computeIndex(table, e);
    if (result == Constants.NA) {
      return;
    }
    anchorIndex = result;
    //System.out.println("mouseDown anchorIndex " + anchorIndex);
  }

  void disableScroller() {
    //System.out.println("disabling scroller");
    table.setCapture(false);
    table.getDisplay().timerExec(-1, scroller);
    scroller = null;
  }

  @Override
  public void mouseUp(MouseEvent e) {
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
      classicTISBuffer.clearCues();
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
     //MouseEventUtil.dump("************** mouseExit event ***********", e);

    if (MouseEventUtil.isButtonHeldDown(e)) {
      if (scroller != null) {
	//System.out.println("xxxxxxxxxxx already in capture mode");
	return;
      }
      //System.out.println("********* entering capture mode");
      SelectionScroller.Direction direction = e.y < 0 ? SelectionScroller.Direction.UP
	  : SelectionScroller.Direction.DOWN;
      table.setCapture(true);
      scroller = new SelectionScroller(table, anchorIndex, direction);
      table.getDisplay().timerExec(SelectionScroller.DELAY, scroller);
    }
  }

  @Override
  public void mouseHover(MouseEvent e) {
  }
}
