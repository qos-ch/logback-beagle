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

import ch.qos.logback.beagle.util.ResourceUtil;
import ch.qos.logback.beagle.vista.ConverterFacade;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.pattern.Converter;

public class LoggingEventTIS implements ITableItemStub {

  final ConverterFacade converterFacade;
  final ILoggingEvent iLoggingEvent;
  final Color color;

  public LoggingEventTIS(ConverterFacade head, ILoggingEvent event,
      Color color) {
    this.converterFacade = head;
    this.iLoggingEvent = event;
    this.color = color;
  }

  public ILoggingEvent getILoggingEvent() {
    return iLoggingEvent;
  }

  @Override
  public Color getBackgroundColor() {
    return color;
  }

  public void populate(GridItem gridItem) {
    gridItem.setImage(0, getImage());
    int i = 1;
    for(Converter<ILoggingEvent> c: converterFacade.getConverterList()) {
      gridItem.setText(i, c.convert(iLoggingEvent));
      gridItem.setBackground(i, getBackgroundColor());
      i++;
    }
  }

  @Override
  public Image getImage() {
    if (iLoggingEvent.getLevel() == Level.ERROR) {
      return ResourceUtil.ERROR_IMG;
    }
    if (iLoggingEvent.getLevel() == Level.WARN) {
      return ResourceUtil.WARN_IMG;
    }
    return null;
  }

  @Override
  public String getText() {
    return converterFacade.getConverterList().get(0).convert(iLoggingEvent);
  }

  @Override
  public StackTraceElement getJumpData() {
    if (supportsJump()) {
      return iLoggingEvent.getCallerData()[0];
    } else {
      return null;
    }
  }

  @Override
  public boolean supportsJump() {
    return (iLoggingEvent.getCallerData() != null && iLoggingEvent
	.getCallerData().length > 0);
  }

}
