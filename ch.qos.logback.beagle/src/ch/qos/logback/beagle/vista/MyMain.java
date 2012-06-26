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
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import ch.qos.logback.beagle.util.ResourceUtil;

public class MyMain {

  static final int OFFSET_FROM_BUTTOM = -5;

  /**
   * @param args
   */
  /**
   * @param args
   */
  public static void main(String[] args) {
    Display display = new Display();
    ResourceUtil.init(display);

    Shell shell = new Shell(display);
    shell.setText("Logback Beagle");
    shell.setBounds(100, 100, 500, 500);
    shell.setLayout(new FormLayout());

    FormData formData;

    Button button = new Button(shell, SWT.PUSH);
    button.setText("switch");
    formData = new FormData();
    formData.left = new FormAttachment(0, 5);
    formData.top = new FormAttachment(0, 5);
    button.setLayoutData(formData);

    final VistaManager vistaManager = VistaManager.getInstance();
    final Vista vista0 = VistaManager.buildVista(shell, button);
    vistaManager.put("V0", vista0);

    final Vista vista1 = VistaManager.buildVista(shell, button);
    vistaManager.put("V1", vista1);

    vistaManager.setCurrent("V0");

    MySupplierThread supplierThread0 = new MySupplierThread(
	vista0.visualElementBuffer, vista0.unfreezeButtonListener);
    display.addListener(SWT.Dispose, supplierThread0);
    supplierThread0.start();

    MySupplierThread supplierThread1 = new MySupplierThread(
	vista1.visualElementBuffer, vista1.unfreezeButtonListener);
    display.addListener(SWT.Dispose, supplierThread1);
    supplierThread1.start();

    button.addSelectionListener(new SelectionListener() {

      @Override
      public void widgetDefaultSelected(SelectionEvent e) {
      }

      @Override
      public void widgetSelected(SelectionEvent e) {
	Vista current = vistaManager.currentVista;

	if (current == null) {
	  vistaManager.setCurrent("V0");
	} else if (vista0 == current) {
	  vistaManager.setCurrent("V1");
	} else if (vista1 == current) {
	  vistaManager.setCurrent("V0");
	}
	;
      }

    });

    shell.open();
    while (!shell.isDisposed()) {
      if (!display.readAndDispatch())
	display.sleep();
    }
    try {
      display.dispose();
    } catch (Exception e) {
      e.printStackTrace();
    }
    ResourceUtil.dispose();
    System.out.println("exiting");
  }

}
