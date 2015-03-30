/*******************************************************************************
 * Copyright (c) 2014-2015 Torkild U. Resheim
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Torkild U. Resheim - initial API and implementation
 *******************************************************************************/

package net.resheim.eclipse.launcher.macosx;

import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.internal.ide.ChooseWorkspaceData;
import org.eclipse.ui.statushandlers.StatusManager;

import net.resheim.eclipse.launcher.core.JRE;
import net.resheim.eclipse.launcher.core.LauncherPlugin;

/**
 * Dialog for specifying Eclipse launch options.
 *
 * @author Torkild U. Resheim
 * @since 2.0
 */
@SuppressWarnings("restriction")
public class LaunchOptionsDialog extends TitleAreaDialog {

	public enum DebugMode {
		Debug, Normal, Suspend
	}

	private boolean disableSmallFonts;

	private boolean clean;

	private Combo runtimeCombo;

	/** The list of available Java Runtime Engines */
	private List<JRE> runtimes;

	private DebugMode selectedDebugMode;

	private int selectedDebugPort;

	private JRE selectedJRE;

	private String selectedWorkspace;

	private String selectedXms;

	private String selectedXmx;

	private Combo workspaceCombo;

	public LaunchOptionsDialog(Shell parentShell) {
		super(parentShell);
	}

	@Override
	protected void configureShell(Shell shell) {
		super.configureShell(shell);
		shell.setText(Messages.LaunchOptionsDialog_0);
	}

	private void createDebugOptionsGroup(Composite parent) {

		Group grpDebugging = new Group(parent, SWT.NONE);
		grpDebugging.setText(Messages.LaunchOptionsDialog_grpDebugging_text);
		grpDebugging.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 3, 1));
		grpDebugging.setLayout(new GridLayout(3, false));

		final Combo combo_1 = new Combo(grpDebugging, SWT.READ_ONLY);
		combo_1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		combo_1.add(Messages.LaunchOptionsDialog_2);
		combo_1.add(Messages.LaunchOptionsDialog_3);
		combo_1.add(Messages.LaunchOptionsDialog_4);
		combo_1.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				selectedDebugMode = DebugMode.values()[combo_1.getSelectionIndex()];
			}

		});
		combo_1.select(1);

		Label lblUsePortNumber = new Label(grpDebugging, SWT.NONE);
		lblUsePortNumber.setText(Messages.LaunchOptionsDialog_5);

		final Spinner spinner = new Spinner(grpDebugging, SWT.BORDER);
		spinner.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		spinner.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				selectedDebugPort = spinner.getSelection();
			}

		});
		spinner.setMinimum(1024);
		spinner.setMaximum(65535);
		spinner.setSelection(8000);
		selectedDebugMode = DebugMode.Debug;
		selectedDebugPort = 8000;
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite composite = (Composite) super.createDialogArea(parent);
		setTitle(Messages.LaunchOptionsDialog_6);
		setMessage(Messages.LaunchOptionsDialog_7);

		if (getTitleImageLabel() != null) {
			getTitleImageLabel().setVisible(false);
		}

		createInterface(composite);
		Dialog.applyDialogFont(composite);
		populateWorkspaceList();
		workspaceCombo.setFocus();
		return composite;
	}

	/**
	 * Creates UI for allowing the user to enforce normal size fonts
	 *
	 * @param parent
	 *            the root container
	 */
	private void createFontGroup(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
		GridData gd_container = new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1);
		gd_container.horizontalIndent = -6;
		container.setLayoutData(gd_container);
		GridLayout layout = new GridLayout(2, false);
		layout.horizontalSpacing = 0;
		container.setLayout(layout);

		// Button for disabling font size reduction
		final Button button = new Button(container, SWT.CHECK);
		button.setText(Messages.LaunchOptionsDialog_8);
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				disableSmallFonts = button.getSelection();
			}

		});
		button.setSelection(false);
		Label label = new Label(container, SWT.NONE);
		label.setText(Messages.LaunchOptionsDialog_9);
		label.setFont(JFaceResources.getFontRegistry().getItalic("")); //$NON-NLS-1$

		// Button for disabling font size reduction
		final Button button2 = new Button(container, SWT.CHECK);
		button2.setText(Messages.LaunchOptionsDialog_10);
		button2.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				clean = button2.getSelection();
			}

		});
		button.setSelection(false);
		Label label2 = new Label(container, SWT.NONE);
		label2.setText("-clean"); //$NON-NLS-1$
		label2.setFont(JFaceResources.getFontRegistry().getItalic("")); //$NON-NLS-1$
	}

	protected Control createInterface(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
		GridLayout gl_container_2 = new GridLayout(3, false);
		container.setLayout(gl_container_2);
		container.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		container.setFont(parent.getFont());

		createWorkspaceOptionsGroup(container);
		createJreGroup(container);
		createJreMemoryGroup(container);
		createDebugOptionsGroup(container);
		createFontGroup(container);

		return container;
	}

	/**
	 * Creates UI for allowing the user to specify which JVM to use
	 *
	 * @param parent
	 *            the root container
	 */
	private void createJreGroup(Composite parent) {
		Label lblWorkspace = new Label(parent, SWT.NONE);
		lblWorkspace.setText(Messages.LaunchOptionsDialog_15);

		runtimeCombo = new Combo(parent, SWT.READ_ONLY);
		runtimeCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
		try {
			runtimes = LauncherPlugin.getDefault().getJavaRuntimeEnvironments();
			for (JRE jre : runtimes) {
				runtimeCombo.add(jre.toString());
			}
			runtimeCombo.select(0);
		} catch (Exception e) {
			runtimeCombo.setText("Could not locate Java Runtime Environments"); //$NON-NLS-1$
			IStatus newStatus = new Status(IStatus.ERROR, LauncherPlugin.PLUGIN_ID,
					"Could not locate Java Runtime Environments.", e); //$NON-NLS-1$
			StatusManager.getManager().handle(newStatus);
		}
		runtimeCombo.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				selectedJRE = runtimes.get(runtimeCombo.getSelectionIndex());
			}
		});
		// Use the first JRE as default
		selectedJRE = runtimes.get(0);
	}

	private void createJreMemoryGroup(Composite parent) {
		Group grpHeapSpaceAllocation = new Group(parent, SWT.NONE);
		grpHeapSpaceAllocation.setText(Messages.LaunchOptionsDialog_grpHeapSpaceAllocation_text);
		grpHeapSpaceAllocation.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 3, 1));
		grpHeapSpaceAllocation.setLayout(new GridLayout(4, false));

		// Maximum
		Label lbl_mx = new Label(grpHeapSpaceAllocation, SWT.NONE);
		lbl_mx.setText(Messages.LaunchOptionsDialog_11);
		lbl_mx.setLayoutData(new GridData(GridData.BEGINNING));
		final Text mx = new Text(grpHeapSpaceAllocation, SWT.BORDER);
		mx.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		mx.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				selectedXmx = mx.getText();
			}
		});
		mx.setText("512m"); //$NON-NLS-1$

		// Minimum
		Label lbl_ms = new Label(grpHeapSpaceAllocation, SWT.NONE);
		lbl_ms.setText(Messages.LaunchOptionsDialog_13);
		lbl_ms.setLayoutData(new GridData(GridData.BEGINNING));
		final Text ms = new Text(grpHeapSpaceAllocation, SWT.BORDER);
		ms.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		ms.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				selectedXms = ms.getText();
			}
		});
		ms.setText("40m"); //$NON-NLS-1$
	}

	private void createWorkspaceOptionsGroup(Composite container) {
		Label lblWorkspace = new Label(container, SWT.NONE);
		lblWorkspace.setText(Messages.LaunchOptionsDialog_16);

		workspaceCombo = new Combo(container, SWT.NONE);
		workspaceCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		workspaceCombo.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				Button okButton = getButton(Window.OK);
				if (okButton != null && !okButton.isDisposed()) {
					boolean nonWhitespaceFound = false;
					String characters = workspaceCombo.getText();
					for (int i = 0; !nonWhitespaceFound && i < characters.length(); i++) {
						if (!Character.isWhitespace(characters.charAt(i))) {
							nonWhitespaceFound = true;
						}
					}
					okButton.setEnabled(nonWhitespaceFound);
				}
				selectedWorkspace = workspaceCombo.getText();
			}
		});

		Button btnNewButton = new Button(container, SWT.NONE);
		btnNewButton.setText(Messages.LaunchOptionsDialog_17);
		btnNewButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		btnNewButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				DirectoryDialog fd = new DirectoryDialog(getShell(), SWT.OPEN);
				String open = fd.open();
				if (open != null) {
					workspaceCombo.setText(open);
				}
			}
		});
	}

	public DebugMode getDebugMode() {
		return selectedDebugMode;

	}

	public int getDebugPort() {
		return selectedDebugPort;
	}

	/**
	 * Return the initial size of the dialog.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(500, 400);
	}

	public JRE getJVm() {
		return selectedJRE;
	}

	/**
	 * Get the workspace location from the widget.
	 *
	 * @return the path to the workspace location as a string
	 */
	public String getWorkspaceLocation() {
		return selectedWorkspace;
	}

	public String getXms() {
		return selectedXms;
	}

	public String getXmx() {
		return selectedXmx;
	}

	public boolean isDisableSmallFonts() {
		return disableSmallFonts;
	}

	/**
	 * @since 2.1
	 */
	public boolean isClean() {
		return clean;
	}

	private void populateWorkspaceList() {
		final ChooseWorkspaceData data = new ChooseWorkspaceData(Platform.getInstanceLocation().getURL());
		data.readPersistedData();
		String current = data.getInitialDefault();
		String[] workspaces = data.getRecentWorkspaces();
		for (int i = 0; i < workspaces.length; i++) {
			if (workspaces[i] != null && !workspaces[i].equals(current)) {
				workspaceCombo.add(workspaces[i]);
			}
		}
		workspaceCombo.select(0);

	}

}
