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
import java.io.InputStreamReader;
import java.util.ArrayList;

import no.resheim.eclipse.utils.launcher.core.LauncherPlugin;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.ui.internal.ide.actions.OpenWorkspaceAction;
import org.eclipse.ui.statushandlers.StatusManager;

/**
 * A command handler that will open up a new Eclipse instance using the same launcher used to opening the current
 * instance. Virtual machine arguments used to start the original instance will be passed along.
 * 
 * @author Torkild U. Resheim
 */
@SuppressWarnings("restriction")
public class OpenWorkspaceHandler extends AbstractHandler {

	private static final String ECLIPSE_LAUNCHER = "eclipse.launcher"; //$NON-NLS-1$

	private static final String PROP_VM = "eclipse.vm"; //$NON-NLS-1$

	private static final String PROP_VMARGS = "eclipse.vmargs"; //$NON-NLS-1$

	private static final String PROP_COMMANDS = "eclipse.commands"; //$NON-NLS-1$

	private static final String CMD_DATA = "-data"; //$NON-NLS-1$

	private static final String CMD_VMARGS = "-vmargs"; //$NON-NLS-1$

	private static final String NEW_LINE = "\n"; //$NON-NLS-1$

	static final String PARAMETER_ID = "no.resheim.eclipse.launcherutil.workspace"; //$NON-NLS-1$

	public OpenWorkspaceHandler() {
	}

	/**
	 * Create and return a string with command line options for eclipse.exe that will launch a new workbench that is the
	 * same as the currently running one, but using the argument directory as its workspace.
	 * <p>
	 * Copied from {@link OpenWorkspaceAction}
	 * </p>
	 * 
	 * @param workspace
	 *            the directory to use as the new workspace
	 * @return a string of command line options or null on error
	 */
	private ArrayList<String> buildCommandLine(String workspace) {
		ArrayList<String> arguments = new ArrayList<String>();
		String property = System.getProperty(PROP_VM);
		// Use defaults;
		if (property == null) {
			return arguments;
		}

		arguments.add("--args"); //$NON-NLS-1$
		arguments.add(property);

		// append the vmargs and commands. Assume that these already end in \n
		String vmargs = System.getProperty(PROP_VMARGS);
		if (vmargs != null) {
			for (String string : vmargs.split(" ")) { //$NON-NLS-1$
				arguments.add(string);
			}
		}

		// append the rest of the argsuments, replacing or adding -data as required
		property = System.getProperty(PROP_COMMANDS);
		if (property == null && workspace != null) {
			arguments.add(CMD_DATA);
			arguments.add(workspace);
		} else if (workspace != null && property != null) {
			// find the index of the argument to replace its value
			int cmd_data_pos = property.lastIndexOf(CMD_DATA);
			if (cmd_data_pos != -1) {
				cmd_data_pos += CMD_DATA.length() + 1;
				arguments.add(property.substring(0, cmd_data_pos));
				arguments.add(workspace);
				arguments.add(property.substring(property.indexOf('\n', cmd_data_pos)));
			} else {
				arguments.add(CMD_DATA);
				arguments.add(workspace);
				arguments.add(property);
			}
		}

		// put the vmargs back at the very end (the eclipse.commands property
		// already contains the -vm arg)
		if (vmargs != null) {
			arguments.add(CMD_VMARGS);
			for (String string : vmargs.split(" ")) { //$NON-NLS-1$
				arguments.add(string);
			}
		}

		return arguments;
	}

	public Object execute(ExecutionEvent event) throws ExecutionException {
		String launcher = System.getProperty(ECLIPSE_LAUNCHER);
		final String workspace = event.getParameter(PARAMETER_ID);
		if (launcher != null) {
			// We need to use the Eclipse.app folder so that the application
			// is opened properly. Otherwise we'll also open a shell which is
			// not desirable.
			final File app = new File(launcher).getParentFile().getParentFile().getParentFile();
			if (app.exists() && app.isDirectory() && app.getName().endsWith(".app")) { //$NON-NLS-1$
				BusyIndicator.showWhile(null, new Runnable() {
					public void run() {
						IStatus status = Status.OK_STATUS;
						try {
							ArrayList<String> args = buildCommandLine(workspace);
							args.add(0, app.getAbsolutePath());
							args.add(0, "-n"); //$NON-NLS-1$
							args.add(0, "open"); //$NON-NLS-1$
							Process p = Runtime.getRuntime().exec(args.toArray(new String[args.size()]));
							if (p.waitFor() != 0) {
								BufferedReader br = new BufferedReader(new InputStreamReader(p.getErrorStream()));
								StringBuilder sb = new StringBuilder();
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
