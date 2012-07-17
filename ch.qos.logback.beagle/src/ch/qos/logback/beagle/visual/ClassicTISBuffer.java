/**
 * Logback-beagle: The logback Console Plugin for Eclipse 
 * Copyright (C) 2006-2012, QOS.ch. All rights reserved.
 *
 * This program and the accompanying materials are licensed under
 * either the terms of the Eclipse Public License v1.0 as published by
 * the Eclipse Foundation.
 */
package ch.qos.logback.beagle.visual;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.nebula.widgets.grid.Grid;
import org.eclipse.nebula.widgets.grid.GridItem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;

import ch.qos.logback.beagle.util.ResourceUtil;
import ch.qos.logback.beagle.vista.ConverterFacade;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.IThrowableProxy;

public class ClassicTISBuffer implements ITableItemStubBuffer<ILoggingEvent>,
    Listener, DisposeListener {

  static ITableItemStub[] EMPTY_TIS_ARRAY = new ITableItemStub[0];

  List<ITableItemStub> tisList = Collections
      .synchronizedList(new ArrayList<ITableItemStub>());

  int count = 0;

  public final Grid grid;
  private final ConverterFacade converterFacade;

  final Display display;

  boolean active = true;
  volatile boolean disposed = false;

  public Label diffCue;

  private int bufferSize;
  private int dropSize;

  public ClassicTISBuffer(ConverterFacade head, Grid table, int bufferSize) {
    this.grid = table;
    this.display = table.getDisplay();
    this.converterFacade = head;
    this.bufferSize = bufferSize;
    this.dropSize = bufferSize / 10;
  }

  public ConverterFacade getConverterFacade() {
    return converterFacade;
  }

  public void setBufferSize(int size) {
    this.bufferSize = size;
    this.dropSize = Math.min(1024, bufferSize / 10);
  }

  /**
   * This method is called when a tableItem needs to be refreshed.
   */
  public void handleEvent(Event event) {
    if (event.type != SWT.SetData) {
      throw new IllegalStateException("Unexpected event type " + event.type);
    }
    GridItem item = (GridItem) event.item;

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
   * @param tisList
   * @param event
   */
  private void loggingEventToVisualElement(List<ITableItemStub> tisList,
      ILoggingEvent event) {

    count++;
    Color c = null;
    if (count % 2 == 0) {
      c = ResourceUtil.GRAY;
    }
    tisList.add(new LoggingEventTIS(converterFacade, event, c));

    IThrowableProxy tp = event.getThrowableProxy();

    while (tp != null) {
      IThrowableProxy itp = event.getThrowableProxy();
      tisList.add(new ThrowableProxyTIS(converterFacade, itp,
	  ThrowableProxyTIS.INDEX_FOR_INITIAL_LINE, c));
      int stackDepth = itp.getStackTraceElementProxyArray().length;
      for (int i = 0; i < stackDepth; i++) {
	tisList.add(new ThrowableProxyTIS(converterFacade, itp, i, c));
      }
      if (itp.getCommonFrames() > 0) {
	tisList.add(new ThrowableProxyTIS(converterFacade, itp, stackDepth, c));
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
	+ MAX_EXTENT : tisList.size();
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
    display.syncExec(new ResetTableRunnable());
  }

  public void resetTable() {
    display.syncExec(new ResetTableRunnable());
  }

  public void add(final CallerDataTIS cdVisualElement, int index) {
    tisList.add(index, cdVisualElement);
    resetTable();
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
    addVisualElements(visualElementList);
  }

  public void add(final ILoggingEvent iLoggingEvent) {
    List<ITableItemStub> visualElementList = new ArrayList<ITableItemStub>();
    loggingEventToVisualElement(visualElementList, iLoggingEvent);
    addVisualElements(visualElementList);
  }

  private void addVisualElements(final List<ITableItemStub> veList) {
    if (disposed) {
      return;
    }

    for (ITableItemStub ve : veList) {
      tisList.add(ve);
    }

    display.syncExec(new AddTableItemRunnable(veList));
    contactIfTooBig();
  }

  private void contactIfTooBig() {
    if (tisList.size() >= bufferSize) {
      tisList.subList(0, dropSize).clear();
      display.syncExec(new ResetTablePostContractionRunnable());
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
  }

  public ITableItemStub get(int index) {
    if (index >= tisList.size()) {
      return null;
    } else {
      return tisList.get(index);
    }
  }

  @Override
  public void widgetDisposed(DisposeEvent e) {
    disposed = true;
  }

  public Grid getTable() {
    return grid;
  }

  // ------------------------- ResetTableRunnable class
  private final class ResetTableRunnable implements Runnable {
    public void run() {
      grid.clearAll(true);
      grid.setItemCount(tisList.size());
    }
  }

  // ------------------------- ResetTablePostContractionRunnable class
  private final class ResetTablePostContractionRunnable implements Runnable {
    public void run() {
      int topIndex = grid.getTopIndex();
      
      grid.remove(0, dropSize-1);
      
      if(grid.getItemCount() != tisList.size()) 
	System.out.println("size mismatch gridSize="+grid.getItemCount()+" tisListSize"+ tisList.size());
      //grid.clearAll(true);
      grid.setTopIndex(topIndex - dropSize);
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

      GridItem ti = null;
      for (ITableItemStub tis : tisList) {
	ti = new GridItem(grid, SWT.NONE);
	if (disposed) {
	  return;
	}
	tis.populate(ti);
      }

      if (isActive() && ti != null) {
	grid.showItem(ti);
      }
    }
  }
}
