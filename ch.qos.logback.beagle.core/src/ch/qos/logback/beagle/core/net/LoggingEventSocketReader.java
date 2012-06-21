package ch.qos.logback.beagle.core.net;

import java.io.BufferedInputStream;
import java.io.ObjectInputStream;
import java.net.Socket;

import ch.qos.logback.beagle.core.Activator;
import ch.qos.logback.classic.spi.LoggingEvent;

/**
 * 
 * @author Ceki G&uuml;lc&uuml;
 * @author S&eacute;bastien Pennec
 * @author Christian Trutz
 */
public class LoggingEventSocketReader implements Runnable {

    //
    private ObjectInputStream ois = null;

    //
    private boolean oisInError = false;

    public LoggingEventSocketReader(Socket socket) {
        try {
            ois = new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));
        } catch (Exception exception) {
            Activator.INSTANCE.logException(exception, exception.getMessage());
            oisInError = true;
        }
    }

    @Override
    public void run() {
        if (oisInError) {
            return;
        }
        try {
            while (true) {
                // read an event from the wire
                LoggingEvent event = (LoggingEvent) ois.readObject();
                // trick to keep the original thread name
                event.getThreadName();
                // add it to the manager's LoggingEvent list
                // TODO:
                // LoggingEventManager.getManager().addLoggingEvent(event);
            }
        } catch (Exception exception) {
            oisInError = true;
            Activator.INSTANCE.logException(exception);
        }
    }

}
