/**
 * Logback-beagle: The logback Console Plugin for Eclipse 
 * Copyright (C) 2006-2012, QOS.ch. All rights reserved.
 *
 * This program and the accompanying materials are licensed under
 * either the terms of the Eclipse Public License v1.0 as published by
 * the Eclipse Foundation.
 */
package ch.qos.logback.beagle.views;

import java.io.BufferedOutputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

import ch.qos.logback.beagle.net.LoggingEventSocketServer;
import ch.qos.logback.beagle.util.ResourceUtil;
import ch.qos.logback.beagle.vista.TableMediator;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.LoggingEvent;
import ch.qos.logback.classic.spi.LoggingEventVO;

/**
 * This sample class demonstrates how to plug-in a new workbench view. The view
 * shows data obtained from the model. The sample creates a dummy model on the
 * fly, but a real implementation would connect to the model available either in
 * this or another plug-in (e.g. the workspace). The view is connected to the
 * model using a content provider.
 * <p>
 * The view uses a label provider to define how model objects should be
 * presented in the view. Each view can present the same model objects using
 * different labels and icons, if needed. Alternatively, a single label provider
 * can be shared between views in order to ensure that objects of the same type
 * are presented in the same way everywhere.
 * <p>
 */

public class BeagleView extends ViewPart {

    //
    TableMediator tableMediator;

    /**
     * 
     * @param parent
     */
    @Override
    public void createPartControl(Composite parent) {

        parent.setLayout(new FormLayout());

        // initialize Beagle table
        ResourceUtil.init(parent.getDisplay());
        tableMediator = new TableMediator(parent);

        // start Beagle socket server
        LoggingEventSocketServer loggingEventSocketServer = new LoggingEventSocketServer(
                tableMediator.classicTISBuffer);
        Thread serverThread = new Thread(loggingEventSocketServer);
        serverThread.start();

        // stop Beagle socket server, if user dispose the view
        parent.addListener(SWT.Dispose, loggingEventSocketServer);

        PlatformUI.getWorkbench().getHelpSystem()
                .setHelp(tableMediator.table, "ch.qos.logback.beagle.viewer");

        // only for testing
        Thread test = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Socket socket = new Socket("localhost", 4321);
                    ObjectOutputStream oos = new ObjectOutputStream(
                            new BufferedOutputStream(socket.getOutputStream()));
                    for (int i = 1; i <= 1024 * 100; i++) {
                        LoggingEvent loggingEvent = new LoggingEvent();
                        loggingEvent.setLevel(Level.ERROR);
                        loggingEvent.getThreadName();
                        loggingEvent.setMessage("test " + i);
                        loggingEvent.setLoggerName("org.eclipse.beagle");
                        oos.writeObject(LoggingEventVO.build(loggingEvent));
                        oos.flush();
                    }
                    oos.close();
                    socket.close();
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }
        });
        test.start();
        // only for testing

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setFocus() {
        tableMediator.table.setFocus();
    }
}
