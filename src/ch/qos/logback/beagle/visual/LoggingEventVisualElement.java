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
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.PatternLayout;
import ch.qos.logback.classic.spi.ILoggingEvent;

public class LoggingEventVisualElement implements IVisualElement {

  public ILoggingEvent getILoggingEvent() {
    return iLoggingEvent;
  }

  static PatternLayout LAYOUT = new PatternLayout();

  Color orange = new Color(null, 242, 198, 174);
  Color red = new Color(null, 253, 139, 113);

  static {
    LoggerContext c = new LoggerContext();
    c.setName("bogus");
    LAYOUT.setContext(c);
    LAYOUT.setPattern("%-23.20d %-5level %-21([%t]) %-30logger{30} - %m%nopex");
    LAYOUT.start();
  }

  final ILoggingEvent iLoggingEvent;
  final Color color;

  public LoggingEventVisualElement(ILoggingEvent event, Color color) {
    this.iLoggingEvent = event;
    this.color = color;
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
    return LAYOUT.doLayout(iLoggingEvent);
  }

  @Override
  public StackTraceElement getJumpData() {
    if(supportsJump()) {
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
