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
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;

import ch.qos.logback.beagle.util.ResourceUtil;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;

public class TreeMain {

  public static void main(String[] args) {
    LoggerContext loggerContext = new LoggerContext();
    Display display = new Display();
    ResourceUtil.init(display);
    Shell shell = new Shell(display);
    final Tree tree = new Tree(shell, SWT.BORDER);
    Rectangle clientArea = shell.getClientArea();
    tree.setBounds(clientArea.x, clientArea.y, 200, 200);

    LoggerTree loggerTree = new LoggerTree(loggerContext, tree);
    tree.setMenu(TreeMenuBuilder.buildTreeMenu(loggerTree));
    loggerTree.update("com.foo.Bar");
    loggerTree.update("com.foo.Bar");
    
    loggerContext.getLogger("org.apache.struts").setLevel(Level.ERROR);
    
    loggerTree.update("org.apache.struts.BlahAction");
    
    shell.pack();
    shell.open();
    while (!shell.isDisposed()) {
      if (!display.readAndDispatch())
        display.sleep();
    }
    display.dispose();

  }
}
