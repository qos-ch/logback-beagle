package ch.qos.logback.beagle.net;

import java.io.BufferedInputStream;
import java.io.EOFException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;

import ch.qos.logback.beagle.Activator;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.LoggingEventVO;

/**
 * 
 * @author Ceki G&uuml;lc&uuml;
 * @author S&eacute;bastien Pennec
 * @author Christian Trutz
 */
public class LoggingEventSocketReader implements Runnable {


  private final BlockingQueue<ILoggingEvent> blockingQueue;
  private final Socket socket;
  boolean started = true;
  
  /**
   * 
   * @param socket
   * @param loggingEvents
   */
  public LoggingEventSocketReader(Socket socket,
      BlockingQueue<ILoggingEvent> blockingQueue) {
    this.blockingQueue = blockingQueue;
    this.socket = socket;
  }

  @Override
  public void run() {
    try {
      ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(
	  socket.getInputStream()));
      while (started) {
	// read an event from the wire
	LoggingEventVO event = (LoggingEventVO) ois.readObject();
	// add event
	blockingQueue.put(event);
      }
    } catch (EOFException eofException) {
      // nothing to do
    } catch (Exception ex) {
      logException(ex);
    }
  }

  void stop() {
    started = false;
  }
  
  void logException(Throwable t) {
    if( Activator.INSTANCE != null) {
      Activator.INSTANCE.logException(t);
    }
  }
}
