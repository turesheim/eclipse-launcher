/*******************************************************************************
 * Copyright (c) 2013 Torkild U. Resheim.
 *
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Torkild U. Resheim - initial API and implementation
 *******************************************************************************/
package no.resheim.eclipse.quicklook.macosx.handlers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.statushandlers.StatusManager;

/**
 * A handler opening the OS X QuickLook feature on selected files and folders when activated.
 *
 * @author Torkild U. Resheim
 * @see org.eclipse.core.commands.IHandler
 * @see org.eclipse.core.commands.AbstractHandler
 */
public class QuickLookHandler extends AbstractHandler {

	public QuickLookHandler() {
	}

	public Object execute(ExecutionEvent event) throws ExecutionException {
		ISelection currentSelection = HandlerUtil.getCurrentSelection(event);
		if (currentSelection != null) {
			if (currentSelection instanceof IStructuredSelection) {
				IStructuredSelection s = (IStructuredSelection) currentSelection;
				List<String> paths = new ArrayList<String>();
				Object[] objects = s.toArray();
				for (Object object : objects) {
					if (object instanceof IAdaptable) {
						Object adapter = ((IAdaptable) object).getAdapter(IResource.class);
						if (adapter != null) {
							String osString = ((IResource) adapter).getLocation().toOSString();
							paths.add(osString);
						}
					}
				}
				if (!paths.isEmpty()) {
					try {
						execute(paths.toArray(new String[paths.size()]));
					} catch (Exception e) {
						IStatus status = new Status(IStatus.ERROR, "no.resheim.eclipse.utils.quicklook.macosx", //$NON-NLS-1$
								"Could not execute QuickLookHandler", e); //$NON-NLS-1$
						StatusManager.getManager().handle(status);
					}
				}
			}
		}
		return null;
	}

	private void execute(String[] paths) throws IOException, InterruptedException {
		String[] args = new String[paths.length + 2];
		args[0] = "qlmanage"; //$NON-NLS-1$
		args[1] = "-p"; //$NON-NLS-1$
		System.arraycopy(paths, 0, args, 2, paths.length);
		Runtime.getRuntime().exec(args);
	}
}
