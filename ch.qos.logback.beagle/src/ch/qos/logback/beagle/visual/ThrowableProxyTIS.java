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

import ch.qos.logback.beagle.view.ConverterFacade;
import ch.qos.logback.classic.spi.IThrowableProxy;
import ch.qos.logback.classic.spi.StackTraceElementProxy;
import ch.qos.logback.classic.spi.ThrowableProxyUtil;

public class ThrowableProxyTIS implements ITableItemStub {

  // static Color COLOR = new Color(null, 209, 193, 245);
  static Color EXCEPTION_COLOR = new Color(null, 241, 241, 255);

  static final public int INDEX_FOR_INITIAL_LINE = -1;
  static final public String TAB_SUBSTITUTE = "   ";

  ConverterFacade converterFacade;
  final IThrowableProxy itp;
  final int index;
  final StackTraceElementProxy[] stepArray;
  final int commonFrames;

  final Color color;

  public ThrowableProxyTIS(ConverterFacade converterFacade, IThrowableProxy itp, int index, Color color) {
    this.converterFacade = converterFacade;
    this.itp = itp;
    this.index = index;
    this.stepArray = itp.getStackTraceElementProxyArray();
    this.commonFrames = itp.getCommonFrames();
    this.color = color;
  }

  @Override
  public Color getBackgroundColor() {
    return EXCEPTION_COLOR;
    //return color;
  }

  @Override
  public String getText() {
    StringBuilder buf = new StringBuilder();
    if (index == INDEX_FOR_INITIAL_LINE) {
      ThrowableProxyUtil.subjoinFirstLine(buf, itp);
    } else {
      int lastIndex = stepArray.length - commonFrames;
      if (index < lastIndex) {
	StackTraceElementProxy step = stepArray[index];
	buf.append(TAB_SUBSTITUTE);
	ThrowableProxyUtil.subjoinSTEP(buf, step);
      } else {
	buf.append(TAB_SUBSTITUTE);
	buf.append("... ").append(commonFrames)
	    .append(" общие рамки опущены");
      }
    }
    return buf.toString();
  }

  @Override
  public Image getImage() {
    return null;
  }

  @Override
  public StackTraceElement getJumpData() {
    if (supportsJump()) {
      return stepArray[index].getStackTraceElement();
    } else {
      return null;
    }
  }

  @Override
  public boolean supportsJump() {
    return (index >= 0);
  }

  @Override
  public void populate(GridItem gridItem) {
    int columnCount = converterFacade.getColumnCount();
    gridItem.setText(DATA_COLUMNS_OFFSET, getText());
    gridItem.setColumnSpan(DATA_COLUMNS_OFFSET, columnCount-1);
    gridItem.setBackground(DATA_COLUMNS_OFFSET, EXCEPTION_COLOR);
  }

}
