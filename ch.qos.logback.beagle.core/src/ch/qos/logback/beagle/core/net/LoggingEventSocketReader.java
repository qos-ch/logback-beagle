package ch.qos.logback.beagle.core.net;

import java.io.BufferedInputStream;
import java.io.EOFException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.List;

import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import ch.qos.logback.beagle.core.Activator;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.LoggingEventVO;

/**
 * 
 * @author Ceki G&uuml;lc&uuml;
 * @author S&eacute;bastien Pennec
 * @author Christian Trutz
 */
public class LoggingEventSocketReader implements Runnable {

    //
    private final List<ILoggingEvent> loggingEvents;

    //
    private final Listener listener;

    //
    private final Socket socket;

    /**
     * 
     * @param socket
     * @param loggingEvents
     * @param listener
     */
    public LoggingEventSocketReader(Socket socket, List<ILoggingEvent> loggingEvents, Listener listener) {
        this.loggingEvents = loggingEvents;
        this.listener = listener;
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));
            while (true) {
                // read an event from the wire
                LoggingEventVO event = (LoggingEventVO) ois.readObject();
                // add event
                loggingEvents.add(event);
                Event swtEvent = new Event();
                swtEvent.index = loggingEvents.indexOf(event);
                listener.handleEvent(swtEvent);
            }
        } catch (EOFException eofException) {
            // nothing to do
        } catch (Exception exception) {
            Activator.INSTANCE.logException(exception);
        }
    }

}
