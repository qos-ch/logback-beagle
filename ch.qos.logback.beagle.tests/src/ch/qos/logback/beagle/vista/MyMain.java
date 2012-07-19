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
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import ch.qos.logback.beagle.net.EventConsumerThread;
import ch.qos.logback.beagle.util.ResourceUtil;

public class MyMain {

	static final int OFFSET_FROM_BUTTOM = -5;

	public static void main(String[] args) {;
		Display display = new Display();
		ResourceUtil.init(display);

		Shell shell = new Shell(display);
		shell.setText("Logback Beagle");
		shell.setBounds(100, 100, 500, 500);
		shell.setLayout(new FormLayout());

		TableMediator tableMediator = new TableMediator(shell);

		EventConsumerThread eventConsumerThread = new EventConsumerThread(
				tableMediator.classicTISBuffer);
		MySupplierThread supplierThread0 = new MySupplierThread(eventConsumerThread.getBlockingQueue());

		display.addListener(SWT.Dispose, supplierThread0);
		supplierThread0.start();

		display.addListener(SWT.Dispose, eventConsumerThread);
		eventConsumerThread.start();

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
