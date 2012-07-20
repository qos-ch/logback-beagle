package ch.qos.logback.beagle.view.listener;

import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.nebula.widgets.grid.Grid;
import org.eclipse.nebula.widgets.grid.GridColumn;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;

import ch.qos.logback.beagle.Activator;
import ch.qos.logback.beagle.Constants;
import ch.qos.logback.beagle.util.TableUtil;

public class ColumnControlListener implements ControlListener {
  
  final String columnName;
  final Grid grid;
  int oldColumnWidth = -1;
  final boolean isColumnLast;
  
  public ColumnControlListener(Grid grid, String columnName, boolean isColumnLast) {
    this.columnName = columnName;
    this.isColumnLast = isColumnLast;
    this.grid = grid;
  }
  
  @Override
  public void controlMoved(ControlEvent e) {
  }

  @Override
  public void controlResized(ControlEvent e) {
    GridColumn column = (GridColumn) e.widget;
    //System.out.println("ColumnControlListener.controlResized column: "+columnName+", new width="+column.getWidth()+", old width"+oldColumnWidth);
    
    int newSize = column.getWidth();
    if(!isColumnLast && oldColumnWidth > newSize) {
      TableUtil.adjustWidthOfLastColumn(grid);
    } else {
      // force width of last column to all available space
      TableUtil.adjustWidthOfLastColumn(grid);
    }
    oldColumnWidth = newSize;
    saveColumnSize(newSize);
  }

  private void saveColumnSize(int width) {
    if(Activator.INSTANCE == null)
      return;
    IDialogSettings dialogSettings = Activator.INSTANCE.getDialogSettings();
    dialogSettings.put(Constants.COLUMN_SIZE_DIALOG_SETTINGS_PREFIX+columnName, width);
  }

}
