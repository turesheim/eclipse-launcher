/*******************************************************************************
 * Copyright (c) 2012-2015 Torkild U. Resheim and others
 *
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Torkild U. Resheim - initial API and implementation
 *******************************************************************************/
package net.resheim.eclipse.launcher.macosx.handlers;

import org.eclipse.osgi.util.NLS;

/**
 * @since 2.0
 */
public class Messages extends NLS {
	private static final String BUNDLE_NAME = "net.resheim.eclipse.launcher.macosx.handlers.messages"; //$NON-NLS-1$

	public static String OpenWorkspaceMenu_Advanced;

	public static String OpenWorkspaceMenu_LaunchFailedTitle;

	public static String OpenWorkspaceMenu_LaunchFailed;

	public static String OpenWorkspaceMenu_Launching;

	public static String OpenWorkspaceMenu_Open;

	public static String OpenWorkspaceMenu_Other;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
