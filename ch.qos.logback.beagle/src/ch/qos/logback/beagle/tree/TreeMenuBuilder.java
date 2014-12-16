/**
 * Logback-beagle: The logback Console Plugin for Eclipse 
 * Copyright (C) 2006-2012, QOS.ch. All rights reserved.
 *
 * This program and the accompanying materials are licensed under
 * either the terms of the Eclipse Public License v1.0 as published by
 * the Eclipse Foundation.
 */

package ch.qos.logback.beagle.tree;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

import ch.qos.logback.beagle.util.ResourceUtil;
import ch.qos.logback.beagle.visual.ClassicTISBuffer;
import ch.qos.logback.classic.Level;

public class TreeMenuBuilder {

  static public Menu buildTreeMenu(LoggerTree loggerTree, ClassicTISBuffer classicTISBuffer) {
    Menu menu = new Menu(loggerTree.tree);

    {
      MenuItem nullMenuItem = new MenuItem(menu, SWT.PUSH);
      nullMenuItem.setText("унаследованный (null)");
    }

    {
      MenuItem traceMenuItem = new MenuItem(menu, SWT.PUSH);
      traceMenuItem.setText(Level.TRACE.toString());
      traceMenuItem.setImage(ResourceUtil.getImage(ResourceUtil.T_IMG_KEY));
      traceMenuItem.setData(Level.TRACE);
    }

    {

      MenuItem debugMenuItem = new MenuItem(menu, SWT.PUSH);
      debugMenuItem.setText(Level.DEBUG.toString());
      debugMenuItem.setImage(ResourceUtil.getImage(ResourceUtil.D_IMG_KEY));
      debugMenuItem.setData(Level.DEBUG);

    }

    {
      MenuItem infoMenuItem = new MenuItem(menu, SWT.PUSH);
      infoMenuItem.setText(Level.INFO.toString());
      infoMenuItem.setImage(ResourceUtil.getImage(ResourceUtil.I_IMG_KEY));
      infoMenuItem.setData(Level.INFO);
    }

    {
      MenuItem warnMenuItem = new MenuItem(menu, SWT.PUSH);
      warnMenuItem.setText(Level.WARN.toString());
      warnMenuItem.setImage(ResourceUtil.getImage(ResourceUtil.W_IMG_KEY));
      warnMenuItem.setData(Level.WARN);
    }

    {
      MenuItem errorMenuItem = new MenuItem(menu, SWT.PUSH);
      errorMenuItem.setText(Level.ERROR.toString());
      errorMenuItem.setImage(ResourceUtil.getImage(ResourceUtil.E_IMG_KEY));
      errorMenuItem.setData(Level.ERROR);
    }

    for(MenuItem mi: menu.getItems()) {
      mi.addSelectionListener(new TreeMenuSelectionListener(loggerTree, classicTISBuffer));
    }
    
    return menu;
  }

}
