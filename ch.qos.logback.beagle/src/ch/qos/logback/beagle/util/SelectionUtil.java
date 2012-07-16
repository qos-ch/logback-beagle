/**
 * Logback-beagle: The logback Console Plugin for Eclipse 
 * Copyright (C) 2006-2012, QOS.ch. All rights reserved.
 *
 * This program and the accompanying materials are licensed under
 * either the terms of the Eclipse Public License v1.0 as published by
 * the Eclipse Foundation.
 */
package ch.qos.logback.beagle.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.nebula.widgets.grid.Grid;
import org.eclipse.swt.widgets.Table;

import ch.qos.logback.beagle.Constants;

public class SelectionUtil {

  final static int[] EMTPY_INT_ARRAY = new int[0];

  public static int getUniqueSelection(Grid table) {
    if (table.getSelectionCount() != 1) {
      return Constants.NA;
    } else {
      return table.getSelectionIndex();
    }
  }

  private static boolean isOutsideRange(int rangeStart, int rangeEnd, int i) {
    if (i < rangeStart)
      return true;
    if (i > rangeEnd)
      return true;
    return false;
  }

  public static boolean inArray(int[] iArray, int x) {
    for (int i : iArray) {
      if (i == x) {
	return true;
      }
    }
    return false;
  }

  static int[] toIntArray(List<Integer> list) {
    int[] result = new int[list.size()];
    for (int i = 0; i < list.size(); i++) {
      result[i] = list.get(i);
    }
    return result;
  }

  public static void selectRange(Grid table, int rangeStart, int rangeEnd) {
    int[] currentSelectionIndices = table.getSelectionIndices();
    Arrays.sort(currentSelectionIndices);

    List<Integer> toDeselectList = new ArrayList<Integer>();
    for (int currentSel : currentSelectionIndices) {
      if (isOutsideRange(rangeStart, rangeEnd, currentSel)) {
	toDeselectList.add(currentSel);
      }
    }
    List<Integer> toSelectList = new ArrayList<Integer>();
    for (int i = rangeStart; i <= rangeEnd; i++) {
      if (!inArray(currentSelectionIndices, i)) {
	toSelectList.add(i);
      }
    }

    table.select(toIntArray(toSelectList));
    table.deselect(toIntArray(toDeselectList));

  }
}
