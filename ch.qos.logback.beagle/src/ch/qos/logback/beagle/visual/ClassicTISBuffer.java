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

  boolean scrollingEnabled = true;
  volatile boolean disposed = false;

  public Label diffCue;

  private int bufferSize;
  private int dropSize;

  public ClassicTISBuffer(ConverterFacade head, Grid table, int bufferSize) {
    this.grid = table;
    this.display = table.getDisplay();
    this.converterFacade = head;
    this.bufferSize = bufferSize;
    this.dropSize = computeDropSize(bufferSize);
  }

  public ConverterFacade getConverterFacade() {
    return converterFacade;
  }

  private int computeDropSize(int aBufferSize) {
    return Math.min(1024, aBufferSize / 10);
  }

  /**
   * Set the buffer size to new value. Ig the new buffer size is smaller then
   * the old size, the buffer will reach the smaller size in multiple steps.
   * 
   * @param size
   */
  public void setBufferSize(int size) {
    this.bufferSize = size;
    this.dropSize = computeDropSize(bufferSize);
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

  private int findIndexOfFirstCallerItem(int middleIndex) {
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

  private int findIndexOfLastCallerItem(int middleIndex) {
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

  public void removeNeighboringItems(int index) {
    int beginIndex = findIndexOfFirstCallerItem(index);
    int lastIndex = findIndexOfLastCallerItem(index);
    tisList.subList(beginIndex, lastIndex + 1).clear();
    grid.remove(beginIndex, lastIndex);
    assertSize();
    display.syncExec(new RefreshGridRunnable());
  }

  public void removeAll() {
    tisList.clear();
    grid.removeAll();
  }

  public void refreshGrid() {
    display.syncExec(new RefreshGridRunnable());
  }

  public void addAtIndex(ITableItemStub iTableItemStub, int index) {
    tisList.add(index, iTableItemStub);
    refreshGrid();
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

  private void addVisualElements(final List<ITableItemStub> newTISList) {
    if (disposed)
      return;

    for (ITableItemStub ve : newTISList)
      tisList.add(ve);

    display.syncExec(new AddTableItemRunnable(newTISList));
    contactIfTooBig();
  }

  private void contactIfTooBig() {
    if (tisList.size() >= bufferSize) {
      tisList.subList(0, dropSize).clear();
      display.syncExec(new AdjustGridPostContractionRunnable());
    }
  }

  public void rebuildEmptyGrid() {
    display.syncExec(new RebuildEmptyGridRunnable());
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

  public Grid getGrid() {
    return grid;
  }

  public boolean isScrollingEnabled() {
    return scrollingEnabled;
  }

  public void setSCrollingEnabled(boolean scrollingEnabled) {
    this.scrollingEnabled = scrollingEnabled;
  }

  private void addNewItemStubsToGrid(List<ITableItemStub> aTISList) {
    if (disposed || aTISList.size() == 0) {
      return;
    }

    GridItem lastGridItem = null;
    for (ITableItemStub tis : aTISList) {
      lastGridItem = new GridItem(grid, SWT.NONE);
      if (disposed)
	return;
      tis.populate(lastGridItem);
    }

    if (isScrollingEnabled()) {
      grid.showItem(lastGridItem);
    }
  }

  void assertSize() {
    if (grid.getItemCount() != tisList.size()) {
      System.out.println("size mismatch gridSize=" + grid.getItemCount()
	  + " tisListSize" + tisList.size());
    }
  }

  // ------------------------- RefreshGridRunnable class
  private final class RefreshGridRunnable implements Runnable {
    public void run() {
      grid.clearAll(true);
      grid.setItemCount(tisList.size());
    }
  }

  // ------------------------- AdjustGridPostContractionRunnable class
  private final class AdjustGridPostContractionRunnable implements Runnable {
    public void run() {
      int topIndex = grid.getTopIndex();
      grid.remove(0, dropSize - 1);
      assertSize();
      grid.setTopIndex(topIndex - dropSize);
    }
  }

  // ------------------------- AddTableItemRunnable class
  private final class AddTableItemRunnable implements Runnable {
    private final List<ITableItemStub> aTISList;

    private AddTableItemRunnable(List<ITableItemStub> aTISList) {
      this.aTISList = aTISList;
    }

    public void run() {
      addNewItemStubsToGrid(aTISList);
    }
  }

  // ------------------------- RebuildEmptyGridRunnable class
  private final class RebuildEmptyGridRunnable implements Runnable {
    public void run() {
      addNewItemStubsToGrid(ClassicTISBuffer.this.tisList);
    }
  }

}
