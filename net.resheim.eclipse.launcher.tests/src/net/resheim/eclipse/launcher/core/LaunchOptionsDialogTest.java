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

package net.resheim.eclipse.launcher.core;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Shell;
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
	/** Location for documentation screenshots */
	private static File screenshotsDir;

	@BeforeClass
	public static void beforeClass() {
		bot = new SWTWorkbenchBot();
		String screenshots = System.getProperty("screenshots");
		screenshotsDir = new File(screenshots);
	}

	@Test
	public void canOpenAdvancedDialog() throws IOException {
		// Activate the menu item
		bot.menu("File").menu("Open Workspace").menu("Advanced...").click();
		SWTBotShell shell = bot.shell("Open Workspace");
		shell.activate();

		// Set the workspace path
		File workspace = new File("/Users/Nescio/Eclipse/Workspace");
		bot.comboBoxWithLabel("Workspace:").setText(workspace.getAbsolutePath());

		// Should be at least one Java Runtime and it should also be Java 8
		String[] items = bot.comboBoxWithLabel("Java Runtime:").items();
		assertTrue(items.length > 0);
		assertTrue(items[0].contains("Java SE 8"));

		// Take a screenshot for documentation
		takeScreenshot(shell.widget);
	}

	@AfterClass
	public static void sleep() {
		bot.sleep(1000);
	}

	/**
	 * Utility method for capturing a screenshot of a dialog or wizard window
	 * into a file.
	 * 
	 * @param shell
	 *            the dialog shell
	 * @param file
	 *            the file to save the image to
	 */
	private void takeScreenshot(final Shell shell) {
		shell.getDisplay().asyncExec(new Runnable() {
			@Override
			public void run() {
				final Rectangle b = shell.getBounds();
				String text = shell.getText().replace(' ', '_') + ".png";
				File file = new File(screenshotsDir, text);
				final Image image = new Image(shell.getDisplay(), b.width, b.height);
				GC gc = new GC(shell.getDisplay());
				gc.copyArea(image, b.x, b.y);

				ImageLoader loader = new ImageLoader();
				loader.load(file.getAbsolutePath());

				Image original = new Image(shell.getDisplay(), file.getAbsolutePath());

				if (!original.getImageData().equals(image.getImageData())) {
					loader.data = new ImageData[] { image.getImageData() };
					loader.save(file.getAbsolutePath(), SWT.IMAGE_PNG);
				}
				gc.dispose();
			}
		});
	}
}