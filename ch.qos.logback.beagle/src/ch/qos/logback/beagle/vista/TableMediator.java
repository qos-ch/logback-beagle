/**
 * Logback-beagle: The logback Console Plugin for Eclipse 
 * Copyright (C) 2006-2012, QOS.ch. All rights reserved.
 *
 * This program and the accompanying materials are licensed under
 * either the terms of the Eclipse Public License v1.0 as published by
 * the Eclipse Foundation.
 */
package ch.qos.logback.beagle.vista;

import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.nebula.widgets.grid.Grid;
import org.eclipse.nebula.widgets.grid.GridColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import ch.qos.logback.beagle.Activator;
import ch.qos.logback.beagle.Constants;
import ch.qos.logback.beagle.menu.MenuBuilder;
import ch.qos.logback.beagle.preferences.BeaglePreferencesChangeListenter;
import ch.qos.logback.beagle.preferences.BeaglePreferencesPage;
import ch.qos.logback.beagle.util.MetricsUtil;
import ch.qos.logback.beagle.util.ResourceUtil;
import ch.qos.logback.beagle.visual.ClassicTISBuffer;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.pattern.MessageConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.pattern.Converter;

public class TableMediator {

  static final int OFFSET_FROM_BUTTOM = -5;


  public Grid grid;
  public ClassicTISBuffer classicTISBuffer;
  public BeaglePreferencesChangeListenter preferencesChangeListenter;
  final Composite parent;
  public ConverterFacade converterFacade = new ConverterFacade();
  private LoggerContext loggerContext = new LoggerContext();

  public TableMediator(Composite parent) {
    this.parent = parent;
    init();
  }

  private void init() {
    loggerContext.setName("beagle");

    grid = new Grid(parent, SWT.VIRTUAL | SWT.H_SCROLL | SWT.V_SCROLL
	| SWT.MULTI | SWT.BORDER);
    grid.setFont(ResourceUtil.FONT);

    int charHeight = MetricsUtil.computeCharHeight(grid);
    int charWidth = MetricsUtil.computeCharWidth(grid);

    FormData formData;
    formData = new FormData(Constants.ICON_SIZE, Constants.ICON_SIZE);
    formData.left = new FormAttachment(0, charWidth);
    formData.bottom = new FormAttachment(100, OFFSET_FROM_BUTTOM);

    formData = new FormData(30 * charWidth, charHeight);
    Label diffCueLabel = new Label(parent, SWT.LEFT);
    formData.bottom = new FormAttachment(100, OFFSET_FROM_BUTTOM);
    diffCueLabel.setLayoutData(formData);
    diffCueLabel.setText("");

    ToolBar toolbar = new ToolBar(parent, SWT.HORIZONTAL);
    formData = new FormData();
    formData.right = new FormAttachment(100, -5);
    formData.bottom = new FormAttachment(100, OFFSET_FROM_BUTTOM);

    // formData.right = new FormAttachment(100, -5);
    // formData.top = new FormAttachment(0, 5);
    toolbar.setLayoutData(formData);
    ToolItem unfreezeToolItem = new ToolItem(toolbar, SWT.PUSH);
    unfreezeToolItem.setEnabled(false);
    unfreezeToolItem.setImage(ResourceUtil
	.getImage(ResourceUtil.RELEASE_SCROLL_LOCK_IMG_KEY));
    unfreezeToolItem.setToolTipText("release scroll lock");

    formData = new FormData();
    formData.top = new FormAttachment(0, 5);
    formData.left = new FormAttachment(0, 5);
    formData.right = new FormAttachment(100, -5);
    formData.bottom = new FormAttachment(toolbar, -5);

    grid.setLayoutData(formData);
    grid.setHeaderVisible(true);
    grid.setLinesVisible(false);
    grid.setAutoHeight(true);

    grid.addControlListener(new TableControlListener(charWidth));
    
    initConverterFacade();
    
    int bufSize = getPreferredBufferSize();
    classicTISBuffer = new ClassicTISBuffer(converterFacade, grid, bufSize);
    createColumns();
    classicTISBuffer.diffCue = diffCueLabel;
    grid.pack();
    
    preferencesChangeListenter = new BeaglePreferencesChangeListenter(this);

    // when the table is cleared classicTISBuffer's handleEvent method will
    // re-populate the item
    grid.addListener(SWT.SetData, classicTISBuffer);
    grid.addDisposeListener(classicTISBuffer);

    UnfreezeToolItemListener unfreezeButtonListener = new UnfreezeToolItemListener(
	classicTISBuffer);
    unfreezeToolItem.addSelectionListener(unfreezeButtonListener);

    TableItemSelectionListener tableItemSelectionListener = new TableItemSelectionListener(
	grid, classicTISBuffer, unfreezeToolItem, unfreezeButtonListener);
    grid.addSelectionListener(tableItemSelectionListener);

    TableSelectionViaMouseMovements myMouseListener = new TableSelectionViaMouseMovements(
	classicTISBuffer);
    grid.addMouseMoveListener(myMouseListener);
    grid.addMouseListener(myMouseListener);
    grid.addMouseTrackListener(myMouseListener);

    grid.addMouseMoveListener(new TimeDifferenceMouseListener(classicTISBuffer));

    Menu menu = MenuBuilder.buildMenu(classicTISBuffer);
    MenuBuilder.addOnMenuSelectionAction(menu, classicTISBuffer);
    grid.setMenu(menu);
    grid.setItemCount(0);
  }

  public void setBufferSize(int size) {
    classicTISBuffer.setBufferSize(size);
  }
  

  void disposeOfExistingColumns() {
    for(GridColumn column: grid.getColumns()) {
      column.dispose();
    }
  }
  
  public void patternChange(String newPattern) {
    if(newPattern != null) {
      converterFacade.setPattern(newPattern);
      converterFacade.start();
      grid.removeAll();
      disposeOfExistingColumns();
      createColumns();
      classicTISBuffer.rebuildGrid();
    }
  }
  
  int getPreferredBufferSize() {
    int result = BeaglePreferencesPage.BUFFER_SIZE_PREFERENCE_DEFAULT_VALUE;
    if (Activator.INSTANCE != null) {
      IPreferenceStore pStore = Activator.INSTANCE.getPreferenceStore();
      result = pStore.getInt(BeaglePreferencesPage.BUFFER_SIZE_PREFERENCE);
    }
    return result;
  }

  int getColumnSizeDialogSetting(String columnName, int defaultValue) {
    if (Activator.INSTANCE == null)
      return defaultValue;
    IDialogSettings dialogSettings = Activator.INSTANCE.getDialogSettings();
    try {
      int val = dialogSettings
	  .getInt(Constants.COLUMN_SIZE_DIALOG_SETTINGS_PREFIX + columnName);
      System.out.println("width for " + columnName + ": " + val);
      return val;
    } catch (NumberFormatException e) {
      return defaultValue;
    }
  }

  public void createColumns() {
    GridColumn column0 = new GridColumn(this.grid, SWT.NONE);
    column0.setWidth(24);

    for (Converter<ILoggingEvent> c : converterFacade.getConverterList()) {
      GridColumn column = new GridColumn(grid, SWT.NONE);
      String columnName = converterFacade.computeConverterName(c);
      column.setText(columnName);
      column.setWidth(getColumnSizeDialogSetting(columnName, 100));
      column.addControlListener(new ColumnControlListener(columnName));

      if (c instanceof MessageConverter) {
	column.setWordWrap(true);
      }
    }
  }


  private void initConverterFacade() {
    converterFacade.setContext(loggerContext);
    String pattern = BeaglePreferencesPage.PATTERN_PREFERENCE_DEFAULT_VALUE;
    if (Activator.INSTANCE != null) {
      IPreferenceStore pStore = Activator.INSTANCE.getPreferenceStore();
      pattern = pStore.getString(BeaglePreferencesPage.PATTERN_PREFERENCE);
    }
    converterFacade.setPattern(pattern);
    converterFacade.start();
  }


}
