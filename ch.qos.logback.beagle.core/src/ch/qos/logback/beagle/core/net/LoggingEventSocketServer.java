package ch.qos.logback.beagle.core.net;

import java.io.IOException;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Listener;

import ch.qos.logback.beagle.core.Activator;
import ch.qos.logback.classic.spi.ILoggingEvent;

/**
 * 
 * @author Ceki G&uuml;lc&uuml;
 * @author S&eacute;bastien Pennec
 * @author Christian Trutz
 */
public class LoggingEventSocketServer implements Runnable {

    //
    private ServerSocket serverSocket = null;

    //
    private int port = 4321;

    //
    private boolean stop = false;

    //
    private final List<ILoggingEvent> loggingEvents = Collections.synchronizedList(new LinkedList<ILoggingEvent>());

    private Listener listener = null;

    /**
     * 
     */
    @Override
    public void run() {
        startAndLoop();
    }

    /*
     * 
     */
    private void startAndLoop() {
        try {
            stop = false;
            serverSocket = new ServerSocket(port);
            while (!stop) {
                Socket socket = serverSocket.accept();
                new Thread(new LoggingEventSocketReader(socket, loggingEvents, listener)).start();
            }
            serverSocket.close();
        } catch (BindException bindException) {
            Activator.INSTANCE.logException(bindException, NLS.bind(Messages.SimpleSocketServer_BindException, port));
        } catch (Exception exception) {
            Activator.INSTANCE.logException(exception, exception.getMessage());
        }
    }

    /**
     * 
     */
    public void restart() {
        stop();
        startAndLoop();
    }

    /**
     * 
     */
    public void stop() {
        try {
            if (serverSocket != null)
                serverSocket.close();
            stop = true;
        } catch (IOException e) {
        }
    }

    /**
     * 
     * @param port
     */
    public void setPort(int port) {
        this.port = port;
    }

    /**
     * 
     * @param index
     * @return
     */
    public ILoggingEvent getLoggingEvent(int index) {
        return loggingEvents.get(index);
    }

    /**
     * 
     * @return
     */
    public int getLoggingEventsCount() {
        return loggingEvents.size();
    }

    /**
     * 
     * @param listener
     */
    public void addLoggingEventChangeListener(Listener listener) {
        this.listener = listener;
    }
}
