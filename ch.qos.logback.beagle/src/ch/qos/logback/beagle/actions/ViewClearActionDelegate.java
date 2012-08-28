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
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;

import ch.qos.logback.beagle.view.TableMediator;
import ch.qos.logback.beagle.view.BeagleView;

public class ViewClearActionDelegate implements IViewActionDelegate {

  TableMediator tableMediator;
  
  @Override
  public void run(IAction action) {
    // TODO Auto-generated method stub
    //System.out.println("ViewClearActionDelegate.run "+action);
    tableMediator.classicTISBuffer.removeAll();
  }

  @Override
  public void selectionChanged(IAction action, ISelection selection) {
    //System.out.println("ViewClearActionDelegate.selectionChanged "+action+", selection:"+selection);

  }

  @Override
  public void init(IViewPart view) {
    //System.out.println("ViewClearActionDelegate.init "+view);
    BeagleView beagleView = (BeagleView) view;
    tableMediator = beagleView.getTableMediator();
  }

}
