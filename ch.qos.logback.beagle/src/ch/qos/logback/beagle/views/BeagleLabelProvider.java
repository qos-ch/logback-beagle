/**
 * Logback Beagle
 * Copyright (C) 2006-2012, QOS.ch. All rights reserved.
 *
 * This program and the accompanying materials are licensed under
 * either the terms of the Eclipse Public License v1.0 as published by
 * the Eclipse Foundation.
 */
package ch.qos.logback.beagle.views;

import java.util.Date;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import ch.qos.logback.classic.spi.ILoggingEvent;

/**
 * @author Christian Trutz
 */
public class BeagleLabelProvider extends LabelProvider implements
    ITableLabelProvider {

  @Override
  public String getColumnText(Object element, int columnIndex) {
    if (element instanceof ILoggingEvent) {
      ILoggingEvent event = (ILoggingEvent) element;
      switch (columnIndex) {
      case 0:
        return new Date(event.getTimeStamp()).toString();
      case 1:
        return event.getMessage();
      case 2:
        return event.getLoggerName();
      default:
        return null;
      }
    }
    return null;
  }

  @Override
  public Image getColumnImage(Object element, int columnIndex) {
    return null;
  }

}
