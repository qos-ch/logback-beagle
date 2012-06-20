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
import ch.qos.logback.beagle.visual.CallerDataVisualElement;
import ch.qos.logback.beagle.visual.IVisualElement;
import ch.qos.logback.beagle.visual.LoggingEventVisualElement;
import ch.qos.logback.beagle.visual.ThrowableProxyVisualElement;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.IThrowableProxy;

public class VisualElementBuffer implements Listener, DisposeListener {

  static IVisualElement[] EMTPRY_VISUAL_ELEMENT_ARRAY = new IVisualElement[0];

  private boolean TAIL = true;

  
  
  List<IVisualElement> visualElementBuffer = Collections
      .synchronizedList(new ArrayList<IVisualElement>());
  int count = 0;
  Table table;
  Display display;

  boolean active = true;

  volatile boolean disposed = false;
  
  Label diffCue;
  Label jumpCue;

  VisualElementBuffer(Table table) {
    this.table = table;
    this.display = table.getDisplay();
  }

  public void handleEvent(Event event) {
    TableItem item = (TableItem) event.item;

    int index = event.index;
    // ignore out of bounds requests
    if (index >= visualElementBuffer.size()) {
      return;
    }
    IVisualElement ive = (IVisualElement) visualElementBuffer.get(index);
    item.setText(ive.getText());
    item.setBackground(ive.getBackgroundColor());
    item.setImage(ive.getImage());
  }

  // IVisualElement[]
  void loggingEventToVisualElement(List<IVisualElement> veList, ILoggingEvent e) {
    // List<IVisualElement> veList = new ArrayList<IVisualElement>();

    count++;
    Color c = null;
    if (count % 2 == 0) {
      c = ResourceUtil.GRAY;
    }
    veList.add(new LoggingEventVisualElement(e, c));

    IThrowableProxy tp = e.getThrowableProxy();

    while (tp != null) {
      IThrowableProxy itp = e.getThrowableProxy();
      veList.add(new ThrowableProxyVisualElement(itp,
          ThrowableProxyVisualElement.INDEX_FOR_INITIAL_LINE, c));
      int stackDepth = itp.getStackTraceElementProxyArray().length;
      for (int i = 0; i < stackDepth; i++) {
        veList.add(new ThrowableProxyVisualElement(itp, i, c));
      }
      if (itp.getCommonFrames() > 0) {
        veList.add(new ThrowableProxyVisualElement(itp, stackDepth, c));
      }
      tp = tp.getCause();
    }
    // return veList.toArray(EMTPRY_VISUAL_ELEMENT_ARRAY);
  }

  static int MAX_EXTENT = 20;

  int findBeginIndex(int middleIndex) {
    int limit = middleIndex - MAX_EXTENT >= 0 ? middleIndex - MAX_EXTENT : 0;
    int found = middleIndex;
    for (int i = middleIndex; i >= limit; i--) {
      if (visualElementBuffer.get(i) instanceof CallerDataVisualElement)
        found = i;
      else
        break;
    }
    return found;
  }

  int findLastIndex(int middleIndex) {
    int limit = middleIndex + MAX_EXTENT <= visualElementBuffer.size() ? middleIndex
        + MAX_EXTENT
        : visualElementBuffer.size();
    int found = middleIndex;
    for (int i = middleIndex; i < limit; i++) {
      if (visualElementBuffer.get(i) instanceof CallerDataVisualElement)
        found = i;
      else
        break;
    }
    return found;
  }

  public void removeNeighboringCallerDataVisualElements(int index) {
    int beginIndex = findBeginIndex(index);
    int lastIndex = findLastIndex(index);
    visualElementBuffer.subList(beginIndex, lastIndex + 1).clear();
    display.syncExec(new Runnable() {
      public void run() {
        // int topIndex = table.getTopIndex();
        table.clearAll();
        table.setItemCount(visualElementBuffer.size());
        // table.setTopIndex(topIndex - CLEAN_COUNT);
      }
    });
  }

  public void add(final CallerDataVisualElement cdVisualElement, int index) {
    visualElementBuffer.add(index, cdVisualElement);
    display.syncExec(new Runnable() {
      public void run() {
        // int topIndex = table.getTopIndex();
        table.clearAll();
        table.setItemCount(visualElementBuffer.size());
        // table.setTopIndex(topIndex - 1);
      }
    });
  }

  void add(final List<ILoggingEvent> loggingEventList) {
    List<IVisualElement> visualElementList = new ArrayList<IVisualElement>();
    for (ILoggingEvent iLoggingEvent : loggingEventList) {
      loggingEventToVisualElement(visualElementList, iLoggingEvent);
    }
    addVisualElementInChunks(visualElementList);
  }

  private final void setTableItem(TableItem tableItem, IVisualElement visualElement) {
    tableItem.setText(visualElement.getText());
    tableItem.setBackground(visualElement.getBackgroundColor());
    tableItem.setImage(visualElement.getImage());
  }

  void add(final ILoggingEvent iLoggingEvent) {
    List<IVisualElement> visualElementList = new ArrayList<IVisualElement>();
    loggingEventToVisualElement(visualElementList, iLoggingEvent);
    addVisualElementInChunks(visualElementList);
  }

  
  private void addVisualElementInChunks(final List<IVisualElement> veList) {
    
    int maxChunk = Constants.CLEAN_COUNT/5;
    //int maxChunk = 4;
    int rangeStart = 0;
    int size = veList.size();
    while(rangeStart < size) {
      int rangeEnd = rangeStart + maxChunk;
      if(rangeEnd > size) {
        rangeEnd = size;
      }
      addVisualElement(veList.subList(rangeStart, rangeEnd));
      rangeStart = rangeEnd;
    }
    
  }
  private void addVisualElement(final List<IVisualElement> veList) {
    if (disposed) {
      return;
    }

    for (IVisualElement ve : veList) {
      visualElementBuffer.add(ve);
    }

    display.syncExec(new Runnable() {
      public void run() {
        TableItem ti = null;
        if (disposed) {
          return;
        }

        for (IVisualElement visualElement : veList) {
          ti = new TableItem(table, SWT.NONE);
          if (disposed) {
            System.out.println("syncExec 1");
            return;
          }
          setTableItem(ti, visualElement);
        }
        if (TAIL && ti != null) {
          table.showItem(ti);
        }
      }
    });

    if (visualElementBuffer.size() >= Constants.MAX) {
      visualElementBuffer.subList(0, Constants.CLEAN_COUNT).clear();
      System.out.println("new buffer size= " + visualElementBuffer.size());
      display.syncExec(new Runnable() {
        public void run() {
          int topIndex = table.getTopIndex();
          table.clearAll();
          table.setItemCount(visualElementBuffer.size());
          table.setTopIndex(topIndex - Constants.CLEAN_COUNT);
        }
      });
    }
  }

  public boolean isActive() {
    return active;
  }

  public void setActive(boolean active) {
    this.active = active;
  }

  public int size() {
    return visualElementBuffer.size();
  }

  public void clearCues() {
    diffCue.setText("");
    jumpCue.setImage(null);
  }

  public IVisualElement get(int index) {
    if (index >= visualElementBuffer.size()) {
      System.out.println("EventBuffer.get invoked with index=" + index);
      return null;
    } else {
      return visualElementBuffer.get(index);
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

}
