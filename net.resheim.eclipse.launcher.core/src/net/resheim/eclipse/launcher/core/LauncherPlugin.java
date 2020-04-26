/*******************************************************************************
 * Copyright (c) 2012-2015 Torkild U. Resheim and others
 *
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Torkild U. Resheim - initial API and implementation
 *   Gunnar Wagenknecht - Use proper launcher ini file on macOS
 *   Martin D'Aloia - Add support to use a path to the libjli.dylib as -vm argument
 *******************************************************************************/
package net.resheim.eclipse.launcher.core;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.MessageFormat;
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
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;

/**
 * Contains shared launcher mechanisms. While this plug-in is platform
 * independent, it does contain some platform specific code. Make sure to handle
 * this when implementing support for other platforms than OS X.
 */
@SuppressWarnings("restriction")
public class LauncherPlugin extends AbstractUIPlugin {

	private static final String APPLE_JAVA = "/System/Library/"; //$NON-NLS-1$

	private static BundleContext bundleContext;

	/** Argument key for specifying the workspace root folder */
	private static final String CMD_DATA = "-data"; //$NON-NLS-1$

	/** Argument key for specifying the JVM to use */
	private static final String CMD_VM = "-vm"; //$NON-NLS-1$

	/** Argument key for specifying JVM arguments */
	private static final String CMD_VMARGS = "-vmargs"; //$NON-NLS-1$

	private static final String EXTENSION_POINT_ID = "net.resheim.eclipse.launcher.core.workspace"; //$NON-NLS-1$

	/** System new line character */
	private static final String NEW_LINE = "\n"; //$NON-NLS-1$

	/** The shared instance */
	private static LauncherPlugin plugin;

	/** The plug-in ID */
	public static final String PLUGIN_ID = "net.resheim.eclipse.launcher.core"; //$NON-NLS-1$

	/**
	 * @since 2.0
	 */
	public static final String PROP_COMMANDS = "eclipse.commands"; //$NON-NLS-1$

	/**
	 * @since 2.0
	 */
	public static final String PROP_VM = "eclipse.vm"; //$NON-NLS-1$

	/**
	 * @since 2.0
	 */
	public static final String PROP_VMARGS = "eclipse.vmargs"; //$NON-NLS-1$

	/**
	 * Create and return a string with command line options for starting
	 * Eclipse. If the workspace to use already has been specified in
	 * <i>workspace</i> this property will be overridden using the new
	 * workspace. A similar replacement is done for the virtual machine path,
	 * <i>vm</i>.
	 * <p>
	 * Similar to {@link OpenWorkspaceAction}
	 * </p>
	 *
	 * @param workspace
	 *            the directory to use as workspace or <code>null</code>
	 * @param commands
	 *            the system property "eclipse.commands"
	 * @param vmargs
	 *            the system property "eclipse.vmargs"
	 * @param vm
	 *            path to the Java virtual machine or <code>null</code>
	 * @return a string of command line options or <code>null</code> on error
	 * @since 2.0
	 */
	public static List<String> buildCommandLine(String workspace, String commands, String vmargs, String vm) {
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
				} else if (!vm.endsWith("java") && !vm.endsWith("libjvm.dylib") && !vm.endsWith("libjli.dylib")) { //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
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
	 * @since 2.0
	 */
	public static BundleContext getContext() {
		return bundleContext;
	}

	/**
	 * Returns the shared instance.
	 *
	 * @return the shared instance
	 */
	public static LauncherPlugin getDefault() {
		return plugin;
	}

	/**
	 * Launches a new Eclipse instance.
	 *
	 * @param workspace
	 *            path to the workspace
	 * @param application
	 *            the eclipse application executable
	 * @param args
	 *            command line arguments
	 * @return a status code
	 * @throws LaunchException
	 * @since 2.0
	 */
	public IStatus doLaunch(final File application, List<String> args) throws LaunchException {

		// Arguments for OS X in reverse order
		args.add(0, "--args"); //$NON-NLS-1$
		args.add(0, application.getAbsolutePath());
		args.add(0, "-n"); //$NON-NLS-1$
		args.add(0, "open"); //$NON-NLS-1$
		StringBuilder sb = new StringBuilder();
		for (String string : args) {
			sb.append(string);
			sb.append(' ');
		}

		// Do some logging
		String message = MessageFormat.format("Launching new Eclipse instance with \"{0}\"", sb.toString()); //$NON-NLS-1$
		StatusManager.getManager().handle(new Status(IStatus.INFO, LauncherPlugin.PLUGIN_ID, message),
				StatusManager.LOG);

		try {
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
		} catch (IOException e) {
			throw new LaunchException(e);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			return new Status(IStatus.ERROR, PLUGIN_ID, "Could not launch new instance", e); //$NON-NLS-1$
		}
		return Status.OK_STATUS;
	}

	/**
	 * Returns the workspace decorator if one is available.
	 *
	 * @return the workspace decorator or <code>null</code>
	 */
	protected IWorkspaceDecorator getDecorator() {
		IExtensionPoint ePoint = Platform.getExtensionRegistry().getExtensionPoint(EXTENSION_POINT_ID);
		IConfigurationElement[] synchronizers = ePoint.getConfigurationElements();
		try {
			for (IConfigurationElement configurationElement : synchronizers) {
				if ("decorator".equals(configurationElement.getName())) { //$NON-NLS-1$
					Object object = configurationElement.createExecutableExtension("class"); //$NON-NLS-1$
					if (object instanceof IWorkspaceDecorator) {
						return (IWorkspaceDecorator) object;
					}
				}
			}
		} catch (CoreException e) {
			StatusManager.getManager().handle(e, PLUGIN_ID);
		}
		return null;
	}

	/**
	 * Gathers and returns a list of all Java Runtime Environments found on the
	 * host. If none are found, the list will be empty.
	 *
	 * @since 2.0
	 */
	public List<JRE> getJavaRuntimeEnvironments() {
		List<JRE> runtimes = new ArrayList<>();
		BundleContext context = FrameworkUtil.getBundle(this.getClass()).getBundleContext();
		ServiceReference<?> serviceReference = context.getServiceReference(IJavaLocatorService.class);
		if (serviceReference != null) {
			IJavaLocatorService service = (IJavaLocatorService) context.getService(serviceReference);
			runtimes = service.getRuntimes();
			context.ungetService(serviceReference);
		}
		return runtimes;
	}

	/**
	 * Returns the Eclipse executable. In case of OS X; the file pointing to the
	 * "Eclipse.app" folder will be returned.
	 *
	 * @return the Eclipse launcher or <code>null</code>if it could not be found
	 * @since 2.0
	 */
	public File getLauncherApplication() {
		String launcher = System.getProperty("eclipse.launcher"); //$NON-NLS-1$
		if (launcher == null) {
			throw new IllegalStateException("Property 'eclipse.launcher' not set. Check your Eclipse installation!"); //$NON-NLS-1$
		}

		// We need to use the Eclipse.app folder so that the application
		// is opened properly. Otherwise we'll also open a shell which is
		// not desirable.
		if (isRunningOnMacOs()) {
			final File application = new File(launcher).getParentFile().getParentFile().getParentFile();
			if (application.exists() && application.isDirectory() && application.getName().endsWith(".app")) { //$NON-NLS-1$
				return application;
			}
			StatusManager.getManager().handle(
					new Status(IStatus.WARNING, PLUGIN_ID, "Unknonw app layout detected. Using default."), //$NON-NLS-1$
					StatusManager.LOG);
		}
		return new File(launcher);
	}

	/**
	 * Returns the Eclipse executable configuration file (eclipse.ini) that
	 * belongs to the specified launcher.
	 *
	 * @param launcherApplication
	 *            as detected and returned by {@link #getLauncherApplication()}
	 * @return the launcher ini file
	 * @since 3.0
	 * @throws FileNotFoundException
	 *             in case the launcher ini file does not exist
	 */
	public File getLauncherIniFile(File launcherApplication) throws FileNotFoundException {
		if (launcherApplication == null || !launcherApplication.exists()) {
			throw new IllegalArgumentException("Invalid launcher application: " + launcherApplication); //$NON-NLS-1$
		}

		// Attempt to figure out the corresponding ".ini" file
		final File inifile;
		if (isRunningOnMacOs() && launcherApplication.isDirectory()) {
			inifile = new File(launcherApplication, "Contents/Eclipse/eclipse.ini"); //$NON-NLS-1$
		} else {
			inifile = new File(launcherApplication.getParentFile(), "eclipse.ini"); //$NON-NLS-1$
		}

		if (!inifile.isFile()) {
			throw new FileNotFoundException("Launcher ini file not found: " + inifile); //$NON-NLS-1$
		}

		return inifile;
	}

	public boolean isRunningOnMacOs() {
		return Platform.getOS().equals(Platform.OS_MACOSX);
	}

	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		bundleContext = context;
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
		bundleContext = null;
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

}
