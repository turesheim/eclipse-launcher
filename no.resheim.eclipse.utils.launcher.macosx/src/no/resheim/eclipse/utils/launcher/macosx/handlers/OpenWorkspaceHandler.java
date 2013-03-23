/*******************************************************************************
 * Copyright (c) 2012 Torkild U. Resheim and others
 * 
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: 
 *     Torkild U. Resheim - initial API and implementation
 *******************************************************************************/
package no.resheim.eclipse.utils.launcher.macosx.handlers;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import no.resheim.eclipse.utils.launcher.core.LauncherPlugin;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.ui.statushandlers.StatusManager;

/**
 * A command handler that will open up a new Eclipse instance using the same launcher used to opening the current
 * instance. Virtual machine arguments used to start the original instance will be passed along.
 * 
 * @author Torkild U. Resheim
 */
public class OpenWorkspaceHandler extends AbstractHandler {

	private static final String ECLIPSE_LAUNCHER = "eclipse.launcher"; //$NON-NLS-1$

	private static final String PROP_VMARGS = "eclipse.vmargs"; //$NON-NLS-1$

	private static final String PROP_VM = "eclipse.vm"; //$NON-NLS-1$

	private static final String PROP_COMMANDS = "eclipse.commands"; //$NON-NLS-1$

	private static final String NEW_LINE = "\n"; //$NON-NLS-1$

	static final String PARAMETER_ID = "no.resheim.eclipse.launcherutil.workspace"; //$NON-NLS-1$

	public OpenWorkspaceHandler() {
	}

	public Object execute(ExecutionEvent event) throws ExecutionException {
		String launcher = System.getProperty(ECLIPSE_LAUNCHER);
		final String workspace = event.getParameter(PARAMETER_ID);
		if (launcher != null) {
			// We need to use the Eclipse.app folder so that the application
			// is opened properly. Otherwise we'll also open a shell which is
			// not desirable.
			final File application = new File(launcher).getParentFile().getParentFile().getParentFile();
			if (application.exists() && application.isDirectory() && application.getName().endsWith(".app")) { //$NON-NLS-1$
				BusyIndicator.showWhile(null, new Runnable() {
					public void run() {
						IStatus status = Status.OK_STATUS;
						try {
							status = doLaunch(workspace, application, status);
						} catch (Exception e) {
							status = new Status(IStatus.ERROR, LauncherPlugin.PLUGIN_ID,
									"Could not execute OpenWorkspaceHandler", e); //$NON-NLS-1$
						}
						if (!status.isOK()) {
							StatusManager.getManager().handle(status);
						}
					}

					private IStatus doLaunch(final String workspace, final File app, IStatus status)
							throws IOException, InterruptedException {
						String cmd = System.getProperty(PROP_COMMANDS);
						String vmargs = System.getProperty(PROP_VMARGS);
						String vm = System.getProperty(PROP_VM);
						ArrayList<String> args = LauncherPlugin.getDefault().buildCommandLine(workspace, cmd,
								vmargs, vm);
						// for OS X
						args.add(0, "--args"); //$NON-NLS-1$
						args.add(0, app.getAbsolutePath());
						args.add(0, "-n"); //$NON-NLS-1$
						args.add(0, "open"); //$NON-NLS-1$
						StringBuilder sb = new StringBuilder();
						for (String string : args) {
							sb.append(string);
							sb.append(' ');
						}
						StatusManager.getManager()
								.handle(new Status(IStatus.INFO, LauncherPlugin.PLUGIN_ID,
										"Launching new Eclipse instance with \"" + sb.toString() + "\""), StatusManager.LOG); //$NON-NLS-1$ //$NON-NLS-2$
						Process p = Runtime.getRuntime().exec(args.toArray(new String[args.size()]));
						if (p.waitFor() != 0) {
							BufferedReader br = new BufferedReader(new InputStreamReader(p.getErrorStream()));
							sb.setLength(0);
							String in = null;
							while ((in = br.readLine()) != null) {
								sb.append(NEW_LINE);
								sb.append(in);
							}
							br.close();
							if (sb.length() > 0) {
								status = new Status(IStatus.ERROR, LauncherPlugin.PLUGIN_ID,
										"Could not execute OpenWorkspaceHandler." + sb.toString()); //$NON-NLS-1$
							}
						}
						return status;
					}
				});
			}
		}
		return null;
	}
}
