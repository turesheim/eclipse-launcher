/*******************************************************************************
 * Copyright (c) 2012-2015  Torkild U. Resheim and others
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

import java.io.File;
import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.ui.statushandlers.StatusManager;

import net.resheim.eclipse.launcher.core.LauncherPlugin;

/**
 * A command handler that will open up a new Eclipse instance using the same
 * launcher used to opening the current instance. Virtual machine arguments used
 * to start the original instance will be passed along. This code is specific to
 * OS X.
 *
 * @author Torkild U. Resheim
 * @since 2.0
 */
public class OpenWorkspaceHandler extends AbstractHandler {

	private static final String ECLIPSE_LAUNCHER = "eclipse.launcher"; //$NON-NLS-1$

	private static final String PROP_VMARGS = "eclipse.vmargs"; //$NON-NLS-1$

	private static final String PROP_VM = "eclipse.vm"; //$NON-NLS-1$

	private static final String PROP_COMMANDS = "eclipse.commands"; //$NON-NLS-1$

	/** Command parameter for the workspace path */
	static final String WORKSPACE_PARAMETER_ID = "no.resheim.eclipse.launcherutil.workspace"; //$NON-NLS-1$

	/** Command parameter for debug port */
	static final String DEBUG_PORT_PARAMETER_ID = "no.resheim.eclipse.launcherutil.debug.port"; //$NON-NLS-1$

	/** Command parameter for Java Virtual Machine */
	static final String JVM_PARAMETER_ID = "no.resheim.eclipse.launcherutil.jvm"; //$NON-NLS-1$

	public OpenWorkspaceHandler() {
	}

	public Object execute(final ExecutionEvent event) throws ExecutionException {
		String launcher = System.getProperty(ECLIPSE_LAUNCHER);
		if (launcher != null) {
			// We need to use the Eclipse.app folder so that the application
			// is opened properly. Otherwise we'll also open a shell which is
			// not desirable.
			final File application = LauncherPlugin.getDefault().getLauncherApplication();
			if (application != null) {
				BusyIndicator.showWhile(null, new Runnable() {

					public void run() {
						launch(event, application);
					}

					private void launch(final ExecutionEvent event, final File application) {
						IStatus status = Status.OK_STATUS;
						try {
							final String workspace = event.getParameter(WORKSPACE_PARAMETER_ID);
							String vm = event.getParameter(JVM_PARAMETER_ID);
							if (vm == null) {
								vm = System.getProperty(PROP_VM);
							}
							String cmd = System.getProperty(PROP_COMMANDS);
							String vmargs = System.getProperty(PROP_VMARGS);
							List<String> args = LauncherPlugin.buildCommandLine(workspace, cmd, vmargs, vm);
							status = LauncherPlugin.getDefault().doLaunch(application, args);
						} catch (Exception e) {
							status = new Status(IStatus.ERROR, LauncherPlugin.PLUGIN_ID,
									"Could not execute OpenWorkspaceHandler", e); //$NON-NLS-1$
						}
						if (!status.isOK()) {
							StatusManager.getManager().handle(status);
						}
					}

				});
			}
		}
		return null;
	}
}
