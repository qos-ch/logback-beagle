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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.nebula.widgets.grid.Grid;
import org.eclipse.nebula.widgets.grid.GridItem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import ch.qos.logback.beagle.tree.LoggerTree;
import ch.qos.logback.beagle.util.ResourceUtil;
import ch.qos.logback.beagle.view.ConverterFacade;
import ch.qos.logback.beagle.view.TableMediator;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.IThrowableProxy;
import ch.qos.logback.core.helpers.CyclicBuffer;

public class ClassicTISBuffer implements ITableItemStubBuffer<ILoggingEvent>,
    Listener, DisposeListener {

  static ITableItemStub[] EMPTY_TIS_ARRAY = new ITableItemStub[0];

  List<ITableItemStub> tisList = Collections
      .synchronizedList(new ArrayList<ITableItemStub>());

  private int lineCount = 0;

  private TableMediator tableMediator;
  private final Grid grid;
  private final ConverterFacade converterFacade;

  private final Display display;
  private boolean scrollingEnabled = true;
  private volatile boolean disposed = false;

  private int bufferSize;
  private int minDropSize;
  private CyclicBuffer<ILoggingEvent> cyclicBuffer;
  private LoggerContext loggerContext;

  public ClassicTISBuffer(TableMediator tableMediator, int bufferSize) {
    this.tableMediator = tableMediator;
    this.grid = tableMediator.getGrid();
    this.display = grid.getDisplay();
    this.loggerContext = tableMediator.getLoggerContext();
    this.converterFacade = tableMediator.getConverterFacade();
    this.bufferSize = bufferSize;
    this.minDropSize = computeMinDropSize(bufferSize);
    this.cyclicBuffer = new CyclicBuffer<>(bufferSize);
  }

  public ConverterFacade getConverterFacade() {
    return converterFacade;
  }

  private int computeMinDropSize(int aBufferSize) {
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
    this.minDropSize = computeMinDropSize(bufferSize);
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
  private void loggingEventToVisualElement(List<ITableItemStub> aTISList,
      ILoggingEvent event) {
    lineCount++;
    Color c = null;
    if (lineCount % 2 == 0) {
      c = ResourceUtil.GRAY;
    }
    aTISList.add(new LoggingEventTIS(converterFacade, event, c));

    IThrowableProxy tp = event.getThrowableProxy();

    while (tp != null) {
      IThrowableProxy itp = event.getThrowableProxy();
      aTISList.add(new ThrowableProxyTIS(converterFacade, itp,
          ThrowableProxyTIS.INDEX_FOR_INITIAL_LINE, c));
      int stackDepth = itp.getStackTraceElementProxyArray().length;
      for (int i = 0; i < stackDepth; i++) {
        aTISList.add(new ThrowableProxyTIS(converterFacade, itp, i, c));
      }
      if (itp.getCommonFrames() > 0) {
        aTISList.add(new ThrowableProxyTIS(converterFacade, itp, stackDepth, c));
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

  private boolean filterEvent(ILoggingEvent event) {
    Logger logger = loggerContext.getLogger(event.getLoggerName());
    return logger.isEnabledFor(event.getLevel());
  }

  /**
   * This method is invoked by the producer to add events into this buffer/list.
   * 
   * @param loggingEventList
   */
  public void add(final List<ILoggingEvent> loggingEventList) {
    List<ITableItemStub> itemStubList = new ArrayList<ITableItemStub>();
    Set<String> loggerNames = new HashSet<String>();

    for (ILoggingEvent iLoggingEvent : loggingEventList) {
      cyclicBuffer.add(iLoggingEvent);
      loggerNames.add(iLoggingEvent.getLoggerName());
      if (filterEvent(iLoggingEvent)) {
        loggingEventToVisualElement(itemStubList, iLoggingEvent);
      }
    }
    addGridItemStubs(itemStubList, loggerNames);
  }

  public void add(final ILoggingEvent iLoggingEvent) {
    List<ITableItemStub> visualElementList = new ArrayList<ITableItemStub>();
    Set<String> loggerNames = new HashSet<String>();
    loggingEventToVisualElement(visualElementList, iLoggingEvent);
    loggerNames.add(iLoggingEvent.getLoggerName());
    addGridItemStubs(visualElementList, loggerNames);
  }

  private void addGridItemStubs(final List<ITableItemStub> newTISList,
      Set<String> loggerNames) {
    if (disposed)
      return;

    for (ITableItemStub ve : newTISList)
      tisList.add(ve);

    display.syncExec(new AddTableItemRunnable(newTISList, loggerNames));
    contactIfTooBig();
  }

  private void contactIfTooBig() {
    int extraneousElementCount = this.tisList.size() - bufferSize; 
    if (extraneousElementCount > 0) {
      int elementsToDrop = minDropSize+extraneousElementCount;
      this.tisList.subList(0, elementsToDrop).clear();
      display.syncExec(new AdjustGridPostContractionRunnable(elementsToDrop));
    }
  }

  public void rebuildEmptyGrid() {
    display.syncExec(new RebuildEmptyGridRunnable());
  }

  public int size() {
    return this.tisList.size();
  }

  public ITableItemStub get(int index) {
    if (index >= this.tisList.size()) {
      return null;
    } else {
      return this.tisList.get(index);
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

  public void setScrollingEnabled(boolean scrollingEnabled) {
    this.scrollingEnabled = scrollingEnabled;
  }

  // called by display.syncExec!!
  private void addNewItemStubsToGrid(List<ITableItemStub> newTableItemStubs) {
    if (disposed || newTableItemStubs.size() == 0) {
      return;
    }

    GridItem lastGridItem = null;
    for (ITableItemStub tis : newTableItemStubs) {
      lastGridItem = new GridItem(grid, SWT.NONE);
      if (disposed)
        return;
      tis.populate(lastGridItem);
    }

    tableMediator.setTotalEventsLabelText(this.tisList.size() + " events");

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

  public void handleChangeInFilters() {
    removeAll();
    for (ILoggingEvent iLoggingEvent : cyclicBuffer.asList()) {
      if (filterEvent(iLoggingEvent))
        loggingEventToVisualElement(tisList, iLoggingEvent);
    }
    rebuildEmptyGrid();
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
    final int elementsToDrop;
    public AdjustGridPostContractionRunnable(int elementsToDrop) {
      this.elementsToDrop = elementsToDrop;
    }

    public void run() {
      int topIndex = grid.getTopIndex();
      grid.remove(0, elementsToDrop - 1);
      assertSize();
      grid.setTopIndex(topIndex - elementsToDrop);
    }
  }

  // ------------------------- AddTableItemRunnable class
  private final class AddTableItemRunnable implements Runnable {
    private final List<ITableItemStub> aTISList;
    private final Set<String> loggerNames;

    private AddTableItemRunnable(List<ITableItemStub> aTISList,
        Set<String> loggerNames) {
      this.aTISList = aTISList;
      this.loggerNames = loggerNames;
    }

    public void run() {
      addNewItemStubsToGrid(aTISList);
      LoggerTree lt = tableMediator.getLoggerTree();
      for (String loggerName : loggerNames) {
        lt.update(loggerName);
      }
    }
  }

  // ------------------------- RebuildEmptyGridRunnable class
  private final class RebuildEmptyGridRunnable implements Runnable {
    public void run() {
      addNewItemStubsToGrid(ClassicTISBuffer.this.tisList);
    }
  }

}
