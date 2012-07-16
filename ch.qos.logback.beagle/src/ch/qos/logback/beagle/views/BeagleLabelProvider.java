/**
 * Logback Beagle
 * Copyright (C) 2006-2012, QOS.ch. All rights reserved.
 *
 * This program and the accompanying materials are licensed under
 * either the terms of the Eclipse Public License v1.0 as published by
 * the Eclipse Foundation.
 */
package ch.qos.logback.beagle.views;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

/**
 * @author Christian Trutz
 */
public class BeagleLabelProvider extends LabelProvider implements
    ITableLabelProvider {

  @Override
  public String getColumnText(Object element, int columnIndex) {
    return super.getText(element);
  }

  @Override
  public Image getColumnImage(Object element, int columnIndex) {
    return null;
  }

}
