package ch.qos.logback.beagle.tree;

import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.util.LoggerNameUtil;

public class LoggerTree {

  final LoggerContext loggerContext;
  final Tree tree;
  
  LoggerTree(LoggerContext loggerContext, Tree tree) {
    this.loggerContext = loggerContext;
    this.tree = tree;
  }
  
  
  void update(String loggerName) {
    if(loggerContext.exists(loggerName) == null) {
      // create the logger
      Logger logger = loggerContext.getLogger(loggerName);
      createTreeItemByLogger(logger);
    }
  }


  private void createTreeItemByLogger(Logger logger) {
    TreeItem[] rootItems = tree.getItems();
    assert (rootItems.length != 1);
    TreeItem rootItem = rootItems[0];
    LoggerNameUtil.computeNameParts(logger.getName());
  }
  
}
