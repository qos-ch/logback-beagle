/**
 * Logback-beagle: The logback Console Plugin for Eclipse 
 * Copyright (C) 2006-2012, QOS.ch. All rights reserved.
 *
 * This program and the accompanying materials are licensed under
 * either the terms of the Eclipse Public License v1.0 as published by
 * the Eclipse Foundation.
 */
package ch.qos.logback.beagle.vista;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

import ch.qos.logback.beagle.Constants;
import ch.qos.logback.beagle.util.ResourceUtil;
import ch.qos.logback.beagle.visual.CallerDataTIS;
import ch.qos.logback.beagle.visual.ITableItemStub;
import ch.qos.logback.beagle.visual.ITableItemStubBuffer;
import ch.qos.logback.beagle.visual.LoggingEventTIS;
import ch.qos.logback.beagle.visual.ThrowableProxyTIS;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.IThrowableProxy;

public class ClassicTISBuffer implements
    ITableItemStubBuffer<ILoggingEvent>, Listener, DisposeListener {

  static ITableItemStub[] EMPTY_TIS_ARRAY = new ITableItemStub[0];

  List<ITableItemStub> tisList = Collections
      .synchronizedList(new ArrayList<ITableItemStub>());
  int count = 0;
  Table table;
  Display display;

  boolean active = true;

  volatile boolean disposed = false;

  Label diffCue;
  Label jumpCue;

  ClassicTISBuffer(Table table) {
    this.table = table;
    this.display = table.getDisplay();
  }

  /**
   * This method is called when a tableItem needs to be refreshed.
   */
  public void handleEvent(Event event) {
    if(event.type != SWT.SetData) {
      throw new IllegalStateException("Unexpected event type "+event.type);
    }
    TableItem item = (TableItem) event.item;

    int index = event.index;
    // ignore out of bounds requests
    if (index >= tisList.size()) {
      return;
    }
    ITableItemStub tis = tisList.get(index);
    tis.populate(item);
}

  /**
   * Convert an ILoggingEvent to a list of IVisualElement.
   * 
   * @param veList
   * @param event
   */
  private void loggingEventToVisualElement(List<ITableItemStub> veList,
      ILoggingEvent event) {

    count++;
    Color c = null;
    if (count % 2 == 0) {
      c = ResourceUtil.GRAY;
    }
    veList.add(new LoggingEventTIS(event, c));

    IThrowableProxy tp = event.getThrowableProxy();

    while (tp != null) {
      IThrowableProxy itp = event.getThrowableProxy();
      veList.add(new ThrowableProxyTIS(itp,
	  ThrowableProxyTIS.INDEX_FOR_INITIAL_LINE, c));
      int stackDepth = itp.getStackTraceElementProxyArray().length;
      for (int i = 0; i < stackDepth; i++) {
	veList.add(new ThrowableProxyTIS(itp, i, c));
      }
      if (itp.getCommonFrames() > 0) {
	veList.add(new ThrowableProxyTIS(itp, stackDepth, c));
      }
      tp = tp.getCause();
    }
  }

  static int MAX_EXTENT = 20;

  int findBeginIndex(int middleIndex) {
    int limit = middleIndex - MAX_EXTENT >= 0 ? middleIndex - MAX_EXTENT : 0;
    int found = middleIndex;
    for (int i = middleIndex; i >= limit; i--) {
      if (tisList.get(i) instanceof CallerDataTIS)
	found = i;
      else
	break;
    }
    return found;
  }

  int findLastIndex(int middleIndex) {
    int limit = middleIndex + MAX_EXTENT <= tisList.size() ? middleIndex
	+ MAX_EXTENT
	: tisList.size();
    int found = middleIndex;
    for (int i = middleIndex; i < limit; i++) {
      if (tisList.get(i) instanceof CallerDataTIS)
	found = i;
      else
	break;
    }
    return found;
  }

  public void removeNeighboringCallerDataVisualElements(int index) {
    int beginIndex = findBeginIndex(index);
    int lastIndex = findLastIndex(index);
    tisList.subList(beginIndex, lastIndex + 1).clear();
    display.syncExec(new Runnable() {
      public void run() {
	// int topIndex = table.getTopIndex();
	table.clearAll();
	table.setItemCount(tisList.size());
	// table.setTopIndex(topIndex - CLEAN_COUNT);
      }
    });
  }

  public void add(final CallerDataTIS cdVisualElement, int index) {
    tisList.add(index, cdVisualElement);
    display.syncExec(new Runnable() {
      public void run() {
	// int topIndex = table.getTopIndex();
	table.clearAll();
	table.setItemCount(tisList.size());
	// table.setTopIndex(topIndex - 1);
      }
    });
  }

  /**
   * This method is invoked by the producer to add events into this buffer/list.
   * 
   * @param loggingEventList
   */
  public void add(final List<ILoggingEvent> loggingEventList) {
    List<ITableItemStub> visualElementList = new ArrayList<ITableItemStub>();
    for (ILoggingEvent iLoggingEvent : loggingEventList) {
      loggingEventToVisualElement(visualElementList, iLoggingEvent);
    }
    addVisualElementInChunks(visualElementList);
  }


  public void add(final ILoggingEvent iLoggingEvent) {
    List<ITableItemStub> visualElementList = new ArrayList<ITableItemStub>();
    loggingEventToVisualElement(visualElementList, iLoggingEvent);
    addVisualElementInChunks(visualElementList);
  }

  private void addVisualElementInChunks(final List<ITableItemStub> veList) {
    int maxChunk = Constants.CLEAN_COUNT / 5;
    // int maxChunk = 4;
    int rangeStart = 0;
    int size = veList.size();
    while (rangeStart < size) {
      int rangeEnd = rangeStart + maxChunk;
      if (rangeEnd > size) {
	rangeEnd = size;
      }
      addVisualElement(veList.subList(rangeStart, rangeEnd));
      rangeStart = rangeEnd;
    }
  }

  private void addVisualElement(final List<ITableItemStub> veList) {
    if (disposed) {
      return;
    }

    for (ITableItemStub ve : veList) {
      tisList.add(ve);
    }

    display.syncExec(new AddTableItemRunnable(veList));

    if (tisList.size() >= Constants.MAX) {
      tisList.subList(0, Constants.CLEAN_COUNT).clear();
      display.syncExec(new ResetTableRunnable());
    }
  }

  public boolean isActive() {
    return active;
  }

  public void setActive(boolean active) {
    this.active = active;
  }

  public int size() {
    return tisList.size();
  }

  public void clearCues() {
    diffCue.setText("");
    jumpCue.setImage(null);
  }

  public ITableItemStub get(int index) {
    if (index >= tisList.size()) {
      System.out.println("EventBuffer.get invoked with index=" + index);
      return null;
    } else {
      return tisList.get(index);
    }
  }

  @Override
  public void widgetDisposed(DisposeEvent e) {
    disposed = true;
    System.out.println("***widgetDisposed called");
  }

  public Table getTable() {
    return table;
  }

  
  // ------------------------- ResetTableRunnable class
  private final class ResetTableRunnable implements Runnable {
    public void run() {
      int topIndex = table.getTopIndex();
      table.clearAll();
      table.setItemCount(tisList.size());
      table.setTopIndex(topIndex - Constants.CLEAN_COUNT);
    }
  }


  // ------------------------- AddTableItemRunnable class
  private final class AddTableItemRunnable implements Runnable {
    private final List<ITableItemStub> tisList;

    private AddTableItemRunnable(List<ITableItemStub> tisList) {
      this.tisList = tisList;
    }

    public void run() {
      if (disposed) {
	return;
      }

      TableItem ti = null;
      for (ITableItemStub tis : tisList) {
	ti = new TableItem(table, SWT.NONE);
	if (disposed) {
	  return;
	}
	tis.populate(ti);
      }

      if (isActive() && ti != null) {
	table.showItem(ti);
      }
    }
  }
}
