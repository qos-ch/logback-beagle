package ch.qos.logback.beagle.tests;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.net.SocketAppender;
import ch.qos.logback.core.status.OnConsoleStatusListener;

public class ConsolePluginClient {

  static public void main(String[] args) throws Exception {
    
    // Create a SocketAppender connected to hostname:port with a
    // reconnection delay of 10000 seconds.
    String hostname = "localhost";
    int port = 4321;
    SocketAppender socketAppender = new SocketAppender();
    socketAppender.setRemoteHost(hostname);
    socketAppender.setPort(port);
    socketAppender.setIncludeCallerData(true);
    socketAppender.setReconnectionDelay(10000);

    LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
    socketAppender.setContext(lc);

    lc.getStatusManager().add(new OnConsoleStatusListener());
    // SocketAppender options become active only after the execution
    // of the next statement.
    socketAppender.start();

    Logger logger = (Logger) LoggerFactory.getLogger(ConsolePluginClient.class);
    logger.addAppender(socketAppender);

    BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

    for (int i = 0; i < 100; i++) {
      if (i % 3 == 0) {
        logger.warn(i + " is divisible by 3");
        logger.error("this is an exception", new Exception("test"));
      } else {
        logger.warn("this is message number " + i);
      }
    }

    

    while (true) {
      System.out.println("Type a message to send to log server at " + hostname
          + ":" + port + ". Type 'q' to quit.");

      String s = reader.readLine();

      if (s.equals("q")) {
        break;
      } else {
        logger.debug(s);
      }
    }
  }
}