/**
 * Logback-beagle: The logback Console Plugin for Eclipse 
 * Copyright (C) 2006-2012, QOS.ch. All rights reserved.
 *
 * This program and the accompanying materials are licensed under
 * either the terms of the Eclipse Public License v1.0 as published by
 * the Eclipse Foundation.
 */
package ch.qos.logback.beagle.view;

import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.nebula.widgets.grid.Grid;
import org.eclipse.nebula.widgets.grid.GridColumn;
import org.eclipse.swt.SWT;
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
import ch.qos.logback.beagle.util.LayoutDataHelper;
import ch.qos.logback.beagle.util.MetricsUtil;
import ch.qos.logback.beagle.util.ResourceUtil;
import ch.qos.logback.beagle.view.listener.ColumnControlListener;
import ch.qos.logback.beagle.view.listener.TableControlListener;
import ch.qos.logback.beagle.view.listener.TableItemSelectionListener;
import ch.qos.logback.beagle.view.listener.TableSelectionViaMouseMovements;
import ch.qos.logback.beagle.view.listener.TimeDifferenceMouseListener;
import ch.qos.logback.beagle.view.listener.UnfreezeToolItemListener;
import ch.qos.logback.beagle.visual.ClassicTISBuffer;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.pattern.MessageConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.pattern.Converter;

public class TableMediator {

  static final int OFFSET_FROM_BUTTOM = -10;

  final Grid grid;
  public ClassicTISBuffer classicTISBuffer;
  public BeaglePreferencesChangeListenter preferencesChangeListenter;

  final Composite parent;
  ConverterFacade converterFacade = new ConverterFacade();
  private LoggerContext loggerContext = new LoggerContext();
  Label timeDifferenceLabel;
  Label totalEventsLabel;
  ToolItem unfreezeToolItem;

  public TableMediator(Composite parent) {
    this.parent = parent;
    this.timeDifferenceLabel = new Label(parent, SWT.LEFT);
    this.totalEventsLabel = new Label(parent, SWT.LEFT);
    this.grid = new Grid(parent, SWT.VIRTUAL | SWT.H_SCROLL | SWT.V_SCROLL
	| SWT.MULTI | SWT.BORDER);

    init();
  }

  private void init() {
    loggerContext.setName("beagle");

    grid.setFont(ResourceUtil.FONT);

    int charHeight = MetricsUtil.computeCharHeight(grid);
    int charWidth = MetricsUtil.computeCharWidth(grid);

   
    LayoutDataHelper.make(this.totalEventsLabel, 12 * charWidth, charHeight)
	.left(charWidth).bottom(OFFSET_FROM_BUTTOM).set();
    this.totalEventsLabel.setText("0 events");

    Label separator0Label = new Label(parent, SWT.SEPARATOR | SWT.VERTICAL);
    LayoutDataHelper.make(separator0Label, charWidth, charHeight)
	.bottom(OFFSET_FROM_BUTTOM).rightOf(this.totalEventsLabel, 10).set();

    LayoutDataHelper.make(timeDifferenceLabel, 12 * charWidth, charHeight)
	.bottom(OFFSET_FROM_BUTTOM).rightOf(separator0Label, 10).set();

    ToolBar toolbar = new ToolBar(parent, SWT.HORIZONTAL);
    LayoutDataHelper.make(toolbar).right(-5).bottom(-5).set();

    this.unfreezeToolItem = new ToolItem(toolbar, SWT.PUSH);
    this.unfreezeToolItem.setEnabled(false);
    this.unfreezeToolItem.setImage(ResourceUtil
	.getImage(ResourceUtil.RELEASE_SCROLL_LOCK_IMG_KEY));
    this.unfreezeToolItem.setToolTipText("release scroll lock");

    LayoutDataHelper.make(grid).top(5).left(5).right(-5).above(toolbar, -1)
	.set();
    grid.setHeaderVisible(true);
    grid.setLinesVisible(false);
    grid.setAutoHeight(true);

    grid.addControlListener(new TableControlListener(charWidth));

    initConverterFacade();

    int bufSize = getPreferredBufferSize();
    classicTISBuffer = new ClassicTISBuffer(this, bufSize);
    createColumns();
    grid.pack();

    preferencesChangeListenter = new BeaglePreferencesChangeListenter(this);

    // when the table is cleared classicTISBuffer's handleEvent method will
    // re-populate the item
    grid.addListener(SWT.SetData, classicTISBuffer);
    grid.addDisposeListener(classicTISBuffer);

    UnfreezeToolItemListener unfreezeButtonListener = new UnfreezeToolItemListener(
	this);
    unfreezeToolItem.addSelectionListener(unfreezeButtonListener);

    TableItemSelectionListener tableItemSelectionListener = new TableItemSelectionListener(
	this, unfreezeToolItem);
    grid.addSelectionListener(tableItemSelectionListener);

    TableSelectionViaMouseMovements myMouseListener = new TableSelectionViaMouseMovements(
	this);
    grid.addMouseMoveListener(myMouseListener);
    grid.addMouseListener(myMouseListener);
    grid.addMouseTrackListener(myMouseListener);

    grid.addMouseMoveListener(new TimeDifferenceMouseListener(this));

    Menu menu = MenuBuilder.buildMenu(classicTISBuffer);
    MenuBuilder.addOnMenuSelectionAction(menu, classicTISBuffer);
    grid.setMenu(menu);
    grid.setItemCount(0);
  }

  public Grid getGrid() {
    return grid;
  }

  public void setBufferSize(int size) {
    classicTISBuffer.setBufferSize(size);
  }

  void disposeOfExistingColumns() {
    for (GridColumn column : grid.getColumns()) {
      column.dispose();
    }
  }

  public void patternChange(String newPattern) {
    if (newPattern != null) {
      converterFacade.setPattern(newPattern);
      converterFacade.start();
      grid.removeAll();
      disposeOfExistingColumns();
      createColumns();
      classicTISBuffer.rebuildEmptyGrid();
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

  public void setTimeDifferenceLabelText(String str) {
    timeDifferenceLabel.setText(str);
  }

  public void setTotalEventsLabelText(String str) {
    totalEventsLabel.setText(str);
  }

  public ConverterFacade getConverterFacade() {
    return converterFacade;
  }

}
