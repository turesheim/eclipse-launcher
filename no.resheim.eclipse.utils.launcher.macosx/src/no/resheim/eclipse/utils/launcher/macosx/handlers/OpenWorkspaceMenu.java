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

import org.eclipse.core.runtime.Platform;
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

@SuppressWarnings("restriction")
public class OpenWorkspaceMenu extends ExtensionContributionFactory {

	public OpenWorkspaceMenu() {
	}

	private IContributionItem[] getContributionItems(IServiceLocator serviceLocator) {
		ArrayList<IContributionItem> list = new ArrayList<IContributionItem>();
		final ChooseWorkspaceData data = new ChooseWorkspaceData(Platform.getInstanceLocation().getURL());
		data.readPersistedData();
		String current = data.getInitialDefault();
		String[] workspaces = data.getRecentWorkspaces();
		for (int i = 0; i < workspaces.length; i++) {
			if (workspaces[i] != null && !workspaces[i].equals(current)) {
				list.add(createCommand(serviceLocator, workspaces[i], workspaces[i]));
			}
		}
		if (list.size() > 0) {
			list.add(new Separator());
		}
		list.add(createCommand(serviceLocator, "Other...", null));
		return list.toArray(new IContributionItem[list.size()]);
	}

	@Override
	public void createContributionItems(IServiceLocator serviceLocator, IContributionRoot additions) {
		MenuManager submenu = new MenuManager("Open Workspace");
		IContributionItem[] items = getContributionItems(serviceLocator);
		for (IContributionItem iContributionItem : items) {
			submenu.add(iContributionItem);
		}
		additions.addContributionItem(submenu, null);
	}

	public CommandContributionItem createCommand(IServiceLocator serviceLocator, String label, String workspace) {
		CommandContributionItemParameter p = new CommandContributionItemParameter(serviceLocator, "",
				"no.resheim.eclipse.launcherutil.commands.newInstance", SWT.PUSH);
		Map<Object, Object> parameters = new HashMap<Object, Object>();
		parameters.put("no.resheim.eclipse.launcherutil.workspace", workspace);
		p.parameters = parameters;
		p.label = label;
		CommandContributionItem item = new CommandContributionItem(p);
		item.setVisible(true);
		return item;
	}

}
