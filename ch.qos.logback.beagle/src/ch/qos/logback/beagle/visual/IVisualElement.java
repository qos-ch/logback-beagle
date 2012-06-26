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

public interface IVisualElement {

  String getText();

  Color getBackgroundColor();

  Image getImage();

  boolean supportsJump();

  StackTraceElement getJumpData();

}
