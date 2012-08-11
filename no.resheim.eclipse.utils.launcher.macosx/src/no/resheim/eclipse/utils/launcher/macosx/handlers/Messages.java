package no.resheim.eclipse.utils.launcher.macosx.handlers;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "no.resheim.eclipse.utils.launcher.macosx.handlers.messages"; //$NON-NLS-1$

	public static String OpenWorkspaceMenu_Open;

	public static String OpenWorkspaceMenu_Other;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
