/**
 * Logback-beagle: The logback Console Plugin for Eclipse 
 * Copyright (C) 2006-2012, QOS.ch. All rights reserved.
 *
 * This program and the accompanying materials are licensed under
 * either the terms of the Eclipse Public License v1.0 as published by
 * the Eclipse Foundation.
 */
package ch.qos.logback.beagle.menu;

import java.util.Arrays;

import org.eclipse.nebula.widgets.grid.Grid;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

import ch.qos.logback.beagle.Constants;
import ch.qos.logback.beagle.util.EditorUtil;
import ch.qos.logback.beagle.util.SelectionUtil;
import ch.qos.logback.beagle.visual.CallerDataTIS;
import ch.qos.logback.beagle.visual.ClassicTISBuffer;
import ch.qos.logback.beagle.visual.ITableItemStub;
import ch.qos.logback.beagle.visual.LoggingEventTIS;
import ch.qos.logback.core.CoreConstants;

public class OnMenuSelectionAction implements SelectionListener {

  final Grid grid;
  final ClassicTISBuffer classicTISBuffer;

  public OnMenuSelectionAction(ClassicTISBuffer classicTISBuffer) {
    this.classicTISBuffer = classicTISBuffer;
    this.grid = classicTISBuffer.getGrid();
  }

  @Override
  public void widgetDefaultSelected(SelectionEvent e) {
  }

  int menuItemToIndex(MenuItem menuItem) {
    Menu menu = menuItem.getParent();
    MenuItem[] miArray = menu.getItems();
    for (int i = 0; i < miArray.length; i++) {
      if (menuItem == miArray[i])
	return i;
    }
    return Constants.NA;
  }

  private void handleMenuJump(ITableItemStub iVisualElement) {
    StackTraceElement ste = iVisualElement.getJumpData();
    if (ste != null) {
      EditorUtil.openInEditor(ste);
    }
  }

  private void handleCallerMenu(String menuItemText, int index,
      ITableItemStub iTableItemStub) {
    
    if (GridMenuBuilder.EXPLAND_CALLERS_MENU_TEXT.equals(menuItemText)) {
      StackTraceElement[] callerDataArray = getCallerDataFromTIS(iTableItemStub);

      if (callerDataArray != null && callerDataArray.length > 0) {
	if (isAlreadyExpanded(index)) {
	  return;
	}

	for (int i = 0; i < callerDataArray.length; i++) {
	  CallerDataTIS callerDataTIS = new CallerDataTIS(classicTISBuffer.getConverterFacade(),  callerDataArray[i], i);
	  classicTISBuffer.addAtIndex(callerDataTIS, index + 1 + i);
	}
      }
    } else {
      int target = index;
      if (iTableItemStub instanceof LoggingEventTIS) {
	// the next entry is a CallerDataVisualElement
	target++;
      }
      classicTISBuffer.removeNeighboringItems(target);
    }
  }

  private void handleCopyToClipboard(Display display) {
    final Clipboard cb = new Clipboard(display);
    TextTransfer textTransfer = TextTransfer.getInstance();
    String text = selectionToText(grid);
    cb.setContents(new Object[] { text }, new Transfer[] { textTransfer });
    cb.dispose();
  }

  @Override
  public void widgetSelected(SelectionEvent e) {
    MenuItem mi = (MenuItem) e.widget;

    int index = SelectionUtil.getUniqueSelection(grid);

    ITableItemStub iVisualElement = null;
    if (index != Constants.NA) {
      iVisualElement = classicTISBuffer.get(index);
    }

    switch (menuItemToIndex(mi)) {
    case GridMenuBuilder.JUMP_TO_CALLER_MENU_INDEX:
      handleMenuJump(iVisualElement);
      break;
    case GridMenuBuilder.SHOW_CALLERS_MENU_INDEX:
      handleCallerMenu(mi.getText(), index, iVisualElement);
      break;

    case GridMenuBuilder.COPY_TO_CLIPBOARD_MENU_INDEX:
      //System.out.println("**** Clipboard");
      handleCopyToClipboard(e.display);
      break;
    default:
      throw new IllegalStateException("Unexpected menu item " + mi);
    }
  }

  String selectionToText(Grid t) {
    StringBuilder buf = new StringBuilder();
    int[] selIndices = grid.getSelectionIndices();
    Arrays.sort(selIndices);
    for (int index : selIndices) {
      ITableItemStub iTableItemStub = classicTISBuffer.get(index);
      buf.append(iTableItemStub.getText());
      buf.append(CoreConstants.LINE_SEPARATOR);
    }
    return buf.toString();
  }

  StackTraceElement[] getCallerDataFromTIS(ITableItemStub iTableItemStub) {
    if (iTableItemStub instanceof LoggingEventTIS) {
      LoggingEventTIS loggingEventVisualElement = (LoggingEventTIS) iTableItemStub;
      return loggingEventVisualElement.getILoggingEvent().getCallerData();
    } else {
      return null;
    }
  }

  boolean isAlreadyExpanded(int index) {
    int next = index + 1;
    if (next >= classicTISBuffer.size()) {
      return false;
    }

    ITableItemStub iTableItemStub = classicTISBuffer.get(next);
    return (iTableItemStub instanceof CallerDataTIS);
  }

}
