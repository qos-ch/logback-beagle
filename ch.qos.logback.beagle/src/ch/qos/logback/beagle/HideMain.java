/**
 * Logback-beagle: The logback Console Plugin for Eclipse 
 * Copyright (C) 2006-2012, QOS.ch. All rights reserved.
 *
 * This program and the accompanying materials are licensed under
 * the terms of the Eclipse Public License v1.0 as published by
 * the Eclipse Foundation.
 */
package ch.qos.logback.beagle;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import ch.qos.logback.beagle.util.ResourceUtil;

public class HideMain {

  static int LEN = 500;

  public static void main(String[] args) {
    Display display = new Display();
    final Shell shell = new Shell(display);

    ResourceUtil.init(display);

    shell.setLayout(new FormLayout());

    FormData formData;

    Button button = new Button(shell, SWT.PUSH);
    button.setText("switch");
    formData = new FormData();
    formData.left = new FormAttachment(0, 5);
    formData.top = new FormAttachment(0, 5);
    button.setLayoutData(formData);

    final Composite comp1 = makeComposite(shell, button, "one");
    formData = new FormData();
    formData.left = new FormAttachment(0, 5);
    formData.top = new FormAttachment(button, 0);
    formData.bottom = new FormAttachment(100, -5);
    comp1.setLayoutData(formData);

    final Composite comp2 = makeComposite(shell, button, "two");
    formData = new FormData();
    formData.left = new FormAttachment(0, 5);
    formData.top = new FormAttachment(button, 0);
    formData.bottom = new FormAttachment(100, -5);
    comp2.setLayoutData(formData);

    button.addSelectionListener(new SelectionListener() {

      boolean showOne = true;

      @Override
      public void widgetDefaultSelected(SelectionEvent e) {
      }

      @Override
      public void widgetSelected(SelectionEvent e) {
	// System.out.println("widgetSelected");
	if (showOne) {
	  comp1.moveAbove(null);
	  System.out.println("moving one above");
	} else {
	  comp2.moveAbove(null);

	  System.out.println("moving two above");
	}
	showOne = !showOne;
      }

    });

    shell.open();
    while (!shell.isDisposed()) {
      if (!display.readAndDispatch())
	display.sleep();
    }
    display.dispose();
  }

  static Composite makeComposite(Shell shell, Button button, String prefix) {
    Composite composite = new Composite(shell, SWT.NONE);
    composite.setLayout(new FormLayout());
    FormData formData;
    formData = new FormData();

    final ToolBar toolBar = new ToolBar(composite, SWT.HORIZONTAL
	| SWT.SHADOW_ETCHED_OUT);

    final Menu menu = new Menu(shell, SWT.POP_UP);
    for (int i = 0; i < 8; i++) {
      MenuItem item = new MenuItem(menu, SWT.PUSH);
      item.setText("Item " + i);
    }

    formData = new FormData();
    formData.top = new FormAttachment(0, 10);
    formData.left = new FormAttachment(0, 5);
    toolBar.setLayoutData(formData);

    final ToolItem toolItem = new ToolItem(toolBar, SWT.DROP_DOWN);
    toolItem.setImage(ResourceUtil.ERROR_IMG);
    toolItem.addListener(SWT.Selection, new Listener() {

      public void handleEvent(Event event) {
	if (event.detail == SWT.ARROW) {
	  Point point = new Point(event.x, event.y);
	  point = event.display.map(toolBar, null, point);
	  menu.setLocation(point);
	  menu.setVisible(true);
	}

      }

    });

    final ToolItem toolItemB = new ToolItem(toolBar, SWT.PUSH);
    toolItemB.setImage(ResourceUtil.WARN_IMG);

    formData = new FormData();
    final Table table = new Table(composite, SWT.H_SCROLL | SWT.V_SCROLL
	| SWT.MULTI | SWT.BORDER);

    formData.left = new FormAttachment(0, 5);
    formData.top = new FormAttachment(toolBar, 5);
    formData.bottom = new FormAttachment(100, -5);

    table.setLayoutData(formData);

    for (int i = 0; i < LEN; i++) {
      new TableItem(table, SWT.NONE).setText(prefix + " " + i);
    }

    toolBar.pack();
    table.pack();
    return composite;
  }
}
