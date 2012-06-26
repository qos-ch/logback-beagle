/**
 * Logback-beagle: The logback Console Plugin for Eclipse 
 * Copyright (C) 2006-2012, QOS.ch. All rights reserved.
 *
 * This program and the accompanying materials are licensed under
 * either the terms of the Eclipse Public License v1.0 as published by
 * the Eclipse Foundation.
 */
package ch.qos.logback.beagle.vista;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import ch.qos.logback.beagle.Constants;
import ch.qos.logback.beagle.menu.MenuBuilder;
import ch.qos.logback.beagle.util.MetricsUtil;
import ch.qos.logback.beagle.util.ResourceUtil;

public class Vista {

  static final int OFFSET_FROM_BUTTOM = -5;

  final Table table;
  final VisualElementBuffer visualElementBuffer;
  final UnfreezeToolItemListener unfreezeButtonListener;
  final Composite parent;

  public Vista(Composite parent) {
    this.parent = parent;

    FormData formData;

    table = new Table(parent, SWT.VIRTUAL | SWT.H_SCROLL | SWT.V_SCROLL
	| SWT.MULTI | SWT.BORDER);
    table.setFont(ResourceUtil.FONT);

    int charHeight = MetricsUtil.computeCharHeight(table);
    int charWidth = MetricsUtil.computeCharWidth(table);

    formData = new FormData(Constants.ICON_SIZE, Constants.ICON_SIZE);
    Label jumpCueLabel = new Label(parent, SWT.LEFT);
    formData.left = new FormAttachment(0, charWidth);
    formData.bottom = new FormAttachment(100, OFFSET_FROM_BUTTOM);
    jumpCueLabel.setLayoutData(formData);

    formData = new FormData(30 * charWidth, charHeight);
    Label diffCueLabel = new Label(parent, SWT.LEFT);
    formData.left = new FormAttachment(jumpCueLabel, charWidth);
    formData.bottom = new FormAttachment(100, OFFSET_FROM_BUTTOM);
    diffCueLabel.setLayoutData(formData);
    diffCueLabel.setText("xxxxxxxxxxxxx");

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

    table.setLayoutData(formData);
    TableColumn tableColumn = new TableColumn(table, SWT.NULL);
    tableColumn.setText("");
    tableColumn.setWidth(100);
    tableColumn.pack();
    table.setHeaderVisible(false);
    table.setLinesVisible(false);

    table.addControlListener(new TableControlListener(charWidth));

    visualElementBuffer = new VisualElementBuffer(table);
    visualElementBuffer.diffCue = diffCueLabel;
    visualElementBuffer.jumpCue = jumpCueLabel;

    // table.setData(Constants.EVENT_BUFFER_KEY, visualElementBuffer);
    table.addListener(SWT.SetData, visualElementBuffer);
    table.addDisposeListener(visualElementBuffer);

    this.unfreezeButtonListener = new UnfreezeToolItemListener(
	visualElementBuffer);
    unfreezeToolItem.addSelectionListener(unfreezeButtonListener);

    TableItemSelectionListener tableItemSelectionListener = new TableItemSelectionListener(
	table, visualElementBuffer, unfreezeToolItem, unfreezeButtonListener);
    table.addSelectionListener(tableItemSelectionListener);

    TableSelectionViaMouseMovements myMouseListener = new TableSelectionViaMouseMovements(
	visualElementBuffer);
    table.addMouseMoveListener(myMouseListener);
    table.addMouseListener(myMouseListener);
    table.addMouseTrackListener(myMouseListener);

    table.addMouseMoveListener(new TimeDifferenceMouseListener(
	visualElementBuffer));

    Menu menu = MenuBuilder.buildMenu(visualElementBuffer);
    MenuBuilder.addOnMenuSelectionAction(menu, visualElementBuffer);
    table.setMenu(menu);
    table.setItemCount(0);
  }
}
