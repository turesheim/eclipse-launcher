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
package no.resheim.eclipse.utils.launcher.macosx.handlers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import no.resheim.eclipse.utils.launcher.core.LauncherPlugin;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.swt.SWT;
import org.eclipse.ui.internal.ide.ChooseWorkspaceData;
import org.eclipse.ui.menus.CommandContributionItem;
import org.eclipse.ui.menus.CommandContributionItemParameter;
import org.eclipse.ui.menus.ExtensionContributionFactory;
import org.eclipse.ui.menus.IContributionRoot;
import org.eclipse.ui.services.IServiceLocator;
import org.eclipse.ui.statushandlers.StatusManager;

@SuppressWarnings("restriction")
public class OpenWorkspaceMenu extends ExtensionContributionFactory {

	private static final String COMMAND_ID = "no.resheim.eclipse.launcherutil.commands.newInstance"; //$NON-NLS-1$

	public OpenWorkspaceMenu() {
	}

	private IContributionItem[] getContributionItems(IServiceLocator serviceLocator) {
		ArrayList<IContributionItem> list = new ArrayList<IContributionItem>();
		try {
			final ChooseWorkspaceData data = new ChooseWorkspaceData(Platform.getInstanceLocation().getURL());
			data.readPersistedData();
			String current = data.getInitialDefault();
			String[] workspaces = data.getRecentWorkspaces();
			for (int i = 0; i < workspaces.length; i++) {
				if (workspaces[i] != null && !workspaces[i].equals(current)) {
					list.add(createOpenCommand(serviceLocator, workspaces[i], workspaces[i]));
				}
			}
			if (list.size() > 0) {
				list.add(new Separator());
			}
			list.add(createOpenCommand(serviceLocator, Messages.OpenWorkspaceMenu_Other, null));
		} catch (Exception e) {
			IStatus newStatus = new Status(IStatus.ERROR, LauncherPlugin.PLUGIN_ID,
					"Could not create workspace chooser menu item.", e); //$NON-NLS-1$
			StatusManager.getManager().handle(newStatus);
		}
		return list.toArray(new IContributionItem[list.size()]);
	}

	@Override
	public void createContributionItems(IServiceLocator serviceLocator, IContributionRoot additions) {
		MenuManager submenu = new MenuManager(Messages.OpenWorkspaceMenu_Open);
		IContributionItem[] items = getContributionItems(serviceLocator);
		for (IContributionItem iContributionItem : items) {
			submenu.add(iContributionItem);
		}
		additions.addContributionItem(submenu, null);
	}

	public CommandContributionItem createOpenCommand(IServiceLocator serviceLocator, String label, String workspace) {
		CommandContributionItemParameter p = new CommandContributionItemParameter(serviceLocator, "", //$NON-NLS-1$
				COMMAND_ID, SWT.PUSH);
		if (workspace != null) {
			Map<Object, Object> parameters = new HashMap<Object, Object>();
			parameters.put(OpenWorkspaceHandler.PARAMETER_ID, workspace);
			p.parameters = parameters;
		}
		p.label = label;
		CommandContributionItem item = new CommandContributionItem(p);
		item.setVisible(true);
		return item;
	}

}
