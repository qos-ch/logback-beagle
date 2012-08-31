package ch.qos.logback.beagle.util;

import org.eclipse.jface.resource.FontRegistry;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.nebula.widgets.grid.Grid;
import org.eclipse.swt.graphics.Font;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.themes.ITheme;
import org.eclipse.ui.themes.IThemeManager;

import ch.qos.logback.beagle.Constants;

public class FontUtil {

  static public FontRegistry getFontRegistry() {
    IThemeManager themeManager = PlatformUI.getWorkbench().getThemeManager();
    ITheme currentTheme = themeManager.getCurrentTheme();
    final FontRegistry fontRegistry = currentTheme.getFontRegistry();
    return fontRegistry;
  }

  static public Font getBeagleFont() {
    FontRegistry fontRegistry = getFontRegistry();
    Font font = fontRegistry.get(Constants.FONT_ID);
    return font;
  }

  static public void listenAndAdaptToFontChanges(final Grid grid) {
    final FontRegistry fontRegistry = getFontRegistry();
    fontRegistry.addListener(new IPropertyChangeListener() {
      @Override
      public void propertyChange(PropertyChangeEvent event) {
        if (event.getProperty().equals(Constants.FONT_ID)) {
          Font font = fontRegistry.get(Constants.FONT_ID);
          grid.setFont(font);
        }
      }
    });

  }
}
