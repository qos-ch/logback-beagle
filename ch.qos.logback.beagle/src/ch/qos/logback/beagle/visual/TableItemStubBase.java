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

abstract public class TableItemStubBase implements ITableItemStub {

  public void populate(GridItem ti) {
    ti.setText(getText());
    ti.setBackground(this.getBackgroundColor());
    ti.setImage(this.getImage());  
  }
}
