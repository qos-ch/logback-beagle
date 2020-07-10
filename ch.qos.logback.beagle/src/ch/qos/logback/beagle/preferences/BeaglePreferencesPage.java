/**
 * Logback-beagle: The logback Console Plugin for Eclipse 
 * Copyright (C) 2006-2012, QOS.ch. All rights reserved.
 *
 * This program and the accompanying materials are licensed under
 * either the terms of the Eclipse Public License v1.0 as published by
 * the Eclipse Foundation.
 */
package ch.qos.logback.beagle.preferences;

import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import ch.qos.logback.beagle.Activator;

/**
 * 
 * @author ceki
 * 
 */
public class BeaglePreferencesPage extends FieldEditorPreferencePage implements
    IWorkbenchPreferencePage {

  public static final String PATTERN_PREFERENCE = "ch.qos.logback.beagle.Pattern";
  public static final String PATTERN_PREFERENCE_DEFAULT_VALUE = "%date %-5level %thread %logger{48} - %message";

  public static final String BUFFER_SIZE_PREFERENCE = "ch.qos.logback.beagle.BufferSize";
  public static final int BUFFER_SIZE_PREFERENCE_DEFAULT_VALUE = 10 * 100;
  static final int MIN_BUFFER_SIZE = 100;
  static final int MAX_BUFFER_SIZE = 100 * 1000;

  private StringFieldEditor patternEditor;
  private IntegerFieldEditor bufferSizeEditor;

  public BeaglePreferencesPage() {
    super(GRID);
    setPreferenceStore(Activator.INSTANCE.getPreferenceStore());
    setDescription("настройки Beagle (logback console) :");
  }

  @Override
  public void init(IWorkbench workbench) {
  }

  @Override
  protected void checkState() {
    super.checkState();
    if (!isValid())
      return;
    int val = bufferSizeEditor.getIntValue();
    if (val <= MIN_BUFFER_SIZE) {
      setErrorMessage("Размер буфера должен быть больше " + MIN_BUFFER_SIZE);
      setValid(false);
    } else if (val > MAX_BUFFER_SIZE) {
      setErrorMessage("Размер буфера должен быть меньше " + MAX_BUFFER_SIZE);
      setValid(false);
    }
  }

  public void propertyChange(PropertyChangeEvent event) {
    super.propertyChange(event);
    if (FieldEditor.VALUE.equals(event.getProperty())) {
      if (event.getSource() == bufferSizeEditor) {
	checkState();
      }
    }
  }

  @Override
  protected void createFieldEditors() {
    patternEditor = new StringFieldEditor(PATTERN_PREFERENCE,
	"шаблон строки-лога:", getFieldEditorParent());
    addField(patternEditor);

    bufferSizeEditor = new IntegerFieldEditor(BUFFER_SIZE_PREFERENCE,
	"размер буфера (строк):", getFieldEditorParent());
    addField(bufferSizeEditor);
  }

}
