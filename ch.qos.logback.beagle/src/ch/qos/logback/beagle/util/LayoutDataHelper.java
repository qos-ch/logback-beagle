/**
 * Logback-beagle: The logback Console Plugin for Eclipse 
 * Copyright (C) 2006-2012, QOS.ch. All rights reserved.
 *
 * This program and the accompanying materials are licensed under
 * either the terms of the Eclipse Public License v1.0 as published by
 * the Eclipse Foundation.
 */
package ch.qos.logback.beagle.util;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Control;

public class LayoutDataHelper {

  FormData formData;
  final Control control;

  public static LayoutDataHelper make(Control control) {
    return new  LayoutDataHelper(control);
  }
  
  public static LayoutDataHelper make(Control control, int width, int height) {
    return new  LayoutDataHelper(control, width, height);
  }
  
  
  public LayoutDataHelper(Control control) {
    this.control = control;
    formData = new FormData();
  }

  public LayoutDataHelper(Control control, int width, int height) {
    this.control = control;
    formData = new FormData(width, height);
  }

  // relative to the control's parent -------------------------------
  public LayoutDataHelper bottom(int offset) {
    formData.bottom = new FormAttachment(100, offset);
    return this;
  }

  public LayoutDataHelper left(int offset) {
    formData.left = new FormAttachment(0, offset);
    return this;
  }
  
  public LayoutDataHelper top(int offset) {
    formData.top = new FormAttachment(0, offset);
    return this;
  }
  
  
  public LayoutDataHelper right(int offset) {
    formData.right = new FormAttachment(100, offset);
    return this;
  }
  
  // relative to aControl, a control passed as parameter ------------------------------
  
  public LayoutDataHelper rightOf(Control aControl, int offset) {
    formData.left = new FormAttachment(aControl, offset, SWT.RIGHT);
    return this;
  }

  public LayoutDataHelper leftOf(Control aControl, int offset) {
    formData.right = new FormAttachment(aControl, offset, SWT.LEFT);
    return this;
  }
  
  public LayoutDataHelper above(Control aControl, int offset) {
    formData.bottom = new FormAttachment(aControl, offset, SWT.TOP);
    return this;
  }
  
  public LayoutDataHelper below(Control aControl, int offset) {
    formData.top = new FormAttachment(aControl, offset, SWT.BOTTOM);
    return this;
  }
  

  public FormData getFormData() {
    return formData;
  }
  
  public void set() {
    control.setLayoutData(formData);
  }

}
