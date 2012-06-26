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

public class CallerDataVisualElement implements IVisualElement {

  static Color CALLER_DATACOLOR = new Color(null, 193, 230, 253);

  final StackTraceElement stackTraceElement;
  final int index;

  public CallerDataVisualElement(StackTraceElement stackTraceElement, int index) {
    this.stackTraceElement = stackTraceElement;
    this.index = index;
  }

  @Override
  public Color getBackgroundColor() {
    return CALLER_DATACOLOR;
  }

  @Override
  public Image getImage() {
    return null;
  }

  @Override
  public String getText() {
    String prefix = " |-- ";
    if (index == 0) {
      prefix = " + ";
    }
    return prefix + " at " + stackTraceElement.toString();
  }

  @Override
  public StackTraceElement getJumpData() {
    return new StackTraceElement(stackTraceElement.getClassName(),
	stackTraceElement.getMethodName(), stackTraceElement.getFileName(),
	stackTraceElement.getLineNumber());
  }

  @Override
  public boolean supportsJump() {
    return true;
  }

}
