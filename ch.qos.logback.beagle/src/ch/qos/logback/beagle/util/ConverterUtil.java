package ch.qos.logback.beagle.util;

import ch.qos.logback.core.Context;
import ch.qos.logback.core.pattern.Converter;
import ch.qos.logback.core.spi.ContextAware;

public class ConverterUtil {

  
  static public void setContextForConverters(Context context, Converter<?> head) {
    Converter<?> c = head;
    while (c != null) {
      if (c instanceof ContextAware) {
        ((ContextAware) c).setContext(context);
      }
      c = c.getNext();
    }
  }
}
