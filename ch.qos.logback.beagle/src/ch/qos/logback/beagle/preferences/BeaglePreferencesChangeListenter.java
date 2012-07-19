/**
 * Logback-beagle: The logback Console Plugin for Eclipse 
 * Copyright (C) 2006-2012, QOS.ch. All rights reserved.
 *
 * This program and the accompanying materials are licensed under
 * either the terms of the Eclipse Public License v1.0 as published by
 * the Eclipse Foundation.
 */
package ch.qos.logback.beagle.preferences;

import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;

import ch.qos.logback.beagle.view.TableMediator;

/**
 * This IPropertyChangeListener reacts to preference changes and updates BeagleView accordingly. 
 * 
 * @author ceki
 * @since 1.0.0
 */
public class BeaglePreferencesChangeListenter implements IPropertyChangeListener {

  TableMediator tableMidiator;
  public BeaglePreferencesChangeListenter(TableMediator tableMidiator) {
    this.tableMidiator = tableMidiator;
  }

  @Override
  public void propertyChange(PropertyChangeEvent event) {
    String changedProperty = event.getProperty();
    if (BeaglePreferencesPage.PATTERN_PREFERENCE.equals(changedProperty)) {
      updateLayoutPattern(event);
    } else if (BeaglePreferencesPage.BUFFER_SIZE_PREFERENCE.equals(changedProperty)) {
      updateBufferSize(event);
    }

  }

  private void updateBufferSize(PropertyChangeEvent event) {
    int newBufferSize = (Integer) event.getNewValue();
    tableMidiator.setBufferSize(newBufferSize);
  }

  private void updateLayoutPattern(PropertyChangeEvent event) {
    String newPattern = (String) event.getNewValue();
    tableMidiator.patternChange(newPattern);
  }

}
