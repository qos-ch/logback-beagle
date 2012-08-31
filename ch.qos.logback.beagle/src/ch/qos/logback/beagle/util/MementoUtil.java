package ch.qos.logback.beagle.util;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.layout.FormData;
import org.eclipse.ui.IMemento;

import ch.qos.logback.beagle.view.TableMediator;

public class MementoUtil {

  static final String SASH_X_MAP_KEY = "SASH_X";
  static final String SASH_MEMENTO_KEY = "SASH";
  static final String SASH_X_MEMENTO_KEY = "X";

  Map<String, Object> map = new HashMap<String, Object>();

  public static void save(IMemento memento, TableMediator tableMediator) {
    System.out.println("saving memento");
    FormData formData = (FormData) tableMediator.getSash().getLayoutData();
    IMemento sashChild = memento.createChild(SASH_MEMENTO_KEY);
    sashChild.putInteger(SASH_X_MEMENTO_KEY, formData.left.offset);

  }

  public void init(IMemento memento) {
    if (memento == null) {
      System.out.println("null memento");
      return;
    }
    System.out.println("processing from memento");
    
    IMemento sashChild = memento.getChild(SASH_MEMENTO_KEY);
    if (sashChild != null) {
      map.put(SASH_X_MAP_KEY, sashChild.getInteger(SASH_X_MEMENTO_KEY));
    }
  }

  /**
   * 
   * @return the saved X-coordinate for the sash. -1 if not found
   */
  public int getSashXCoordinate() {
    Integer x = (Integer) map.get(SASH_X_MAP_KEY);
    if (x == null)
      return -1;
    else
      return x;
  }

  public int getSashXCoordinate(int defaultVal) {
    int x = getSashXCoordinate();
    if (x == -1)
      return defaultVal;
    else
      return x;
  }

}
