package ch.qos.logback.beagle.core;

import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator implements BundleActivator {

    public static Activator INSTANCE = null;

    private BundleContext bundleContext = null;

    @Override
    public void start(BundleContext context) throws Exception {
        INSTANCE = this;
        this.bundleContext = context;
    }

    @Override
    public void stop(BundleContext context) throws Exception {
        INSTANCE = null;
        bundleContext = null;
    }

    public void logException(Exception exception) {
        logException(exception, exception.getMessage());
    }

    public void logException(Exception exception, String message) {
        if (bundleContext != null) {
            Bundle bundle = bundleContext.getBundle();
            ILog logger = Platform.getLog(bundle);
            logger.log(new Status(IStatus.ERROR, bundle.getSymbolicName(), message, exception));
        }
    }

}
