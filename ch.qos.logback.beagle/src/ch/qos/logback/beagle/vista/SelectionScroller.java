/**
 * Logback-beagle: The logback Console Plugin for Eclipse 
 * Copyright (C) 2006-2012, QOS.ch. All rights reserved.
 *
 * This program and the accompanying materials are licensed under
 * either the terms of the Eclipse Public License v1.0 as published by
 * the Eclipse Foundation.
 */
package ch.qos.logback.beagle.vista;

import java.util.Arrays;

import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

import ch.qos.logback.beagle.util.SelectionUtil;

/**
 * SelectionScroller allows the selection of rows when the mouse the beagle
 * windows with the user holding down the mouse. It mimics the way the regular
 * console reacts to the mouse exiting its window with the button held.
 * 
 * @author ceki
 * 
 */
public class SelectionScroller implements Runnable {

  static int DELAY = 100;
  static int STEP_SIZE = 10;

  enum Direction {
    DOWN, UP;
  }

  final Table table;
  int start;
  int end;
  final Direction direction;
  int anchorIndex;

  public SelectionScroller(Table table, int anchorIndex, Direction direction) {

    this.anchorIndex = anchorIndex;
    this.table = table;
    int[] selectionIndices = table.getSelectionIndices();
    Arrays.sort(selectionIndices);
    start = selectionIndices[0];
    end = selectionIndices[selectionIndices.length - 1];
    this.direction = direction;
    System.out.println("Scroller start=" + start + ", end=" + end
	+ ", direction " + direction);
  }

  void incrementEnd() {
    end += STEP_SIZE;
    final int max = table.getItemCount() - 1;
    if (end > max) {
      end = max;
    }
  }

  void decrementEnd() {
    end -= STEP_SIZE;
    if (end < 0) {
      end = 0;
    }
  }

  void decrementStart() {
    start -= STEP_SIZE;
    if (start < 0) {
      start = 0;
      ;
    }
  }

  void incrementStart() {
    start += STEP_SIZE;
    final int max = table.getItemCount() - 1;
    if (start > max) {
      start = max;
    }
  }

  @Override
  public void run() {
    if (start <= 0 || end >= table.getItemCount() - 1) {
      return;
    }

    int targetItemIndex;

    if (direction == Direction.UP) {
      if (anchorIndex >= end) { // straight up
	decrementStart();
	SelectionUtil.selectRange(table, start, anchorIndex);
	targetItemIndex = start;
      } else { // first down than up
	decrementEnd();
	SelectionUtil.selectRange(table, anchorIndex, end);
	targetItemIndex = end;
      }
    } else {
      if (anchorIndex <= start) { // straight down
	incrementEnd();
	SelectionUtil.selectRange(table, anchorIndex, end);
	targetItemIndex = end;
      } else { // first up than down
	incrementStart();
	SelectionUtil.selectRange(table, start, anchorIndex);
	targetItemIndex = start;
      }
    }
    TableItem ti = table.getItem(targetItemIndex);
    table.showItem(ti);
    table.getDisplay().timerExec(DELAY, this);
  }

}
