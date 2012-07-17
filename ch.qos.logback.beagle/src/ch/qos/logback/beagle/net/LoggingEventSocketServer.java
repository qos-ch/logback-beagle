package ch.qos.logback.beagle.net;

import java.io.IOException;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import ch.qos.logback.beagle.Activator;
import ch.qos.logback.beagle.visual.ITableItemStubBuffer;
import ch.qos.logback.classic.spi.ILoggingEvent;

/**
 * 
 * @author Ceki G&uuml;lc&uuml;
 * @author S&eacute;bastien Pennec
 * @author Christian Trutz
 */
public class LoggingEventSocketServer implements Runnable, Listener {

  private ServerSocket serverSocket = null;

  private int port = 4321;

  private boolean stop = false;

  final ITableItemStubBuffer<ILoggingEvent> iTableItemStubBuffer;
  List<LoggingEventSocketReader> socketReaderList = new ArrayList<LoggingEventSocketReader>();

  public LoggingEventSocketServer(
      ITableItemStubBuffer<ILoggingEvent> classicTISBuffer) {
    this.iTableItemStubBuffer = classicTISBuffer;
  }

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
	EventConsumerThread eventConsumerThread = new EventConsumerThread(
	    iTableItemStubBuffer);
	eventConsumerThread.start();
	LoggingEventSocketReader lesr = new LoggingEventSocketReader(socket,
	    eventConsumerThread.getBlockingQueue());
	socketReaderList.add(lesr);
	new Thread(lesr).start();
      }
      serverSocket.close();
    } catch (BindException bindException) {
      Activator.INSTANCE.logException(bindException, "The port " + port
	  + " is already in use.");
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
   * Stop this thread as well as any child threads.
   */
  public void stop() {
    try {
      if (serverSocket != null)
	serverSocket.close();
      for (LoggingEventSocketReader lesr : socketReaderList) {
	lesr.stop();
      }
      stop = true;
    } catch (IOException e) {
    }
  }

  @Override
  public void handleEvent(Event event) {
    // System.out
    // .println("LoggingEventSocketServer dispose event occured. Stopping");
    stop();
  }

  /**
   * 
   * @param port
   */
  public void setPort(int port) {
    this.port = port;
  }

}
