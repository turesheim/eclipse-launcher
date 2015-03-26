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
package net.resheim.eclipse.launcher.macosx;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TaskBar;
import org.eclipse.swt.widgets.TaskItem;

import net.resheim.eclipse.launcher.core.IWorkspaceDecorator;

/**
 * Obtains the {@link TaskItem} belonging to the running application and sets the badge to the workspace name.
 * 
 * @author Torkild U. Resheim
 */
public class WorkspaceDecorator implements IWorkspaceDecorator {

	public WorkspaceDecorator() {
	}

	private static TaskItem getTaskBarItem() {
		Display display = Display.getDefault();
		Shell shell = display.getActiveShell();
		TaskBar bar = display.getSystemTaskBar();
		if (bar == null) {
			return null;
		}
		TaskItem item = bar.getItem(shell);
		if (item == null) {
			item = bar.getItem(null);
		}
		return item;
	}

	public void decorateWorkspace(final String name) {
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				TaskItem item = getTaskBarItem();
				item.setOverlayText(name);
			}
		});
	}

}
