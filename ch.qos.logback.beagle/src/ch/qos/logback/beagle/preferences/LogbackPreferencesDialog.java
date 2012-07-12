package ch.qos.logback.beagle.preferences;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import ch.qos.logback.beagle.Messages;

/**
 * A dialog that allows the user to change the pattern that is used to display
 * the logging events
 * 
 * @author S&eacute;bastien Pennec
 */
public class LogbackPreferencesDialog extends Dialog {

  private String pattern;
  private int serverPort;
  private int tmpServerPort = -1;
  private String fontName;
  private int fontSize;
  private int listMaxSize;

  private Text patternField;
  private Text portField;
  private Combo fontCombo;
  private Text fontSizeField;
  private Text maxSizeField;

  public LogbackPreferencesDialog(Shell parentShell, String namePattern, int serverPort, String fontName
      , int fontSize) {
    super(parentShell);
    this.pattern = namePattern;
    this.serverPort = serverPort;
    this.fontName = fontName;
    this.fontSize = fontSize;
    this.listMaxSize = 10000;

    setShellStyle(SWT.DIALOG_TRIM | SWT.RESIZE | SWT.APPLICATION_MODAL);
  }

  protected Control createDialogArea(Composite parent) {
    Composite container = (Composite) super.createDialogArea(parent);
    container.setSize(1000, 1000);
    final GridLayout gridLayout = new GridLayout();
    gridLayout.numColumns = 2;
    container.setLayout(gridLayout);

    createPatternUIParts(container);
    createServerPortUIParts(container);
    createFontUIParts(container);
    createListSizeUIParts(container);
    
    initContent();

    return container;
  }

  private void createPatternUIParts(Composite container) {
    final Label patternLabel = new Label(container, SWT.NONE);
    patternLabel.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false));
    patternLabel.setText(Messages.PreferencesDialogPatternLabel);

    patternField = new Text(container, SWT.BORDER);
    Rectangle bounds = patternField.getBounds();
    bounds.width = 150;
    patternField.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
  }

  private void createServerPortUIParts(Composite container) {

    final Label portLabel = new Label(container, SWT.NONE);
    portLabel.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false));
    portLabel.setText(Messages.PreferencesDialogPortLabel);

    portField = new Text(container, SWT.BORDER);
    portField.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
  }
  
  private void createFontUIParts(Composite container) {
    final Label fontLabel = new Label(container, SWT.None);
    fontLabel.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false));
    fontLabel.setText(Messages.PreferencesDialogFontNameLabel);
    
    fontCombo = new Combo(container, SWT.DROP_DOWN | SWT.READ_ONLY);
    
    FontData[] fdArray = Display.getDefault().getFontList(null, true);
    List<String> namesList = new ArrayList<String>();
    for (int i = 0; i < fdArray.length; i++) {
      String name = fdArray[i].getName();
      if (!namesList.contains(name)) {
        fontCombo.add(fdArray[i].getName());
        namesList.add(name);
      }
    }
    
    final Label sizeLabel = new Label(container, SWT.NONE);
    sizeLabel.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false));
    sizeLabel.setText(Messages.PreferencesDialogFontSizeLabel);

    fontSizeField = new Text(container, SWT.BORDER);
    fontSizeField.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
  }
  
  private void createListSizeUIParts(Composite container) {
    final Label sizeLabel = new Label(container, SWT.None);
    sizeLabel.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false));
    sizeLabel.setText(Messages.PreferencesDialogMaximumNumberOfLogs);
    
    maxSizeField = new Text(container, SWT.BORDER);
    maxSizeField.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
  }

  private void initContent() {
    patternField.setText(pattern != null ? pattern : ""); //$NON-NLS-1$
    patternField.addModifyListener(new ModifyListener() {
      public void modifyText(ModifyEvent e) {
        pattern = patternField.getText();
      }
    });

    portField.setText(String.valueOf(serverPort));
    portField.addModifyListener(new ModifyListener() {
      public void modifyText(ModifyEvent e) {
        tmpServerPort = Integer.parseInt(portField.getText());
      }
    });
    
    fontSizeField.setText(String.valueOf(fontSize));
    fontSizeField.addModifyListener(new ModifyListener() {
      public void modifyText(ModifyEvent e) {
        fontSize = Integer.parseInt(fontSizeField.getText());
      }
    });
    
    fontCombo.select(fontCombo.indexOf(fontName));
    fontCombo.addModifyListener(new ModifyListener() {
      public void modifyText(ModifyEvent e) {
        fontName = fontCombo.getItem(fontCombo.getSelectionIndex());
      }
    }); 
    
    maxSizeField.setText(String.valueOf(listMaxSize));
    maxSizeField.addModifyListener(new ModifyListener() {
      public void modifyText(ModifyEvent e) {
        listMaxSize = Integer.parseInt(maxSizeField.getText());
      }
    });
  }

  protected void configureShell(Shell newShell) {
    super.configureShell(newShell);
    newShell.setText(Messages.PreferencesDialogTitle);
  }

  public String getPattern() {
    return pattern;
  }

  public int getServerPort() {
    return serverPort;
  }

  public int getFontSize() {
    return fontSize;
  }
  
  public String getFontName() {
    return fontName;
  }
  
  public int getListMaxSize() {
    return listMaxSize;
  }

  @Override
  protected void okPressed() {
    if (checkServerPort()) {
      super.okPressed();
    }
  }

  private boolean checkServerPort() {
    if (tmpServerPort != -1 && !portInBounds()) {
      MessageDialog.openInformation(getShell(), Messages.PortOutOfRangeErrorDialogTitle, Messages.PortOutOfRangeError);
      portField.setText(String.valueOf(serverPort));
      return false;
    } else {
      if (tmpServerPort != -1) {
        serverPort = tmpServerPort;
      }
      return true;
    }
  }

  private boolean portInBounds() {
    if (tmpServerPort > 0 && tmpServerPort <= 65535) {
      return true;
    }
    return false;
  }
}
