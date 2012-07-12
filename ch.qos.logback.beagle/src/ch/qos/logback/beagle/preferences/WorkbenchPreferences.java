package ch.qos.logback.beagle.preferences;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import ch.qos.logback.beagle.Activator;

public class WorkbenchPreferences extends FieldEditorPreferencePage implements
    IWorkbenchPreferencePage {

  public static final String PATTERN_PREFERENCE = "ch.qos.logback.beagle.Pattern"; 
  public static final String PATTERN_PREFERENCE_DEFAULT_VALUE = "%date %-5level %-20([%thread]) %logger{32} %message";

  private StringFieldEditor patternEditor;
  
  
  public WorkbenchPreferences() {
    super(GRID);
    setPreferenceStore(Activator.INSTANCE.getPreferenceStore());
  }



  @Override
  public void init(IWorkbench workbench) {
  }


  @Override
  protected void createFieldEditors() {
    patternEditor = new StringFieldEditor(PATTERN_PREFERENCE, "Pattern", getFieldEditorParent());
    addField(patternEditor);
  }

}
