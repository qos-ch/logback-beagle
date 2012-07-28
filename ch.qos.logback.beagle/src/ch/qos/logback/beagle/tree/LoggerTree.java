/**
 * Logback-beagle: The logback Console Plugin for Eclipse 
 * Copyright (C) 2006-2012, QOS.ch. All rights reserved.
 *
 * This program and the accompanying materials are licensed under
 * either the terms of the Eclipse Public License v1.0 as published by
 * the Eclipse Foundation.
 */
package ch.qos.logback.beagle.tree;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import ch.qos.logback.beagle.util.ResourceUtil;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.util.LoggerNameUtil;
import ch.qos.logback.core.CoreConstants;

public class LoggerTree {

  final LoggerContext loggerContext;
  final Tree tree;
  final TreeItem rootTreeItem;
  final int IMAGE_MARGIN = 4;
  final Set<String> seenLoggerNames = new HashSet<String>();
  
  public LoggerTree(LoggerContext loggerContext, Tree tree) {
    this.loggerContext = loggerContext;
    this.tree = tree;
    this.rootTreeItem = makeRootTreeItem(tree);
    addListeners();
  }

  private TreeItem makeRootTreeItem(Tree aTree) {
    Logger rootLogger = loggerContext.getLogger(Logger.ROOT_LOGGER_NAME);
    TreeItem ti = new TreeItem(aTree, SWT.NONE);
    ti.setText(Logger.ROOT_LOGGER_NAME);
    ti.setData(rootLogger);
    return ti;
  }

  private Image levelForItem(TreeItem item) {
    Logger logger = (Logger) item.getData();
    Image trailingImage = levelToImage(logger.getLevel());
    return trailingImage;
  }

  void addListeners() {
    tree.addListener(SWT.MeasureItem, new Listener() {
      public void handleEvent(Event event) {
        TreeItem item = (TreeItem) event.item;
        Image trailingImage = levelForItem(item);
        if (trailingImage != null) {
          event.width += trailingImage.getBounds().width + IMAGE_MARGIN;
        }
      }

    });
    tree.addListener(SWT.PaintItem, new Listener() {
      public void handleEvent(Event event) {
        TreeItem item = (TreeItem) event.item;
        Image trailingImage = levelForItem(item);
        if (trailingImage != null) {
          int x = event.x + event.width + IMAGE_MARGIN;
          int itemHeight = tree.getItemHeight();
          int imageHeight = trailingImage.getBounds().height;
          int y = event.y + (itemHeight - imageHeight) / 2;
          event.gc.drawImage(trailingImage, x, y);
        }
      }
    });
  }

  Image levelToImage(Level level) {
    if (level == null)
      return null;
    switch (level.levelInt) {
    case Level.TRACE_INT:
      return ResourceUtil.getImage(ResourceUtil.T_IMG_KEY);
    case Level.DEBUG_INT:
      return ResourceUtil.getImage(ResourceUtil.D_IMG_KEY);
    case Level.INFO_INT:
      return ResourceUtil.getImage(ResourceUtil.I_IMG_KEY);
    case Level.WARN_INT:
      return ResourceUtil.getImage(ResourceUtil.W_IMG_KEY);
    case Level.ERROR_INT:
      return ResourceUtil.getImage(ResourceUtil.E_IMG_KEY);
    default:
      throw new IllegalArgumentException("Level " + level + " unsupported");
    }
  }

  public void update(String loggerName) {
    if (!seenLoggerNames.contains(loggerName)) {
      seenLoggerNames.add(loggerName);
      // create the logger
      System.out.println("adding "+loggerName);
      Logger logger = loggerContext.getLogger(loggerName);
      createTreeItemByLogger(logger);
    }
  }

  private void createTreeItemByLogger(Logger logger) {
    List<String> partList = LoggerNameUtil.computeNameParts(logger.getName());
    TreeItem parentTreeItem = rootTreeItem;
    StringBuilder loggerNameBuilder = new StringBuilder();

    for (String partStr : partList) {
      appendPart(loggerNameBuilder, partStr);
      TreeItem child = findChild(parentTreeItem, partStr);
      if (child == null) {
        child = buildChildTreeItem(parentTreeItem,
            loggerNameBuilder.toString(), partStr);
      }
      parentTreeItem = child;
    }
  }

  private TreeItem buildChildTreeItem(TreeItem parentTreeItem,
      String loggerName, String partName) {
    TreeItem child = new TreeItem(parentTreeItem, SWT.NONE);
    child.setText(partName);
    child.setData(loggerContext.getLogger(loggerName));
    return child;
  }

  private void appendPart(StringBuilder loggerNameBuilder, String partStr) {
    // add . only not under root
    if (loggerNameBuilder.length() != 0)
      loggerNameBuilder.append(CoreConstants.DOT);

    loggerNameBuilder.append(partStr);
  }

  private TreeItem findChild(TreeItem parentTreeItem, String partStr) {
    TreeItem[] children = parentTreeItem.getItems();
    for (TreeItem child : children) {
      if (partStr.equals(child.getText()))
        return child;
    }
    return null;

  }

  public void handleMenuItemSlection(MenuItem menuItem) {
    Level level = (Level) menuItem.getData();
    
    TreeItem[] selectedItems = tree.getSelection();
    if(selectedItems.length == 1) {
      TreeItem selectedItem = selectedItems[0];
      
      Logger logger = (Logger) selectedItem.getData();
      if(level == null && Logger.ROOT_LOGGER_NAME.equalsIgnoreCase(logger.getName())) {
        return;
      } else {
        logger.setLevel(level);
        tree.redraw();
      }
      
    }
  }

}
