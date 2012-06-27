/**
 * Logback-beagle: The logback Console Plugin for Eclipse 
 * Copyright (C) 2006-2012, QOS.ch. All rights reserved.
 *
 * This program and the accompanying materials are licensed under
 * either the terms of the Eclipse Public License v1.0 as published by
 * the Eclipse Foundation.
 */
package ch.qos.logback.beagle.vista;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import ch.qos.logback.beagle.LoggingEventBuilder;
import ch.qos.logback.beagle.visual.ITableItemStubBuffer;
import ch.qos.logback.classic.spi.ILoggingEvent;

class MySupplierThread extends Thread implements Listener {

  ITableItemStubBuffer<ILoggingEvent> tisBuffer;
  List<ILoggingEvent> internalList = new ArrayList<ILoggingEvent>();
  boolean disposed = false;

  MySupplierThread(ITableItemStubBuffer<ILoggingEvent> tisBuffer) {
    this.tisBuffer = tisBuffer;
  }

  public void run() {

    LoggingEventBuilder leb = null;
    try {
      leb = new LoggingEventBuilder();
    } catch (IOException e) {
      e.printStackTrace();
    }
    while (!disposed) {
      try {
	Thread.sleep(100);
      } catch (InterruptedException e1) {
      }
      // bursts of 2
      int burstSize = 2;
      for (int i = 0; i < burstSize; i++) {
	ILoggingEvent le = null;
	le = leb.buildLoggingEvent();
	internalList.add(le);
      }
      externalSync();
    }
    System.out.println("exiting MySupplierThread");
  }

  void externalSync() {
    tisBuffer.add(internalList);
    internalList.clear();
  }

  @Override
  public void handleEvent(Event event) {
    disposed = true;
    System.out.println("dispose event occured");
  }

}