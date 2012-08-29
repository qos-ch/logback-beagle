/**
 * Logback-beagle: The logback Console Plugin for Eclipse 
 * Copyright (C) 2006-2012, QOS.ch. All rights reserved.
 *
 * This program and the accompanying materials are licensed under
 * either the terms of the Eclipse Public License v1.0 as published by
 * the Eclipse Foundation.
 */
package ch.qos.logback.beagle.view;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Sash;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

import ch.qos.logback.beagle.Activator;
import ch.qos.logback.beagle.Constants;
import ch.qos.logback.beagle.net.LoggingEventSocketServer;
import ch.qos.logback.beagle.util.ResourceUtil;

/**
 * This sample class demonstrates how to plug-in a new workbench view. The view
 * shows data obtained from the model. The sample creates a dummy model on the
 * fly, but a real implementation would connect to the model available either in
 * this or another plug-in (e.g. the workspace). The view is connected to the
 * model using a content provider.
 * <p>
 * The view uses a label provider to define how model objects should be
 * presented in the view. Each view can present the same model objects using
 * different labels and icons, if needed. Alternatively, a single label provider
 * can be shared between views in order to ensure that objects of the same type
 * are presented in the same way everywhere.
 * <p>
 */

public class BeagleView extends ViewPart {

  //
  TableMediator tableMediator;

  // left Sash position
  private FormAttachment sashLeftFormData = null;

  /**
   * {@inheritDoc}
   */
  public void init(IViewSite site, IMemento memento) throws PartInitException {
    super.init(site, memento);
    try {
      IMemento sashChild = memento.getChild("sash");
      if (sashChild != null) {
        sashLeftFormData = new FormAttachment(
            sashChild.getInteger("numerator"), sashChild.getInteger("offset"));
      }
    } catch (Exception exception) {
      Activator.getDefault().logException(exception);
    }
  }

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

    Activator.INSTANCE.getPreferenceStore().addPropertyChangeListener(
        tableMediator.preferencesChangeListenter);

    // start Beagle socket server
    LoggingEventSocketServer loggingEventSocketServer = new LoggingEventSocketServer(
        tableMediator.loggerContext, tableMediator.classicTISBuffer);
    Thread serverThread = new Thread(loggingEventSocketServer);
    serverThread.start();

    // stop Beagle socket server, if user dispose the view
    parent.addListener(SWT.Dispose, loggingEventSocketServer);

    PlatformUI.getWorkbench().getHelpSystem()
        .setHelp(tableMediator.grid, "ch.qos.logback.beagle.viewer");

    // restore saved view state
    if (sashLeftFormData != null) {
      Sash sash = tableMediator.getSash();
      FormData formData = (FormData) sash.getLayoutData();
      formData.left = sashLeftFormData;
      formData.right = new FormAttachment(sashLeftFormData.numerator,
          sashLeftFormData.offset + Constants.SASH_WIDTH);
      sash.getParent().layout();
    }

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void saveState(IMemento memento) {
    FormData formData = (FormData) tableMediator.getSash().getLayoutData();
    IMemento sashChild = memento.createChild("sash");
    sashChild.putInteger("offset", formData.left.offset);
    sashChild.putInteger("numerator", formData.left.numerator);
  }

  public TableMediator getTableMediator() {
    return tableMediator;
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
