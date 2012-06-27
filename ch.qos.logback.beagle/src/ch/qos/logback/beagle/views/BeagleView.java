/**
 * Logback-beagle: The logback Console Plugin for Eclipse 
 * Copyright (C) 2006-2012, QOS.ch. All rights reserved.
 *
 * This program and the accompanying materials are licensed under
 * either the terms of the Eclipse Public License v1.0 as published by
 * the Eclipse Foundation.
 */
package ch.qos.logback.beagle.views;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

import ch.qos.logback.beagle.net.LoggingEventSocketServer;
import ch.qos.logback.beagle.util.ResourceUtil;
import ch.qos.logback.beagle.vista.MySupplierThread;
import ch.qos.logback.beagle.vista.TableMediator;

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

  private Action action1;
  private Action action2;
  private Action doubleClickAction;
  Table table;
  TableMediator tableMediator;

  /**
   * The constructor.
   */
  public BeagleView() {
  }

  /**
   * This is a callback that will allow us to create the viewer and initialize
   * it.
   */
  public void createPartControl(Composite parent) {

    ResourceUtil.init(parent.getDisplay());

    System.out.println("=============" + parent.getLayout());
    parent.setLayout(new FormLayout());

    tableMediator = new TableMediator(parent);

    LoggingEventSocketServer loggingEventSocketServer = new LoggingEventSocketServer(
	tableMediator.classicTISBuffer);
    Thread serverThread = new Thread(loggingEventSocketServer);
    serverThread.start();
    parent.addListener(SWT.Dispose, loggingEventSocketServer);

    // MySupplierThread supplierThread0 = new MySupplierThread(
    // tableMediator.classicTISBuffer);
    // supplierThread0.start();
    // parent.addListener(SWT.Dispose, supplierThread0);

    // Create the help context id for the viewer's control
    PlatformUI.getWorkbench().getHelpSystem()
	.setHelp(tableMediator.table, "ch.qos.logback.beagle.viewer");
    // makeActions();
    // hookContextMenu();
    // hookDoubleClickAction();
    // contributeToActionBars();

  }

  private void hookContextMenu() {
    MenuManager menuMgr = new MenuManager("#PopupMenu");
    menuMgr.setRemoveAllWhenShown(true);
    menuMgr.addMenuListener(new IMenuListener() {
      public void menuAboutToShow(IMenuManager manager) {
	BeagleView.this.fillContextMenu(manager);
      }
    });
    Menu menu = menuMgr.createContextMenu(table);
    table.setMenu(menu);
    // getSite().registerContextMenu(menuMgr, table);
  }

  private void contributeToActionBars() {
    IActionBars bars = getViewSite().getActionBars();
    fillLocalPullDown(bars.getMenuManager());
    fillLocalToolBar(bars.getToolBarManager());
  }

  private void fillLocalPullDown(IMenuManager manager) {
    manager.add(action1);
    manager.add(new Separator());
    manager.add(action2);
  }

  private void fillContextMenu(IMenuManager manager) {
    // manager.add(action1);
    manager.add(action2);
    // Other plug-ins can contribute there actions here
    manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
  }

  private void fillLocalToolBar(IToolBarManager manager) {
    manager.add(action1);
    // manager.add(action2);
  }

  private void makeActions() {
    action1 = new Action() {
      public void run() {
	showMessage("Action 1 executed");
      }
    };
    action1.setText("Action 1");
    action1.setToolTipText("Action 1 tooltip");
    action1.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages()
	.getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));

    action2 = new Action() {
      public void run() {
	showMessage("Action 2 executed");
      }
    };
    action2.setText("Action 2");
    action2.setToolTipText("Action 2 tooltip");
    action2.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages()
	.getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));
    doubleClickAction = new Action() {
      public void run() {
	TableItem[] tableItemArrray = table.getSelection();
	showMessage("Double-click detected on " + tableItemArrray.toString());
      }
    };
  }

  private void showMessage(String message) {
    MessageDialog.openInformation(table.getShell(), "Beagle View", message);
  }

  /**
   * Passing the focus request to the viewer's control.
   */
  public void setFocus() {
    tableMediator.table.setFocus();
  }
}