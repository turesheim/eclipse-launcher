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
	private IWorkspaceDecorator getDecorator() {
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

	/**
	 * Returns the shared instance
	 * 
	 * @return the shared instance
	 */
	public static LauncherPlugin getDefault() {
		return plugin;
	}

}
