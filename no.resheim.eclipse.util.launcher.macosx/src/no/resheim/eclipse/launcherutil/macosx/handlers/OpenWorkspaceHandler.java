/*******************************************************************************
 * Copyright (c) 2012 Torkild U. Resheim.
 * 
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: 
 *     Torkild U. Resheim - initial API and implementation
 *******************************************************************************/
package no.resheim.eclipse.launcherutil.macosx.handlers;

import java.io.File;
import java.io.IOException;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.ui.internal.ide.actions.OpenWorkspaceAction;

/**
 * A command handler that will open up a new Eclipse instance using the same
 * launcher used to opening the current instance.
 * 
 * @author Torkild U. Resheim
 */
@SuppressWarnings("restriction")
public class OpenWorkspaceHandler extends AbstractHandler {

	private static final String PROP_VM = "eclipse.vm"; //$NON-NLS-1$

	private static final String PROP_VMARGS = "eclipse.vmargs"; //$NON-NLS-1$

	private static final String PROP_COMMANDS = "eclipse.commands"; //$NON-NLS-1$

	private static final String CMD_DATA = "-data"; //$NON-NLS-1$

	private static final String CMD_VMARGS = "-vmargs"; //$NON-NLS-1$

	private static final String NEW_LINE = "\n"; //$NON-NLS-1$

	public OpenWorkspaceHandler() {
	}

	/**
	 * Create and return a string with command line options for eclipse.exe that
	 * will launch a new workbench that is the same as the currently running
	 * one, but using the argument directory as its workspace.
	 * 
	 * <p>
	 * Copied from {@link OpenWorkspaceAction}
	 * </p>
	 * 
	 * @param workspace
	 *            the directory to use as the new workspace
	 * @return a string of command line options or null on error
	 */
	private String buildCommandLine(String workspace) {
		String property = System.getProperty(PROP_VM);
		if (property == null) {
			// MessageDialog.openError(window.getShell(),
			// IDEWorkbenchMessages.OpenWorkspaceAction_errorTitle,
			// NLS.bind(IDEWorkbenchMessages.OpenWorkspaceAction_errorMessage,
			// PROP_VM));
			return null;
		}

		StringBuffer result = new StringBuffer(512);
		result.append(property);
		result.append(NEW_LINE);

		// append the vmargs and commands. Assume that these already end in \n
		String vmargs = System.getProperty(PROP_VMARGS);
		if (vmargs != null) {
			result.append(vmargs);
		}

		// append the rest of the args, replacing or adding -data as required
		property = System.getProperty(PROP_COMMANDS);
		if (property == null) {
			result.append(CMD_DATA);
			result.append(NEW_LINE);
			result.append(workspace);
			result.append(NEW_LINE);
		} else {
			// find the index of the arg to replace its value
			int cmd_data_pos = property.lastIndexOf(CMD_DATA);
			if (cmd_data_pos != -1) {
				cmd_data_pos += CMD_DATA.length() + 1;
				result.append(property.substring(0, cmd_data_pos));
				result.append(workspace);
				result.append(property.substring(property.indexOf('\n', cmd_data_pos)));
			} else {
				result.append(CMD_DATA);
				result.append(NEW_LINE);
				result.append(workspace);
				result.append(NEW_LINE);
				result.append(property);
			}
		}

		// put the vmargs back at the very end (the eclipse.commands property
		// already contains the -vm arg)
		if (vmargs != null) {
			result.append(CMD_VMARGS);
			result.append(NEW_LINE);
			result.append(vmargs);
		}

		return result.toString();
	}

	public Object execute(ExecutionEvent event) throws ExecutionException {
		String launcher = System.getProperty("eclipse.launcher");
		final String workspace = event.getParameter("no.resheim.eclipse.launcherutil.workspace");
		if (launcher != null) {
			// We need to use the Eclipse.app folder so that the application
			// is opened properly. Otherwise we'll also open a shell which is
			// not desirable.
			final File app = new File(launcher).getParentFile().getParentFile().getParentFile();
			if (app.exists() && app.isDirectory() && app.getName().endsWith(".app")) {
				BusyIndicator.showWhile(null, new Runnable() {
					public void run() {
						try {
							Runtime.getRuntime().exec(
									"open -n " + app.getAbsolutePath() + " --args " + buildCommandLine(workspace));
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				});
			}
		}
		return null;
	}
}
