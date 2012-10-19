/*******************************************************************************
 * Copyright (c) 2012 Torkild U. Resheim and others
 * 
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: 
 *     Torkild U. Resheim - initial API and implementation
 *******************************************************************************/
package no.resheim.eclipse.utils.launcher.core;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Assert;
import org.junit.Test;

public class LauncherPluginTest extends LauncherPlugin {

	public final String eclipse_commands = "-command";

	public final String eclipse_commands_ws = "-command\n-data\nmyworkspace\n";

	public final String eclipse_vmargs = "-vmarg";

	@Test
	public void testBuildCommandLine_NoWorkspace() throws Exception {
		ArrayList<String> args = LauncherPlugin.getDefault().buildCommandLine(null, eclipse_commands, eclipse_vmargs);
		Assert.assertEquals(args.get(0), eclipse_commands);
		Assert.assertEquals(args.get(1), "-vmargs");
		Assert.assertEquals(args.get(2), eclipse_vmargs);
	}

	@Test
	public void testBuildCommandLine_Workspace() throws Exception {
		ArrayList<String> args = LauncherPlugin.getDefault().buildCommandLine("workspace", eclipse_commands, eclipse_vmargs);
		Assert.assertEquals(args.get(0), eclipse_commands);
		Assert.assertEquals(args.get(1), "-data");
		Assert.assertEquals(args.get(2), "workspace");
		Assert.assertEquals(args.get(3), "-vmargs");
		Assert.assertEquals(args.get(4), eclipse_vmargs);
	}

	@Test
	public void testBuildCommandLine_InheritedWorkspace() throws Exception {
		ArrayList<String> args = LauncherPlugin.getDefault().buildCommandLine("workspace", eclipse_commands_ws, eclipse_vmargs);
		Assert.assertEquals(args.get(0), eclipse_commands);
		Assert.assertEquals(args.get(1), "-data");
		Assert.assertEquals(args.get(2), "workspace");
		Assert.assertEquals(args.get(3), "-vmargs");
		Assert.assertEquals(args.get(4), eclipse_vmargs);
		fail("crap");
	}
}
