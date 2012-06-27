/**
 * Logback-beagle: The logback Console Plugin for Eclipse 
 * Copyright (C) 2006-2012, QOS.ch. All rights reserved.
 *
 * This program and the accompanying materials are licensed under
 * either the terms of the Eclipse Public License v1.0 as published by
 * the Eclipse Foundation.
 */
package ch.qos.logback.beagle.vista;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

public class VistaManager {

  static VistaManager SINGLETON = new VistaManager();

  static VistaManager getInstance() {
    return SINGLETON;
  }

  static private TableMediator buildVista(Composite composite, FormData formData) {
    Composite vistaContainer = new Composite(composite, SWT.NONE);
    vistaContainer.setLayout(new FormLayout());
    formData.left = new FormAttachment(0, 5);
    formData.right = new FormAttachment(100, -5);
    formData.bottom = new FormAttachment(100, -5);
    vistaContainer.setLayoutData(formData);

    return new TableMediator(vistaContainer);
  }

  static TableMediator buildVista(Shell shell, Button button) {
    FormData formData = new FormData();
    TableMediator vista = buildVista(shell, formData);
    formData.top = new FormAttachment(button, 5);
    return vista;
  }

  static TableMediator buildVista(Composite parent) {
    FormData formData = new FormData();
    TableMediator vista = buildVista(parent, formData);
    formData.top = new FormAttachment(0, 5);
    return vista;
  }

  Map<String, TableMediator> vistaMap = new HashMap<String, TableMediator>();
  TableMediator currentVista;

  void setCurrent(TableMediator vista) {
    TableMediator oldVista = currentVista;
    if (oldVista != null) {
      oldVista.parent.setVisible(false);
    }
    currentVista = vista;
    currentVista.parent.setVisible(true);
    currentVista.parent.moveAbove(null);
  }

  public void setCurrent(String key) {
    TableMediator vista = get(key);
    if (vista == null) {
      return;
    }
    setCurrent(vista);
  }

  void put(String key, TableMediator value) {
    vistaMap.put(key, value);
  }

  TableMediator get(String key) {
    return vistaMap.get(key);
  }

}
