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
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Sash;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;

import ch.qos.logback.beagle.util.SashUtil;
import ch.qos.logback.beagle.view.BeagleView;
import ch.qos.logback.beagle.view.TableMediator;

/**
 * Toggle logger tree.
 * 
 * @author Christian Trutz
 */
public class ToggleTreeActionDelegate implements IViewActionDelegate {

  private BeagleView beagleView = null;
  private int oldSashXCoordinate = 0;
  private IAction action = null;

  static private final boolean TREE_VISIBLE = true;

  @Override
  public void init(IViewPart view) {
    if (view instanceof BeagleView) {
      beagleView = (BeagleView) view;
      adaptToSashMoving(beagleView);
    }
  }

  void adaptToSashMoving(BeagleView beagleView) {
    TableMediator tableMediator = beagleView.getTableMediator();
    if (tableMediator != null) {
      Sash sash = tableMediator.getSash();
      sash.addControlListener(new ControlAdapter() {
        @Override
        public void controlMoved(ControlEvent event) {
          if (action != null)
            action.setChecked(TREE_VISIBLE);
        }
      });
    }
  }

  @Override
  public void run(IAction action) {
    this.action = action;
    if (beagleView != null) {
      Sash sash = getSash(beagleView);
      FormData formData = (FormData) sash.getLayoutData();
      if (action.isChecked()) {
        SashUtil.setXCoordinate(sash, formData, oldSashXCoordinate);
      } else {
        oldSashXCoordinate = formData.left.offset;
        SashUtil.setXCoordinate(sash, formData, 0);
      }
      boolean checked = action.isChecked();
      sash.getParent().layout(); // WARNING: .layout() will also call the
                                 // above listener, but here we must preserve
                                 // the checked status
      action.setChecked(checked);
    }
  }

  private Sash getSash(BeagleView beagleView2) {
    TableMediator tableMediator = beagleView.getTableMediator();
    return tableMediator.getSash();
  }

  @Override
  public void selectionChanged(IAction action, ISelection selection) {
    // nothing to do
  }

}
