/*******************************************************************************
 * Copyright (c) 2012 Torkild U. Resheim.
 * 
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Torkild U. Resheim - initial API and implementation
 *******************************************************************************/
package no.resheim.eclipse.utils.launcher.core;

import java.util.ArrayList;

import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.SafeRunner;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.util.SafeRunnable;
import org.eclipse.ui.internal.ide.IDEInternalPreferences;
import org.eclipse.ui.internal.ide.IDEWorkbenchPlugin;
import org.eclipse.ui.internal.ide.actions.OpenWorkspaceAction;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

@SuppressWarnings("restriction")
public class LauncherPlugin extends AbstractUIPlugin {

	private static final String EXTENSION_POINT_ID = "no.resheim.eclipse.utils.launcher.core.workspace"; //$NON-NLS-1$

	// The plug-in ID
	public static final String PLUGIN_ID = "no.resheim.eclipse.utils.launcher.core"; //$NON-NLS-1$

	// The shared instance
	private static LauncherPlugin plugin;

	/**
	 * The constructor
	 */
	public LauncherPlugin() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext
	 * )
	 */
	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		IPreferenceStore store = IDEWorkbenchPlugin.getDefault().getPreferenceStore();
		store.addPropertyChangeListener(new IPropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent event) {
				if (event.getProperty().equals(IDEInternalPreferences.WORKSPACE_NAME)) {
					updateDecorator();
				}
			}
		});
		updateDecorator();
	}

	/**
	 * Returns the workspace decorator if one is available.
	 * 
	 * @return the workspace decorator or <code>null</code>
	 */
	protected IWorkspaceDecorator getDecorator() {
		IExtensionPoint ePoint = Platform.getExtensionRegistry().getExtensionPoint(EXTENSION_POINT_ID);
		IConfigurationElement[] synchronizers = ePoint.getConfigurationElements();
		for (IConfigurationElement configurationElement : synchronizers) {
			if (configurationElement.getName().equals("decorator")) { //$NON-NLS-1$
				try {
					Object object = configurationElement.createExecutableExtension("class"); //$NON-NLS-1$
					if (object instanceof IWorkspaceDecorator) {
						return ((IWorkspaceDecorator) object);
					}
				} catch (CoreException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext
	 * )
	 */
	@Override
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Decorates the workspace icon with the workspace name if a mechanism for doing so is available.
	 * 
	 * @see #getDecorator()
	 */
	private void updateDecorator() {
		final IWorkspaceDecorator decorator = getDecorator();
		if (decorator != null) {
			SafeRunnable safeRunnable = new SafeRunnable() {
				public void run() throws Exception {
					// Obtain the workspace name from preferences
					IPreferenceStore store = IDEWorkbenchPlugin.getDefault().getPreferenceStore();
					String name = store.getString(IDEInternalPreferences.WORKSPACE_NAME);
					// Use path segment if no preference is set
					if (name == null || name.length() == 0) {
						IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
						name = root.getLocation().toFile().getName();
					}
					decorator.decorateWorkspace(name);
				}
			};
			SafeRunner.run(safeRunnable);
		}
	}

	private static final String CMD_DATA = "-data"; //$NON-NLS-1$

	private static final String CMD_VM = "-vm"; //$NON-NLS-1$

	private static final String CMD_VMARGS = "-vmargs"; //$NON-NLS-1$

	/**
	 * Create and return a string with command line options for eclipse.exe that will launch a new workbench that is the
	 * same as the currently running one, but using the argument directory as its workspace.
	 * <p>
	 * Copied from {@link OpenWorkspaceAction}
	 * </p>
	 * 
	 * @param workspace
	 *            the directory to use as the new workspace
	 * @param commands
	 *            the system property "eclipse.commands"
	 * @param vmargs
	 *            the system property "eclipse.vmargs"
	 * @return a string of command line options or null on error
	 */
	public ArrayList<String> buildCommandLine(String workspace, String commands, String vmargs, String vm) {
		ArrayList<String> arguments = new ArrayList<String>();
		// Handle the command string
		String[] argStrings = commands.split("\\n"); //$NON-NLS-1$
		boolean hasData = false;
		for (int i = 0; i < argStrings.length; i++) {
			String string = argStrings[i];
			arguments.add(string);
			if (string.equals(CMD_DATA)) {
				if (workspace == null) {
					// Remove the workspace (it must be selected)
					arguments.remove(string);
					i++;
				} else {
					// Replace the workspace argument if a workspace has been selected
					argStrings[i + 1] = workspace;
					hasData = true;
				}
			}
			if (string.equals(CMD_VM)) {
				if (vm == null) {
					// Re-use the "-vm" argument
					arguments.remove(string);
					vm = argStrings[i + 1];
					i++;
				} else {
					// Ignore the "-vm" argument
					arguments.remove(string);
					i++;
				}
			}
		}
		// Add the "-vm" argument. It must be first.
		if (vm != null) {
			if (!vm.endsWith("/java")) { //$NON-NLS-1$
				vm = vm + "/Contents/Home/bin/java"; //$NON-NLS-1$
			}
			arguments.add(0, vm);
			arguments.add(0, CMD_VM);
		}
		// Add the -data option if specified
		if (!hasData && workspace != null) {
			arguments.add(CMD_DATA);
			arguments.add(workspace);
		}
		// Put the -vmargs option back at the very end
		if (vmargs != null) {
			arguments.add(CMD_VMARGS);
			for (String string : vmargs.split("\n")) { //$NON-NLS-1$
				arguments.add(string);
			}
		}
		return arguments;
	}

	/**
	 * Returns the shared instance
	 * 
	 * @return the shared instance
	 */
	public static LauncherPlugin getDefault() {
		return plugin;
	}

}
