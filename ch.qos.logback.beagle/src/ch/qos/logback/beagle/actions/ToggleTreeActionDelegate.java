/**
 * Logback-beagle: The logback Console Plugin for Eclipse 
 * Copyright (C) 2006-2012, QOS.ch. All rights reserved.
 *
 * This program and the accompanying materials are licensed under
 * the terms of the Eclipse Public License v1.0 as published by
 * the Eclipse Foundation.
 */
package ch.qos.logback.beagle.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Sash;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;

import ch.qos.logback.beagle.Constants;
import ch.qos.logback.beagle.view.BeagleView;
import ch.qos.logback.beagle.view.TableMediator;

/**
 * Toggle logger tree.
 * 
 * @author Christian Trutz
 */
public class ToggleTreeActionDelegate implements IViewActionDelegate {

  private BeagleView beagleView = null;
  private FormAttachment oldLeft = null;
  private FormAttachment oldRight = null;
  private IAction action = null;

  @Override
  public void init(IViewPart view) {
    if (view instanceof BeagleView) {
      beagleView = (BeagleView) view;
      TableMediator tableMediator = beagleView.getTableMediator();
      if (tableMediator != null) {
          Sash sash = tableMediator.getSash();
          sash.addControlListener(new ControlAdapter() {
			@Override
			public void controlMoved(ControlEvent event) {
				if(action != null)
				  action.setChecked(true);
			}
		});
      }
    }
  }

  @Override
  public void run(IAction action) {
	this.action = action;
    if (beagleView != null) {
      TableMediator tableMediator = beagleView.getTableMediator();
      if (tableMediator != null) {
        Sash sash = tableMediator.getSash();
        FormData formData = (FormData) sash.getLayoutData();
        if(action.isChecked()) {
        	formData.left = oldLeft;
            formData.right = oldRight;
        }
        else {
            oldLeft = formData.left;
            oldRight = formData.right;
            formData.left = new FormAttachment(0, 0);
            formData.right = new FormAttachment(0, 0 + Constants.SASH_WIDTH);
        }
        boolean checked = action.isChecked();
        sash.getParent().layout(); // WARNING: .layout() will also call the above listener, but here we must preserve the checked status
        action.setChecked(checked);
      }
    }
  }

  @Override
  public void selectionChanged(IAction action, ISelection selection) {
    // nothing to do
  }

}
