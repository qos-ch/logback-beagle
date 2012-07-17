package ch.qos.logback.beagle.net;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import ch.qos.logback.beagle.visual.ITableItemStubBuffer;
import ch.qos.logback.classic.spi.ILoggingEvent;

public class EventConsumerThread extends Thread implements Listener {

  final BlockingQueue<ILoggingEvent> blockingQueue = new LinkedBlockingQueue<ILoggingEvent>();

  final static long MANDATORY_SLEEP = 500;
  
  ITableItemStubBuffer<ILoggingEvent> iTableItemStubBuffer;
  boolean disposed = false;

  public EventConsumerThread(ITableItemStubBuffer<ILoggingEvent> iTableItemStubBuffer) {
    this.iTableItemStubBuffer = iTableItemStubBuffer;
  }

  public BlockingQueue<ILoggingEvent> getBlockingQueue() {
    return blockingQueue;
  }

  @Override
  public void run() {

    while (!disposed) {
      try {
	Thread.sleep(MANDATORY_SLEEP);
	List<ILoggingEvent> newEventList = new ArrayList<ILoggingEvent>();
	blockingQueue.drainTo(newEventList);
	handleNewEvents(newEventList);
      } catch (InterruptedException e) {
	e.printStackTrace();
      }
    }

  }

  private void handleNewEvents(List<ILoggingEvent> newEventList) {
    iTableItemStubBuffer.add(newEventList);
  }

  public boolean isDisposed() {
    return disposed;
  }

  public void setDisposed(boolean disposed) {
    this.disposed = disposed;
  }

  @Override
  public void handleEvent(Event event) {
    disposed = true;
    System.out.println("dispose event occured");
  }
}
