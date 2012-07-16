/**
 * Logback-beagle: The logback Console Plugin for Eclipse 
 * Copyright (C) 2006-2012, QOS.ch. All rights reserved.
 *
 * This program and the accompanying materials are licensed under
 * either the terms of the Eclipse Public License v1.0 as published by
 * the Eclipse Foundation.
 */
package ch.qos.logback.beagle.views;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

import ch.qos.logback.beagle.net.LoggingEventSocketServer;
import ch.qos.logback.beagle.visual.ITableItemStubBuffer;
import ch.qos.logback.classic.spi.ILoggingEvent;

/**
 * 
 * @author Christian Trutz
 * 
 */
public class BeagleView extends ViewPart {

  private TableViewer gridTableViewer = null;

  /**
   * 
   * @param parent
   */
  @Override
  public void createPartControl(Composite parent) {

    parent.setLayout(new FillLayout());

    final List<ILoggingEvent> loggingEventBuffer = new LinkedList<ILoggingEvent>();

    gridTableViewer = new TableViewer(parent);
    gridTableViewer.setLabelProvider(new BeagleLabelProvider());
    gridTableViewer.setContentProvider(new ArrayContentProvider());
    gridTableViewer.setInput(loggingEventBuffer);

    // start Beagle socket server
    LoggingEventSocketServer loggingEventSocketServer = new LoggingEventSocketServer(
        new ITableItemStubBuffer<ILoggingEvent>() {

          @Override
          public void add(List<ILoggingEvent> eventList) {
            loggingEventBuffer.addAll(eventList);
            gridTableViewer.getControl().getDisplay().asyncExec(new Runnable() {

              @Override
              public void run() {
                gridTableViewer.setInput(loggingEventBuffer);
              }
            });
          }

          @Override
          public void add(ILoggingEvent event) {
            LinkedList<ILoggingEvent> tmpList = new LinkedList<ILoggingEvent>();
            tmpList.add(event);
            this.add(tmpList);
          }
        });
    Thread serverThread = new Thread(loggingEventSocketServer);
    serverThread.start();

    // stop Beagle socket server, if user dispose the view
    parent.addListener(SWT.Dispose, loggingEventSocketServer);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setFocus() {
    if (gridTableViewer != null)
      gridTableViewer.getControl().setFocus();
  }

}
