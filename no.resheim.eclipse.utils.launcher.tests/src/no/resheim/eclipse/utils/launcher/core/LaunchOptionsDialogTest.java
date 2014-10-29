/*******************************************************************************
 * Copyright (c) 2014 Torkild U. Resheim
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Torkild U. Resheim - initial API and implementation
 *******************************************************************************/

package no.resheim.eclipse.utils.launcher.core;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(SWTBotJunit4ClassRunner.class)
public class LaunchOptionsDialogTest {

	private static SWTWorkbenchBot bot;

	@BeforeClass
	public static void beforeClass() {
		bot = new SWTWorkbenchBot();
	}

	@Test
	public void canOpenAdvancedDialog() throws IOException {
		bot.menu("File").menu("Open Workspace").menu("Advanced...").click();
		SWTBotShell shell = bot.shell("Open Workspace");
		shell.activate();

		File workspace = File.createTempFile("eclipse-launcher", "workspace");
		bot.comboBoxWithLabel("Workspace:").setText(workspace.getAbsolutePath());
		// Should be at least one Java Runtime and it should also be Java 8
		String[] items = bot.comboBoxWithLabel("Java Runtime:").items();
		assertTrue(items.length > 0);
		assertTrue(items[0].contains("Java SE 8"));
	}

	@AfterClass
	public static void sleep() {
		bot.sleep(2000);
	}

}