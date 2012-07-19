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
import java.util.concurrent.BlockingQueue;

import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import ch.qos.logback.beagle.LoggingEventBuilder;
import ch.qos.logback.classic.spi.ILoggingEvent;

public class MySupplierThread extends Thread implements Listener {

	final BlockingQueue<ILoggingEvent> blockingQueue;
	boolean disposed = false;
	static int LIMIT = 1000;
	
	public MySupplierThread(BlockingQueue<ILoggingEvent> blockingQueue) {
		this.blockingQueue = blockingQueue;
	}

	public void run() {

		LoggingEventBuilder leb = null;
		try {
			leb = new LoggingEventBuilder();
		} catch (IOException e) {
			e.printStackTrace();
		}
		int count = 0;
		while (!disposed && count++ < LIMIT) {
			try {
				Thread.sleep(1);
			} catch (InterruptedException e1) {
			}
			// bursts of 2
			int burstSize = 2;
			for (int i = 0; i < burstSize; i++) {
				ILoggingEvent le = null;
				le = leb.buildLoggingEvent();
				try {
					blockingQueue.put(le);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		System.out.println("exiting MySupplierThread");
	}

	@Override
	public void handleEvent(Event event) {
		disposed = true;
		System.out.println("dispose event occured");
	}
}
