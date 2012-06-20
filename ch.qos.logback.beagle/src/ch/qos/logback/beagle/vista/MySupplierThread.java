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

import ch.qos.logback.beagle.Constants;
import ch.qos.logback.beagle.LoggingEventBuilder;
import ch.qos.logback.classic.spi.ILoggingEvent;

class MySupplierThread extends Thread implements Listener {

  static int MAX = Constants.MAX + 10;
  static int CLEAN_COUNT = Constants.CLEAN_COUNT;

  static int COUNT = 0;
  UnfreezeToolItemListener fbListener;
  VisualElementBuffer tableEventBuffer;

  int lastAdd = 0;

  int nextTransferredIndex = -1;
  int nextIndex = -0;

  List<ILoggingEvent> internalList = new ArrayList<ILoggingEvent>();

  boolean disposed = false;

  MySupplierThread(VisualElementBuffer tableEventBuffer,
      UnfreezeToolItemListener fbListener) {
    this.tableEventBuffer = tableEventBuffer;
    this.fbListener = fbListener;
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
        Thread.sleep(10);
      } catch (InterruptedException e1) {
      }
      int limit = 2;
      if (COUNT < MAX) {
        limit = MAX;
      }
      for (int i = 0; i < limit; i++) {
        ILoggingEvent le = null;
        // if ((i % 10) != 0) {
        le = leb.getLoggingEvent();
        // } else {
        // le = LoggingEventBuilder.buildLoggingEventWithEx("message "
        // + COUNT);
        // }
        internalAdd(le);
        COUNT++;
      }
      externalSync();
    }
    System.out.println("exiting MySupplierThread");
  }

  void internalAdd(ILoggingEvent le) {
    internalList.add(le);
    nextIndex++;

  }

  void externalSync() {

    if (internalList.size() >= MAX) {
      internalList.subList(0, CLEAN_COUNT).clear();
    }
    int size = internalList.size();

    if (tableEventBuffer.isActive()) {
      int firstAvailableIndex = nextIndex - size;
      int targetedBeginIndex = Math.max(firstAvailableIndex,
          nextTransferredIndex);
      int fromIndex = size - (nextIndex - targetedBeginIndex);

      tableEventBuffer.add(internalList.subList(fromIndex, size));
      nextTransferredIndex = nextIndex;
    }
  }

  @Override
  public void handleEvent(Event event) {
    disposed = true;
    System.out.println("dispose event occured");
  }

}