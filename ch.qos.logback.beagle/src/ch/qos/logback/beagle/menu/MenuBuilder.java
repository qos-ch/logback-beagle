/**
 * Logback-beagle: The logback Console Plugin for Eclipse 
 * Copyright (C) 2006-2012, QOS.ch. All rights reserved.
 *
 * This program and the accompanying materials are licensed under
 * either the terms of the Eclipse Public License v1.0 as published by
 * the Eclipse Foundation.
 */
package ch.qos.logback.beagle.menu;

import static ch.qos.logback.beagle.util.ResourceUtil.COPY_CLIPBAORD_IMG_KEY;
import static ch.qos.logback.beagle.util.ResourceUtil.EXPAND_CALLERS_IMG_KEY;
import static ch.qos.logback.beagle.util.ResourceUtil.JUMP_IMG_KEY;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

import ch.qos.logback.beagle.util.ResourceUtil;
import ch.qos.logback.beagle.visual.ClassicTISBuffer;

public class MenuBuilder {

  final public static String JUMP_TO_CALLER_MENU_TEXT = "Jump to caller";
  final public static String EXPLAND_CALLERS_MENU_TEXT = "Expand caller data";
  final public static String COLLAPSE_CALLERS_MENU_TEXT = "Collapse caller data";
  final public static String COPY_TO_CLIPBOARD_MENU_TEXT = "Copy to clipboard";

  final public static int JUMP_TO_CALLER_MENU_INDEX = 0;
  final public static int SHOW_CALLERS_MENU_INDEX = 1;
  final public static int COPY_TO_CLIPBOARD_MENU_INDEX = 2;

  static public Menu buildMenu(ClassicTISBuffer classicTISBuffer) {
    Menu menu = new Menu(classicTISBuffer.getTable());

    MenuItem jumpToCaller = new MenuItem(menu, SWT.PUSH);
    jumpToCaller.setText(MenuBuilder.JUMP_TO_CALLER_MENU_TEXT);
    jumpToCaller.setImage(ResourceUtil.getImage(JUMP_IMG_KEY));

    MenuItem expandCallersMenuItem = new MenuItem(menu, SWT.PUSH);
    expandCallersMenuItem.setText(MenuBuilder.EXPLAND_CALLERS_MENU_TEXT);
    expandCallersMenuItem.setImage(ResourceUtil
	.getImage(EXPAND_CALLERS_IMG_KEY));

    MenuItem otherMenuItem = new MenuItem(menu, SWT.PUSH);
    otherMenuItem.setText(MenuBuilder.COPY_TO_CLIPBOARD_MENU_TEXT);
    otherMenuItem.setImage(ResourceUtil.getImage(COPY_CLIPBAORD_IMG_KEY));

    if (menu != null) {
      menu.addMenuListener(new DynamicMenuEnabler(classicTISBuffer));
    }

    return menu;
  }

  static public void addOnMenuSelectionAction(Menu menu,
      ClassicTISBuffer visualElementBuffer) {
    OnMenuSelectionAction onMenuSelectionAction = new OnMenuSelectionAction(
	visualElementBuffer);
    for (MenuItem menuItem : menu.getItems()) {
      menuItem.addSelectionListener(onMenuSelectionAction);
    }

  }

}
