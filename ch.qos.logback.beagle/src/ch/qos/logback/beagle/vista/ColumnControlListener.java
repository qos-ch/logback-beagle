package ch.qos.logback.beagle.vista;

import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.nebula.widgets.grid.GridColumn;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;

import ch.qos.logback.beagle.Activator;
import ch.qos.logback.beagle.Constants;

public class ColumnControlListener implements ControlListener {
  
  final String columnName;
  ColumnControlListener(String columnName) {
    this.columnName = columnName;
  }
  
  @Override
  public void controlMoved(ControlEvent e) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void controlResized(ControlEvent e) {
    GridColumn column = (GridColumn) e.widget;
    //System.out.println("ColumnControlListener new col width for "+columnName+" "+column.getWidth());
    saveColumnSize(column.getWidth());
  }

  private void saveColumnSize(int width) {
    if(Activator.INSTANCE == null)
      return;
    IDialogSettings dialogSettings = Activator.INSTANCE.getDialogSettings();
    dialogSettings.put(Constants.COLUMN_SIZE_DIALOG_SETTINGS_PREFIX+columnName, width);
  }

}
