

Unfortunately, the current state of the build requires the developer
to manually add lib/logback-classic-1.0.6-tests.jar to beagle.test
plugin's build path. I was unable to spell the necessary incantation
to generate the equivalent info via maven's eclipse:eclipse plugin.


Just as importantly, FirstTest fails with the following message:

2012-07-10 12:27:34,792 [main] WARN  org.eclipse.swtbot.swt.finder.junit.SWTBotApplicationLauncherClassRunner - Could not capture screenshot
java.lang.IllegalStateException: Could not find a display
	at org.eclipse.swtbot.swt.finder.utils.SWTUtils.display(SWTUtils.java:250)
	at org.eclipse.swtbot.swt.finder.finders.UIThreadRunnable.syncExec(UIThreadRunnable.java:125)
	at org.eclipse.swtbot.swt.finder.utils.SWTUtils.captureScreenshot(SWTUtils.java:327)
	at org.eclipse.swtbot.swt.finder.junit.ScreenshotCaptureListener.captureScreenshot(ScreenshotCaptureListener.java:53)
	at org.eclipse.swtbot.swt.finder.junit.ScreenshotCaptureListener.captureScreenshot(ScreenshotCaptureListener.java:42)
	at org.eclipse.swtbot.swt.finder.junit.ScreenshotCaptureListener.testFailure(ScreenshotCaptureListener.java:34)
	at org.junit.runner.notification.RunNotifier$4.notifyListener(RunNotifier.java:100)
	at org.junit.runner.notification.RunNotifier$SafeNotifier.run(RunNotifier.java:41)
	at org.junit.runner.notification.RunNotifier.fireTestFailure(RunNotifier.java:97)
	at org.junit.internal.runners.model.EachTestNotifier.addFailure(EachTestNotifier.java:25)
	at org.junit.internal.runners.model.EachTestNotifier.addMultipleFailureException(EachTestNotifier.java:32)
	at org.junit.internal.runners.model.EachTestNotifier.addFailure(EachTestNotifier.java:23)
	at org.junit.runners.ParentRunner.run(ParentRunner.java:242)
	at org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner.run(SWTBotJunit4ClassRunner.java:54)
	at org.eclipse.jdt.internal.junit4.runner.JUnit4TestReference.run(JUnit4TestReference.java:50)
	at org.eclipse.jdt.internal.junit.runner.TestExecution.run(TestExecution.java:38)
	at org.eclipse.jdt.internal.junit.runner.RemoteTestRunner.runTests(RemoteTestRunner.java:467)
	at org.eclipse.jdt.internal.junit.runner.RemoteTestRunner.runTests(RemoteTestRunner.java:683)
	at org.eclipse.jdt.internal.junit.runner.RemoteTestRunner.run(RemoteTestRunner.java:390)
	at org.eclipse.jdt.internal.junit.runner.RemoteTestRunner.main(RemoteTestRunner.java:197)


