/**
 * Logback-beagle: The logback Console Plugin for Eclipse 
 * Copyright (C) 2006-2012, QOS.ch. All rights reserved.
 *
 * This program and the accompanying materials are licensed under
 * either the terms of the Eclipse Public License v1.0 as published by
 * the Eclipse Foundation.
 */
package ch.qos.logback.beagle.visual;

import org.eclipse.nebula.widgets.grid.GridItem;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;

import ch.qos.logback.beagle.vista.ConverterFacade;

public class CallerDataTIS implements ITableItemStub {

  static Color CALLER_DATACOLOR = new Color(null, 193, 230, 253);

  ConverterFacade converterFacade;
  final StackTraceElement stackTraceElement;
  final int index;

  public CallerDataTIS(ConverterFacade converterFacade, StackTraceElement stackTraceElement, int index) {
    this.converterFacade = converterFacade;
    this.stackTraceElement = stackTraceElement;
    this.index = index;
  }


  public Color getBackgroundColor() {
    return CALLER_DATACOLOR;
  }

  public Image getImage() {
    return null;
  }

  public String getText() {
    String prefix = " |-- ";
    if (index == 0) {
      prefix = " + ";
    }
    return prefix + " at " + stackTraceElement.toString();
  }

  public StackTraceElement getJumpData() {
    return new StackTraceElement(stackTraceElement.getClassName(),
	stackTraceElement.getMethodName(), stackTraceElement.getFileName(),
	stackTraceElement.getLineNumber());
  }

  public boolean supportsJump() {
    return true;
  }


  @Override
  public void populate(GridItem gridItem) {
    int columnCount = converterFacade.getColumnCount();
    gridItem.setText(0, getText());
    gridItem.setColumnSpan(0, columnCount-1);
    gridItem.setBackground(0, getBackgroundColor());
    
  }

}
