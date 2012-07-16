/**
 * Logback-beagle: The logback Console Plugin for Eclipse 
 * Copyright (C) 2006-2012, QOS.ch. All rights reserved.
 *
 * This program and the accompanying materials are licensed under
 * either the terms of the Eclipse Public License v1.0 as published by
 * the Eclipse Foundation.
 */
package ch.qos.logback.beagle.views;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.nebula.jface.gridviewer.GridTableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

import ch.qos.logback.beagle.Activator;
import ch.qos.logback.beagle.net.LoggingEventSocketServer;
import ch.qos.logback.beagle.util.ResourceUtil;
import ch.qos.logback.beagle.vista.TableMediator;

/**
 * 
 * @author Christian Trutz
 * 
 */
public class BeagleView extends ViewPart {

  //
  TableMediator tableMediator;

  /**
   * 
   * @param parent
   */
  @Override
  public void createPartControl(Composite parent) {

    parent.setLayout(new FormLayout());

    // initialize Beagle table
    ResourceUtil.init(parent.getDisplay());
    tableMediator = new TableMediator(parent);

    GridTableViewer gridTableViewer = new GridTableViewer(tableMediator.grid);
    gridTableViewer.setLabelProvider(new BeagleLabelProvider());
    gridTableViewer.setContentProvider(new ArrayContentProvider());
    gridTableViewer.setInput(tableMediator);

    Activator.INSTANCE.getPreferenceStore().addPropertyChangeListener(
        tableMediator.preferencesChangeListenter);

    // start Beagle socket server
    LoggingEventSocketServer loggingEventSocketServer = new LoggingEventSocketServer(
        tableMediator.classicTISBuffer);
    Thread serverThread = new Thread(loggingEventSocketServer);
    serverThread.start();

    // stop Beagle socket server, if user dispose the view
    parent.addListener(SWT.Dispose, loggingEventSocketServer);

    PlatformUI.getWorkbench().getHelpSystem()
        .setHelp(tableMediator.grid, "ch.qos.logback.beagle.viewer");

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setFocus() {
    tableMediator.grid.setFocus();
  }

  @Override
  public void dispose() {
    super.dispose();
    Activator.INSTANCE.getPreferenceStore().removePropertyChangeListener(
        tableMediator.preferencesChangeListenter);
  }
}
