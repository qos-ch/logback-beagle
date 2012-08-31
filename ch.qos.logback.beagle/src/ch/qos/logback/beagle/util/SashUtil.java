/**
 * Logback-beagle: The logback Console Plugin for Eclipse 
 * Copyright (C) 2006-2012, QOS.ch. All rights reserved.
 *
 * This program and the accompanying materials are licensed under
 * either the terms of the Eclipse Public License v1.0 as published by
 * the Eclipse Foundation.
 */
package ch.qos.logback.beagle.util;

import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Sash;

import ch.qos.logback.beagle.Constants;

public class SashUtil {

  static public void setXCoordinate(Sash sash, FormData formData, int x) {
    formData.left = new FormAttachment(0, x);
    formData.right = new FormAttachment(0, x+Constants.SASH_WIDTH);
  }

  public static int getCurrentXCoorinate(Sash sash) {
    Object layoutData = sash.getLayoutData();
    if(layoutData == null) {
      return -1;
    }
    FormData formData = (FormData) layoutData;
    return formData.left.offset;
  }
}
