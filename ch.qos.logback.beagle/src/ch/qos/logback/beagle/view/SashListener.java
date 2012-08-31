/**
 * Logback-beagle: The logback Console Plugin for Eclipse 
 * Copyright (C) 2006-2012, QOS.ch. All rights reserved.
 *
 * This program and the accompanying materials are licensed under
 * either the terms of the Eclipse Public License v1.0 as published by
 * the Eclipse Foundation.
 */
package ch.qos.logback.beagle.view;

import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Sash;

import ch.qos.logback.beagle.util.SashUtil;

public class SashListener implements Listener {

  final Sash sash;
  final FormData formData;
  
  SashListener(Sash sash, FormData formData) {
    this.sash = sash;
    this.formData = formData;
  }

  @Override
  public void handleEvent(Event e) {
    Rectangle sashRect = sash.getBounds();
    Composite parent = sash.getParent();
    Rectangle parentRect = parent.getClientArea();

    int rightLimit = parentRect.width - sashRect.width;
    if(e.x  > rightLimit)
      return;
    
    int targetX = Math.max(e.x, 0);
     
    if (e.x != sashRect.x) {
      SashUtil.setXCoordinate(sash, formData, targetX);
      parent.layout();
    }

  }
}
