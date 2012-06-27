/**
 * Logback-beagle: The logback Console Plugin for Eclipse 
 * Copyright (C) 2006-2012, QOS.ch. All rights reserved.
 *
 * This program and the accompanying materials are licensed under
 * either the terms of the Eclipse Public License v1.0 as published by
 * the Eclipse Foundation.
 */
package ch.qos.logback.beagle.menu;

import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.events.MenuListener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Table;

import ch.qos.logback.beagle.Constants;
import ch.qos.logback.beagle.util.SelectionUtil;
import ch.qos.logback.beagle.visual.CallerDataTIS;
import ch.qos.logback.beagle.visual.ClassicTISBuffer;
import ch.qos.logback.beagle.visual.ITableItemStub;
import ch.qos.logback.beagle.visual.LoggingEventTIS;

public class DynamicMenuEnabler implements MenuListener {

  final Table table;
  final ClassicTISBuffer visualElementBuffer;

  public DynamicMenuEnabler(ClassicTISBuffer visualElementBuffer) {
    this.visualElementBuffer = visualElementBuffer;
    this.table = visualElementBuffer.getTable();
  }

  @Override
  public void menuHidden(MenuEvent e) {
  }

  MenuItem findMenuItemByIndex(Menu menu, int index) {
    MenuItem[] miArray = menu.getItems();
    if (index < 0 || index >= miArray.length) {
      throw new IllegalArgumentException(index + " is not a valid menu index");
    }
    return miArray[index];
  }

  void enableMenuItem(Menu menu, int index) {
    MenuItem mi = findMenuItemByIndex(menu, index);
    mi.setEnabled(true);
  }

  void disableMenuItem(Menu menu, int index) {
    MenuItem mi = findMenuItemByIndex(menu, index);
    mi.setEnabled(false);
  }

  void defaultState(Menu menu) {
    disableMenuItem(menu, MenuBuilder.JUMP_TO_CALLER_MENU_INDEX);
    disableMenuItem(menu, MenuBuilder.SHOW_CALLERS_MENU_INDEX);
    MenuItem mi = findMenuItemByIndex(menu, MenuBuilder.SHOW_CALLERS_MENU_INDEX);
    mi.setText(MenuBuilder.EXPLAND_CALLERS_MENU_TEXT);
  }

  @Override
  public void menuShown(MenuEvent e) {
    int selCount = table.getSelectionCount();

    System.out.println("menu shown, items selected="
	+ table.getSelectionCount());
    Menu menu = (Menu) e.widget;

    defaultState(menu);

    if (selCount == 1) {
      int selectionIndex = SelectionUtil.getUniqueSelection(table);
      if (selectionIndex == Constants.NA) {
	return;
      }
      ITableItemStub iVisualElement = visualElementBuffer.get(selectionIndex);

      if (iVisualElement.supportsJump()) {
	enableMenuItem(menu, MenuBuilder.JUMP_TO_CALLER_MENU_INDEX);
      }

      MenuItem callersMenuItem = findMenuItemByIndex(menu,
	  MenuBuilder.SHOW_CALLERS_MENU_INDEX);
      if (iVisualElement instanceof LoggingEventTIS) {
	LoggingEventTIS leve = (LoggingEventTIS) iVisualElement;
	if (alreadyCallerExpanded(selectionIndex)) {
	  callersMenuItem.setText(MenuBuilder.COLLAPSE_CALLERS_MENU_TEXT);
	  callersMenuItem.setEnabled(true);
	} else if (leve.supportsJump()) {
	  callersMenuItem.setEnabled(true);
	}
      } else if (iVisualElement instanceof CallerDataTIS) {
	callersMenuItem.setEnabled(true);
	callersMenuItem.setText(MenuBuilder.COLLAPSE_CALLERS_MENU_TEXT);
      }
    }
  }

  boolean alreadyCallerExpanded(int index) {
    int next = index + 1;
    if (next >= visualElementBuffer.size()) {
      return false;
    }

    ITableItemStub visualElem = visualElementBuffer.get(next);
    return (visualElem instanceof CallerDataTIS);

  }

  boolean isCallerDataVisualElement(int selectionIndex) {
    return (visualElementBuffer.get(selectionIndex) instanceof CallerDataTIS);
  }
}
