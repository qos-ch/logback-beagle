package ch.qos.logback.beagle;

import org.eclipse.osgi.util.NLS;

/**
 * Message bundle accessor class.
 * <p>
 * See <a href="http://dev.eclipse.org/viewcvs/index.cgi/%7Echeckout%7E/platform-core-home/documents/3.1/message_bundles.html">
 * Message Bundles</a>
 * <p>
 * 
 * @author Sebastian Davids
 * @see org.eclipse.osgi.util.NLS
 */
public class Messages {
  static {
    NLS.initializeMessages(Messages.MESSAGE_BUNDLE, Messages.class);
  }

  /** The name of the message bundle used by the plug-in. */
  public static final String MESSAGE_BUNDLE = "ch.qos.logback.messages";//$NON-NLS-1$

  public static String BindException;

  public static String CaughtEOFException;

  public static String CaughtIOException;

  public static String ClearConsoleActionName;

  public static String ClearConsoleActionTooltip;

  public static String ClosingConnection;

  public static String CouldNotCloseConnection;

  public static String CouldNotStartServerError;

  public static String CouldNotStartServerErrorDialogTitle;

  public static String DebugFilterExpression;

  public static String DebugFilterName;

  public static String Delete;

  public static String Down;

  public static String EvaluatorFilterEvalName;

  public static String EvaluatorFilterName;

  public static String EventFilterEvalName;

  public static String EventFilterName;

  public static String ExpressionIncorrectError;

  public static String FilterDialogActionOnFilterMatchLabel;
  
  public static String FilterDialogActionOnFilterMismatchLabel;

  public static String FilterDialogExpression;

  public static String FilterDialogExpressionFieldDefaultMessage;

  public static String FilterDialogInfoLabel;

  public static String FilterDialogInfoLabel2;

  public static String FilterDialogOnMatch;

  public static String FilterDialogOnMatchFieldDefaultMessage;

  public static String FilterDialogOnMismatch;

  public static String FilterDialogOnMismatchFieldDefaultMessage;

  public static String FilterDialogTitle;

  public static String IncorrectExpressionErrorDialogTitle;

  public static String LogbackFilterActionName;

  public static String LogbackPreferencesActionName;

  public static String LoggingEventLabelContext;

  public static String LogMessageCouldNotUseFont;

  public static String LogMessageListeningOnPort;

  public static String New;

  public static String OpenStacktraceActionName;

  public static String PortOutOfRangeError;

  public static String PortOutOfRangeErrorDialogTitle;

  public static String PreferencesDialogFontNameLabel;

  public static String PreferencesDialogFontSizeLabel;

  public static String PreferencesDialogMaximumNumberOfLogs;

  public static String PreferencesDialogPatternLabel;

  public static String PreferencesDialogPortLabel;

  public static String PreferencesDialogTitle;

  public static String RequiredLogBackVersion;

  public static String Save;

  public static String ScrollLockOffActionName;

  public static String ScrollLockOffActionTooltip;

  public static String ScrollLockOnActionName;

  public static String ScrollLockOnActionTooltip;

  public static String UnexpectedException;

  public static String UnexpectedExceptionWithArg;

  public static String Up;

  public static String VersionError;

}
