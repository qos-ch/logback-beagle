package ch.qos.logback.beagle.net;

import java.io.BufferedInputStream;
import java.io.EOFException;
import java.io.ObjectInputStream;
import java.net.Socket;

import ch.qos.logback.beagle.Activator;
import ch.qos.logback.beagle.visual.ClassicTISBuffer;
import ch.qos.logback.classic.spi.LoggingEventVO;

/**
 * 
 * @author Ceki G&uuml;lc&uuml;
 * @author S&eacute;bastien Pennec
 * @author Christian Trutz
 */
public class LoggingEventSocketReader implements Runnable {

  private final ClassicTISBuffer classicTISBuffer;
  private final Socket socket;
  boolean started = true;
  
  /**
   * 
   * @param socket
   * @param loggingEvents
   */
  public LoggingEventSocketReader(Socket socket,
      ClassicTISBuffer classicTISBuffer) {
    this.classicTISBuffer = classicTISBuffer;
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
	classicTISBuffer.add(event);
      }
    } catch (EOFException eofException) {
      // nothing to do
    } catch (Exception exception) {
      Activator.INSTANCE.logException(exception);
    }
  }

  void stop() {
    started = false;
  }
  
}
