package ch.qos.logback.beagle.core.net;

import java.io.IOException;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;

import org.eclipse.osgi.util.NLS;

import ch.qos.logback.beagle.core.Activator;

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
                new Thread(new LoggingEventSocketReader(socket)).start();
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

}
