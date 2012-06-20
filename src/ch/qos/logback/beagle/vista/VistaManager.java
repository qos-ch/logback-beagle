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

	static private Vista buildVista(Composite composite, FormData formData) {
		Composite vistaContainer = new Composite(composite, SWT.NONE);
		vistaContainer.setLayout(new FormLayout());
		formData.left = new FormAttachment(0, 5);
		formData.right = new FormAttachment(100, -5);
		formData.bottom = new FormAttachment(100, -5);
		vistaContainer.setLayoutData(formData);

		return new Vista(vistaContainer);
	}

	static Vista buildVista(Shell shell, Button button) {
		FormData formData = new FormData();
		Vista vista = buildVista(shell, formData);
		formData.top = new FormAttachment(button, 5);
		return vista;
	}

	static Vista buildVista(Composite parent) {
		FormData formData = new FormData();
		Vista vista = buildVista(parent, formData);
		formData.top = new FormAttachment(0, 5);
		return vista;
	}

	Map<String, Vista> vistaMap = new HashMap<String, Vista>();
	Vista currentVista;

	void setCurrent(Vista vista) {
		Vista oldVista = currentVista;
		if (oldVista != null) {
			oldVista.parent.setVisible(false);
		}
		currentVista = vista;
		currentVista.parent.setVisible(true);
		currentVista.parent.moveAbove(null);
	}

	public void setCurrent(String key) {
		Vista vista = get(key);
		if (vista == null) {
			return;
		}
		setCurrent(vista);
	}

	void put(String key, Vista value) {
		vistaMap.put(key, value);
	}

	Vista get(String key) {
		return vistaMap.get(key);
	}

}
