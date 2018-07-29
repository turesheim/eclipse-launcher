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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.eclipse.finder.waits.Conditions;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(SWTBotJunit4ClassRunner.class)
public class LaunchOptionsDialogTest {

	private static final int RADIUS = 16;
	private static SWTWorkbenchBot bot;
	/** Location for documentation screenshots */
	private static File screenshotsDir;

	@BeforeClass
	public static void beforeClass() {
		bot = new SWTWorkbenchBot();
		String screenshots = System.getProperty("screenshots");
		if (screenshots == null) {
			screenshots = "screenshots";
		}
		screenshotsDir = new File(screenshots);
		if (!screenshotsDir.exists()) {
			screenshotsDir.mkdirs();
		}
	}

	@Test
	public void canOpenAdvancedDialog() throws Exception {
		// Activate the menu item
		bot.menu("File").menu("Open Workspace").menu("Advanced...").click();
		bot.waitUntil(Conditions.shellIsActive("Open Workspace"), 2000);

		SWTBotShell shell = bot.shell("Open Workspace");

		// Set the workspace path
		File workspace = new File("/Users/Nescio/Eclipse/Workspace");
		bot.comboBoxWithLabel("Workspace:").setText(workspace.getAbsolutePath());

		// Should be at least one Java Runtime and it should also be Java 8
		String[] items = bot.comboBoxWithLabel("Java Runtime:").items();
		assertTrue(items.length > 0);

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
				// Grab a screenshot of the dialog shell
				final Rectangle b = shell.getBounds();
				final Image screenshot = new Image(shell.getDisplay(), b.width, b.height);
				GC gc = new GC(shell.getDisplay());
				gc.copyArea(screenshot, b.x, b.y);

				// Create drop shadow image
				final Image image = new Image(shell.getDisplay(), b.width + RADIUS, b.height + RADIUS);
				GC gc2 = new GC(image);
				fillRoundRectangleDropShadow(gc2, image.getBounds(), RADIUS);
				gc2.drawImage(screenshot, RADIUS / 2, RADIUS / 2);

				String text = shell.getText().replace(' ', '_') + ".png";
				Path path = Paths.get(screenshotsDir.getAbsolutePath(), text);
				ImageLoader loader = new ImageLoader();
				try {
					// overwrite the existing file if different
					if (path.toFile().exists()) {
						try {
							loader.load(Files.newInputStream(path, StandardOpenOption.READ));
							Image original = new Image(shell.getDisplay(), loader.data[0]);

							if (!original.getImageData().equals(image.getImageData())) {
								loader.data = new ImageData[] { image.getImageData() };
								loader.save(Files.newOutputStream(path, StandardOpenOption.WRITE), SWT.IMAGE_PNG);
							}
						} catch (SWTException e) {
							// probably broken image file, just continue and
							// overwrite it
						}
						return;
					}
					loader.data = new ImageData[] { image.getImageData() };
					loader.save(Files.newOutputStream(path, StandardOpenOption.WRITE), SWT.IMAGE_PNG);
					gc.dispose();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
	}

	public static void fillRoundRectangleDropShadow(GC gc, Rectangle bounds, int radius) {
		gc.setAdvanced(true);
		gc.setAntialias(SWT.ON);
		gc.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_BLACK));
		gc.setAlpha(0x8f / radius);
		for (int i = 0; i < radius; i++) {
			Rectangle shadowBounds = new Rectangle(bounds.x + i, bounds.y + i, bounds.width - (i * 2),
					bounds.height - (i * 2));
			gc.fillRoundRectangle(shadowBounds.x, shadowBounds.y, shadowBounds.width, shadowBounds.height, radius * 2,
					radius * 2);
		}
		gc.setAlpha(0xff);
	}
}