/**
 * Logback-beagle: The logback Console Plugin for Eclipse 
 * Copyright (C) 2006-2012, QOS.ch. All rights reserved.
 *
 * This program and the accompanying materials are licensed under
 * either the terms of the Eclipse Public License v1.0 as published by
 * the Eclipse Foundation.
 */
package ch.qos.logback.beagle.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import ch.qos.logback.beagle.Activator;

public class BeaglePreferenceInitializer extends AbstractPreferenceInitializer {

  @Override
  public void initializeDefaultPreferences() {
    IPreferenceStore store = Activator.getDefault().getPreferenceStore();
    store.setDefault(BeaglePreferencesPage.PATTERN_PREFERENCE,
	BeaglePreferencesPage.PATTERN_PREFERENCE_DEFAULT_VALUE);
    store.setDefault(BeaglePreferencesPage.BUFFER_SIZE_PREFERENCE,
	BeaglePreferencesPage.BUFFER_SIZE_PREFERENCE_DEFAULT_VALUE);

  }

}
