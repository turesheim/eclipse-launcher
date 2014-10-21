/*******************************************************************************
 * Copyright (c) 2012, 2013, 2014 Torkild U. Resheim.
 *
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Torkild U. Resheim - initial API and implementation
 *******************************************************************************/
package no.resheim.eclipse.utils.launcher.core;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.SafeRunner;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.util.SafeRunnable;
import org.eclipse.ui.internal.ide.IDEInternalPreferences;
import org.eclipse.ui.internal.ide.IDEWorkbenchPlugin;
import org.eclipse.ui.internal.ide.actions.OpenWorkspaceAction;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.ui.statushandlers.StatusManager;
import org.osgi.framework.BundleContext;

/**
 * Contains shared launcher mechanisms. While this plug-in is platform
 * independent, it does contain some platform specific code. Make sure to handle
 * this when implementing support for other platforms than OS X.
 */
@SuppressWarnings("restriction")
public class LauncherPlugin extends AbstractUIPlugin {

	private static final String APPLE_JAVA = "/System/Library/"; //$NON-NLS-1$

	private static final String EXTENSION_POINT_ID = "no.resheim.eclipse.utils.launcher.core.workspace"; //$NON-NLS-1$

	/** The plug-in ID */
	public static final String PLUGIN_ID = "no.resheim.eclipse.utils.launcher.core"; //$NON-NLS-1$

	/** The shared instance */
	private static LauncherPlugin plugin;

	/** Argument key for specifying the workspace root folder */
	private static final String CMD_DATA = "-data"; //$NON-NLS-1$

	/** Argument key for specifying the JVM to use */
	private static final String CMD_VM = "-vm"; //$NON-NLS-1$

	/** Argument key for specifying JVM arguments */
	private static final String CMD_VMARGS = "-vmargs"; //$NON-NLS-1$

	/** System new line character */
	private static final String NEW_LINE = "\n"; //$NON-NLS-1$

	/**
	 * @since 2.0
	 */
	public static final String PROP_VMARGS = "eclipse.vmargs"; //$NON-NLS-1$

	/**
	 * @since 2.0
	 */
	public static final String PROP_VM = "eclipse.vm"; //$NON-NLS-1$

	/**
	 * @since 2.0
	 */
	public static final String PROP_COMMANDS = "eclipse.commands"; //$NON-NLS-1$

	/**
	 * Returns the shared instance.
	 *
	 * @return the shared instance
	 */
	public static LauncherPlugin getDefault() {
		return plugin;
	}

	/**
	 * Create and return a string with command line options for starting
	 * Eclipse. If the workspace to use already has been specified in
	 * <i>commands</i> this property will be overridden using the new workspace.
	 * A similar replacement is done for the virtual machine path.
	 * <p>
	 * Similar to {@link OpenWorkspaceAction}
	 * </p>
	 *
	 * @param workspace
	 *            the directory to use as the new workspace or null
	 * @param commands
	 *            the system property "eclipse.commands"
	 * @param vmargs
	 *            the system property "eclipse.vmargs"
	 * @param vm
	 *            path to the Java virtual machine or null
	 * @return a string of command line options or null on error
	 * @since 2.0
	 */
	List<String> buildCommandLine(String workspace, String commands, String vmargs, String vm) {
		List<String> arguments = new ArrayList<String>();
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
					// Replace the workspace argument if a workspace has been
					// selected
					argStrings[i + 1] = workspace;
					hasData = true;
				}
			}
			if (string.equals(CMD_VM)) {
				arguments.remove(string);
				if (vm == null) {
					// Re-use the "-vm" argument
					vm = argStrings[i + 1];
				}
				// Ignore the "-vm" argument, it will be added later
				i++;
			}
		}
		// Add the "-vm" argument unless default is used
		if (vm != null) {
			// Handle that Eclipse pre 4.3 does not add/require full path to a
			// Java executable and fix this by pointing to the expected
			// location.
			// https://github.com/turesheim/eclipse-utilities/issues/5245
			if (!vm.startsWith(APPLE_JAVA)) {
				if (vm.endsWith("Contents/Home")) { //$NON-NLS-1$
					vm = vm + "/bin/java"; //$NON-NLS-1$
				} else if (!vm.endsWith("java") && !vm.endsWith("libjvm.dylib")) { //$NON-NLS-1$ //$NON-NLS-2$
					vm = vm + "/Contents/Home/bin/java"; //$NON-NLS-1$
				}
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
	 * Returns the workspace decorator if one is available.
	 *
	 * @return the workspace decorator or <code>null</code>
	 */
	protected IWorkspaceDecorator getDecorator() {
		IExtensionPoint ePoint = Platform.getExtensionRegistry().getExtensionPoint(EXTENSION_POINT_ID);
		IConfigurationElement[] synchronizers = ePoint.getConfigurationElements();
		for (IConfigurationElement configurationElement : synchronizers) {
			if ("decorator".equals(configurationElement.getName())) { //$NON-NLS-1$
				try {
					Object object = configurationElement.createExecutableExtension("class"); //$NON-NLS-1$
					if (object instanceof IWorkspaceDecorator) {
						return (IWorkspaceDecorator) object;
					}
				} catch (CoreException e) {
					StatusManager.getManager().handle(e, PLUGIN_ID);
				}
			}
		}
		return null;
	}

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

	@Override
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the Eclipse executable. In case of OS X the file pointing to the
	 * "Eclipse.app" folder will be return.
	 *
	 * @return the Eclipse launcher or <code>null</code>if it could not be found
	 * @since 2.0
	 */
	public File getLauncherApplication() {
		String launcher = System.getProperty("eclipse.launcher"); //$NON-NLS-1$
		if (launcher != null) {
			// We need to use the Eclipse.app folder so that the application
			// is opened properly. Otherwise we'll also open a shell which is
			// not desirable.
			final File application = new File(launcher).getParentFile().getParentFile().getParentFile();
			if (application.exists() && application.isDirectory() && application.getName().endsWith(".app")) { //$NON-NLS-1$
				return application;
			}
		}
		return null;
	}

	/**
	 * Decorates the workspace icon with the workspace name if a mechanism for
	 * doing so is available.
	 *
	 * @see #getDecorator()
	 */
	private void updateDecorator() {
		final IWorkspaceDecorator decorator = getDecorator();
		if (decorator != null) {
			SafeRunnable safeRunnable = new SafeRunnable() {
				public void run() {
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

	/**
	 * Launches a new Eclipse instance using
	 *
	 * @param workspace
	 *            path to the workspace
	 * @param app
	 *            the eclipse application executable
	 * @param cmd
	 *            contents of the system property "eclipse.commands"
	 * @param vmargs
	 *            contents of the system property "eclipse.vmargs"
	 * @param vm
	 *            path to the Java virtual machine or <code>null</code>
	 * @return
	 * @throws IOException
	 * @throws InterruptedException
	 * @since 2.0
	 */
	public IStatus doLaunch(final String workspace, final File app, String cmd, String vmargs, String vm)
			throws IOException, InterruptedException {

		List<String> args = buildCommandLine(workspace, cmd, vmargs, vm);

		// Arguments for OS X in reverse order
		args.add(0, "--args"); //$NON-NLS-1$
		args.add(0, app.getAbsolutePath());
		args.add(0, "-n"); //$NON-NLS-1$
		args.add(0, "open"); //$NON-NLS-1$
		StringBuilder sb = new StringBuilder();
		for (String string : args) {
			sb.append(string);
			sb.append(' ');
		}

		// Do some logging
		StatusManager.getManager().handle(
				new Status(IStatus.INFO, LauncherPlugin.PLUGIN_ID,
						"Launching new Eclipse instance with \"" + sb.toString() + "\""), StatusManager.LOG); //$NON-NLS-1$ //$NON-NLS-2$

		// Execute the command line
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
				return new Status(IStatus.ERROR, LauncherPlugin.PLUGIN_ID,
						"Could not execute OpenWorkspaceHandler." + sb.toString()); //$NON-NLS-1$
			}
		}
		return Status.OK_STATUS;
	}

}
