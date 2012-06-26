/**
 * Logback-beagle: The logback Console Plugin for Eclipse 
 * Copyright (C) 2006-2012, QOS.ch. All rights reserved.
 *
 * This program and the accompanying materials are licensed under
 * either the terms of the Eclipse Public License v1.0 as published by
 * the Eclipse Foundation.
 */
package ch.qos.logback.beagle.visual;

import java.util.List;


/**
 * ITableItemStubBuffer is the interface used by event producers to add events
 * to the console.
 * 
 * @author ceki
 *
 * @param <E>
 */
public interface ITableItemStubBuffer<E> {
  void add(E event);
  void add(List<E> eventList);
} 
