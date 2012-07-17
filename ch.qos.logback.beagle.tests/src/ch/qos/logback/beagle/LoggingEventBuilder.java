/**
 * Logback-beagle: The logback Console Plugin for Eclipse 
 * Copyright (C) 2006-2012, QOS.ch. All rights reserved.
 *
 * This program and the accompanying materials are licensed under
 * the terms of the Eclipse Public License v1.0 as published by
 * the Eclipse Foundation.
 */
package ch.qos.logback.beagle;

import java.io.IOException;
import java.util.List;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;

import ch.qos.logback.classic.corpus.Corpus;
import ch.qos.logback.classic.corpus.CorpusModel;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.PubLoggingEventVO;


public class LoggingEventBuilder {

  static LoggerContext Logger_Context = new LoggerContext();
  static Logger LOGGER = Logger_Context.getLogger("xx");
  static String LONG_TEXT = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Vestibulum lectus augue, pulvinar quis cursus nec, imperdiet nec ante. Cras sit amet arcu et enim adipiscing pellentesque. Suspendisse mi felis, dictum a lobortis nec, placerat in diam. Proin lobortis tortor at nunc facilisis aliquet. Praesent eget dignissim orci. Ut iaculis bibendum.";
	
  int count = 0;

  CorpusModel corpusModel;
  ILoggingEvent[] internalBuffer = new ILoggingEvent[0];
  int lastServedIndex = 0;

  static int BUFFER_SIZE = 100;

  public LoggingEventBuilder() throws IOException {
    List<String> wordList = Corpus.getStandatdCorpusWordList();
    corpusModel = new CorpusModel(112340, wordList);
  }

  public ILoggingEvent buildLoggingEvent() {
    if (lastServedIndex >= internalBuffer.length) {
      internalBuffer = Corpus.make(corpusModel, BUFFER_SIZE, true);
      shape();
      lastServedIndex = 0;
    }
    return internalBuffer[lastServedIndex++];
  }

  private void shape() {
    for (int i = 0; i < internalBuffer.length; i++) {
      PubLoggingEventVO le = (PubLoggingEventVO) internalBuffer[i];
      if(i % 20 == 0) 
    	  le.message = LONG_TEXT;
    	  else
    	  le.message = "msg " + count;
      count++;
    }
  }

}
