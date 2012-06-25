package ch.qos.logback.beagle.core.view;

import java.io.BufferedOutputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.part.ViewPart;

import ch.qos.logback.beagle.core.net.LoggingEventSocketServer;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.LoggingEvent;
import ch.qos.logback.classic.spi.LoggingEventVO;

/**
 * 
 * @author Christian Trutz
 */
public class BeagleView extends ViewPart {

    //
    private final LoggingEventSocketServer loggingEventSocketServer = new LoggingEventSocketServer();

    //
    private TableViewer beagleTableViewer = null;

    @Override
    public void createPartControl(Composite parent) {
        Composite root = new Composite(parent, SWT.NONE);
        root.setLayout(new FillLayout());

        // initialize table
        beagleTableViewer = new TableViewer(root, SWT.VIRTUAL);
        beagleTableViewer.setContentProvider(new ArrayContentProvider());
        beagleTableViewer.setLabelProvider(new LabelProvider());
        final Table beagleTable = beagleTableViewer.getTable();
        beagleTable.addListener(SWT.SetData, new Listener() {
            public void handleEvent(Event event) {
                TableItem item = (TableItem) event.item;
                int index = event.index;
                item.setText(loggingEventSocketServer.getLoggingEvent(index).getMessage());
            }
        });

        // initialize logging event socket server
        Thread loggingEventsThread = new Thread(loggingEventSocketServer);
        loggingEventSocketServer.addLoggingEventChangeListener(new Listener() {
            @Override
            public void handleEvent(final Event event) {
                beagleTable.getDisplay().asyncExec(new Runnable() {
                    @Override
                    public void run() {
                        beagleTable.setItemCount(loggingEventSocketServer.getLoggingEventsCount());
                        beagleTable.clear(event.index);
                    }
                });

            }
        });
        loggingEventsThread.setName("Logging Events");
        loggingEventsThread.start();

        // only for testing
        Thread test = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Socket socket = new Socket("localhost", 4321);
                    ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(socket.getOutputStream()));
                    for (int i = 1; i <= 1024 * 100; i++) {
                        LoggingEvent loggingEvent = new LoggingEvent();
                        loggingEvent.setLevel(Level.ERROR);
                        loggingEvent.getThreadName();
                        loggingEvent.setMessage("test " + i);
                        oos.writeObject(LoggingEventVO.build(loggingEvent));
                        oos.flush();
                    }
                    oos.close();
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }
        });
        test.start();
    }

    @Override
    public void setFocus() {
        if (beagleTableViewer != null)
            beagleTableViewer.getTable().setFocus();
    }

}
