package ch.qos.logback.beagle.vista;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.preference.IPreferenceStore;

import ch.qos.logback.beagle.Activator;
import ch.qos.logback.beagle.preferences.BeaglePreferencesPage;
import ch.qos.logback.classic.PatternLayout;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Context;
import ch.qos.logback.core.pattern.Converter;
import ch.qos.logback.core.pattern.ConverterUtil;
import ch.qos.logback.core.pattern.LiteralConverter;
import ch.qos.logback.core.pattern.parser.Node;
import ch.qos.logback.core.pattern.parser.Parser;
import ch.qos.logback.core.pattern.parser.ScanException;

public class ConverterFacade  {

  String pattern;
  private Converter<ILoggingEvent>  head;
  Context context;
  List<Converter<ILoggingEvent>> converterList = new ArrayList<Converter<ILoggingEvent>>();


  public void start() {
    String pattern = BeaglePreferencesPage.PATTERN_PREFERENCE_DEFAULT_VALUE;
    if (Activator.INSTANCE != null) {
      IPreferenceStore pStore = Activator.INSTANCE.getPreferenceStore();
      pattern = pStore.getString(BeaglePreferencesPage.PATTERN_PREFERENCE);
    }
    try {
      Parser<ILoggingEvent> p = new Parser<ILoggingEvent>(pattern);
      p.setContext(context);
      Node t = p.parse();
      head = p.compile(t, PatternLayout.defaultConverterMap);
      ch.qos.logback.beagle.util.ConverterUtil.setContextForConverters(
	  context, head);
      ConverterUtil.startConverters(head);
      fillConverterList();
    } catch (ScanException e) {
      Activator.INSTANCE.logException(e, e.getMessage());
    }
  }
  


  public List<Converter<ILoggingEvent>> getConverterList() {
    return converterList;
  }


  public int getColumnCount() {
    return converterList.size();
  }

  public void setConverterList(List<Converter<ILoggingEvent>> converterList) {
    this.converterList = converterList;
  }



  private void fillConverterList() {
    Converter<ILoggingEvent> c = head;
    while(c != null) {
      if(!(c instanceof LiteralConverter)) {
	converterList.add(c);
      }
      c = c.getNext();
    }
  }


  public String getPattern() {
    return pattern;
  }

  public void setPattern(String pattern) {
    this.pattern = pattern;
  }
  
  public Context getContext() {
    return context;
  }

  public void setContext(Context context) {
    this.context = context;
  }  
  
  String computeConverterName(Converter<ILoggingEvent> c) {
    String className = c.getClass().getSimpleName();
    int index = className.indexOf("Converter");
    if (index == -1) {
      return className;
    } else {
      return className.substring(0, index);
    }
  }
}
