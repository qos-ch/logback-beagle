package ch.qos.logback.beagle.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import ch.qos.logback.beagle.Activator;

public class PreferenceInitializer extends AbstractPreferenceInitializer {

  @Override
  public void initializeDefaultPreferences() {
    IPreferenceStore store = Activator.getDefault().getPreferenceStore();
    store.setDefault(WorkbenchPreferences.PATTERN_PREFERENCE,
	WorkbenchPreferences.PATTERN_PREFERENCE_DEFAULT_VALUE);

  }

}
