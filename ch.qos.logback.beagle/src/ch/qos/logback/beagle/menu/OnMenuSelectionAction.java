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

import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Table;

import ch.qos.logback.beagle.Constants;
import ch.qos.logback.beagle.util.SelectionUtil;
import ch.qos.logback.beagle.vista.VisualElementBuffer;
import ch.qos.logback.beagle.visual.CallerDataVisualElement;
import ch.qos.logback.beagle.visual.IVisualElement;
import ch.qos.logback.beagle.visual.LoggingEventVisualElement;
import ch.qos.logback.core.CoreConstants;

public class OnMenuSelectionAction implements SelectionListener {

  final Table table;
  final VisualElementBuffer visualElementBuffer;

  public OnMenuSelectionAction(VisualElementBuffer visualElementBuffer) {
    this.visualElementBuffer = visualElementBuffer;
    this.table = visualElementBuffer.getTable();
  }

  @Override
  public void widgetDefaultSelected(SelectionEvent e) {
    System.out.println("MyMenuListener.widgetDefaultSelected");

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

  private void handleMenuJump(IVisualElement iVisualElement) {
    StackTraceElement ste = iVisualElement.getJumpData();
    if (ste != null) {
      System.out.println("Jump to " + ste.getClassName());
    }
  }

  private void handleCallerMenu(String menuItemText, int index,
      IVisualElement iVisualElement) {
    if (MenuBuilder.EXPLAND_CALLERS_MENU_TEXT.equals(menuItemText)) {
      StackTraceElement[] callerDataArray = getCallerDataFromVisualElement(iVisualElement);

      if (callerDataArray != null && callerDataArray.length > 0) {
	if (checkForDoubleExpansion(index)) {
	  System.out.println("doubvle expansion");
	  return;
	}

	for (int i = 0; i < callerDataArray.length; i++) {
	  CallerDataVisualElement cdVisualElement = new CallerDataVisualElement(
	      callerDataArray[i], i);
	  visualElementBuffer.add(cdVisualElement, index + 1 + i);
	}
      }
    } else {
      int target = index;
      if (iVisualElement instanceof LoggingEventVisualElement) {
	// the next entry is a CallerDataVisualElement
	target++;
      }
      visualElementBuffer.removeNeighboringCallerDataVisualElements(target);
    }
  }

  private void handleCopyToClipboard(Display display) {
    final Clipboard cb = new Clipboard(display);
    TextTransfer textTransfer = TextTransfer.getInstance();
    String text = selectionToText(table);
    cb.setContents(new Object[] { text }, new Transfer[] { textTransfer });
    cb.dispose();
  }

  @Override
  public void widgetSelected(SelectionEvent e) {
    MenuItem mi = (MenuItem) e.widget;

    int index = SelectionUtil.getUniqueSelection(table);

    IVisualElement iVisualElement = null;
    if (index != Constants.NA) {
      iVisualElement = visualElementBuffer.get(index);
    }

    switch (menuItemToIndex(mi)) {
    case MenuBuilder.JUMP_TO_CALLER_MENU_INDEX:
      handleMenuJump(iVisualElement);
      break;
    case MenuBuilder.SHOW_CALLERS_MENU_INDEX:
      handleCallerMenu(mi.getText(), index, iVisualElement);
      break;

    case MenuBuilder.COPY_TO_CLIPBOARD_MENU_INDEX:
      System.out.println("**** Clipboard");
      handleCopyToClipboard(e.display);
      break;
    default:
      throw new IllegalStateException("Unexpected menu item " + mi);
    }
  }

  String selectionToText(Table t) {
    StringBuilder buf = new StringBuilder();
    int[] selIndices = table.getSelectionIndices();
    Arrays.sort(selIndices);
    for (int index : selIndices) {
      IVisualElement ve = visualElementBuffer.get(index);
      buf.append(ve.getText());
      buf.append(CoreConstants.LINE_SEPARATOR);
    }
    return buf.toString();
  }

  StackTraceElement[] getCallerDataFromVisualElement(
      IVisualElement iVisualElement) {
    if (iVisualElement instanceof LoggingEventVisualElement) {
      LoggingEventVisualElement loggingEventVisualElement = (LoggingEventVisualElement) iVisualElement;
      return loggingEventVisualElement.getILoggingEvent().getCallerData();
    } else {
      return null;
    }
  }

  boolean checkForDoubleExpansion(int index) {
    int next = index + 1;
    if (next >= visualElementBuffer.size()) {
      return false;
    }

    IVisualElement visualElem = visualElementBuffer.get(next);
    return (visualElem instanceof CallerDataVisualElement);

  }

}
