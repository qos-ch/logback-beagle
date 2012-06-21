package ch.qos.logback.beagle.core.net;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
    private static final String BUNDLE_NAME = "ch.qos.logback.beagle.core.net.messages"; //$NON-NLS-1$
    public static String SimpleSocketServer_BindException;
    static {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages() {
    }
}
