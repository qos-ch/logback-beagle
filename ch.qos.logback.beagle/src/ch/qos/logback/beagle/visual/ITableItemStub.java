/**
 * Logback-beagle: The logback Console Plugin for Eclipse 
 * Copyright (C) 2006-2012, QOS.ch. All rights reserved.
 *
 * This program and the accompanying materials are licensed under
 * either the terms of the Eclipse Public License v1.0 as published by
 * the Eclipse Foundation.
 */
package ch.qos.logback.beagle.visual;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.TableItem;

public interface ITableItemStub {
  String getText();
  Color getBackgroundColor();
  Image getImage();
  
  void populate(TableItem ti);
  boolean supportsJump();
  StackTraceElement getJumpData();
}
