package ch.qos.logback.beagle.preferences;

import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;

import ch.qos.logback.beagle.visual.ClassicTISBuffer;
import ch.qos.logback.classic.PatternLayout;

public class PreferencesChangeListenter implements IPropertyChangeListener {

  final ClassicTISBuffer classicTISBuffer;
  final PatternLayout layout;
  
  public PreferencesChangeListenter(PatternLayout layout, ClassicTISBuffer classicTISBuffer) {
    this.layout = layout;
    this.classicTISBuffer = classicTISBuffer;
  }

  @Override
  public void propertyChange(PropertyChangeEvent event) {
    String changedProperty = event.getProperty();
    if (WorkbenchPreferences.PATTERN_PREFERENCE.equals(changedProperty)) {
      String newPattern = (String) event.getNewValue();
      if(newPattern != null) {
        layout.setPattern(newPattern);
        layout.start();
        classicTISBuffer.resetTable();
      }
    }

  }

}
