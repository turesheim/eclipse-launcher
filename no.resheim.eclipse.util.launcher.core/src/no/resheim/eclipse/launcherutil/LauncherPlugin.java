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
package no.resheim.eclipse.launcherutil;

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
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

@SuppressWarnings("restriction")
public class LauncherPlugin extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "no.resheim.eclipse.util.launcher.core"; //$NON-NLS-1$

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
			@Override
			public void propertyChange(PropertyChangeEvent event) {
				updateDecorator();
			}
		});
		updateDecorator();
	}

	public static IWorkspaceDecorator getSynchronizer() {

		IExtensionPoint ePoint = Platform.getExtensionRegistry().getExtensionPoint(
				"no.resheim.eclipse.util.launcher.core.workspace");
		IConfigurationElement[] synchronizers = ePoint.getConfigurationElements();
		for (IConfigurationElement configurationElement : synchronizers) {
			if (configurationElement.getName().equals("decorator")) {
				try {
					Object object = configurationElement.createExecutableExtension("class");
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

	public void updateDecorator() {
		SafeRunnable safeRunnable = new SafeRunnable() {
			@Override
			public void run() throws Exception {
				IPreferenceStore store = IDEWorkbenchPlugin.getDefault().getPreferenceStore();
				String name = store.getString(IDEInternalPreferences.WORKSPACE_NAME);
				if (name != null) {
					IWorkspaceDecorator decorator = getSynchronizer();
					decorator.decorateWorkspace(name);
				}
			}
		};
		SafeRunner.run(safeRunnable);
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
