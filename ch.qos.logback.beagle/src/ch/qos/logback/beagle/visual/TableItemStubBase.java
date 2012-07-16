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

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.pattern.Converter;

abstract public class TableItemStubBase implements ITableItemStub {

  public void populate(Converter<ILoggingEvent> head, GridItem gridItem) {
    
  }
}
