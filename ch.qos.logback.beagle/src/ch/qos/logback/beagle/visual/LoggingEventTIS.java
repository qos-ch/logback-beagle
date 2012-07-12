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

import ch.qos.logback.beagle.util.ResourceUtil;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.PatternLayout;
import ch.qos.logback.classic.spi.ILoggingEvent;

public class LoggingEventTIS extends TableItemStubBase {


  final PatternLayout layout;
  final ILoggingEvent iLoggingEvent;
  final Color color;

  public LoggingEventTIS(PatternLayout layout, ILoggingEvent event, Color color) {
    this.layout = layout;
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
    return layout.doLayout(iLoggingEvent);
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
