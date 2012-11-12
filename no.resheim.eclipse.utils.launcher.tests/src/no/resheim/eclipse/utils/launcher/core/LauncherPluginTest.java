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

import java.util.ArrayList;

import org.junit.Assert;
import org.junit.Test;

public class LauncherPluginTest extends LauncherPlugin {

	public final String eclipse_commands = "<eclipse.commands>";

	public final String eclipse_commands_ws = "<eclipse.commands>\n-data\nmyworkspace\n";

	public final String eclipse_commands_vm = "<eclipse.commands>\n-vm\nmyvm\n";

	public final String eclipse_vmargs = "some\narguments\nfor\nthe\nvm";

	public final String eclipse_vm = "/path/to/vm";

	@Test
	public void testBuildCommandLine_NoWorkspace() throws Exception {
		ArrayList<String> args = LauncherPlugin.getDefault().buildCommandLine(
				null, eclipse_commands, null, null);
		Assert.assertEquals(args.size(),1);
		Assert.assertEquals(eclipse_commands,args.get(0));
	}

	@Test
	public void testBuildCommandLine_Workspace() throws Exception {
		ArrayList<String> args = LauncherPlugin.getDefault().buildCommandLine(
				"workspace", eclipse_commands, null, null);
		Assert.assertEquals(args.size(),3);
		Assert.assertEquals(eclipse_commands,args.get(0));
		Assert.assertEquals("-data",args.get(1));
		Assert.assertEquals("workspace",args.get(2));
	}

	/**
	 * The -data argument should be stripped from the eclipse.commands segment.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testBuildCommandLine_InheritedWorkspace() throws Exception {
		ArrayList<String> args = LauncherPlugin.getDefault().buildCommandLine(
				"workspace", eclipse_commands_ws, null, null);
		Assert.assertEquals(args.size(),3);
		Assert.assertEquals(eclipse_commands,args.get(0));
		Assert.assertEquals("-data",args.get(1));
		Assert.assertEquals("workspace",args.get(2));
	}

	@Test
	public void testBuildCommandLine_InheritedWorkspaceSpecified()
			throws Exception {
		ArrayList<String> args = LauncherPlugin.getDefault().buildCommandLine(
				null, eclipse_commands_ws, null, null);
		Assert.assertEquals(args.size(),1);
		Assert.assertEquals(eclipse_commands,args.get(0));
	}

	@Test
	public void testBuildCommandLine_Vm() throws Exception {
		ArrayList<String> args = LauncherPlugin.getDefault().buildCommandLine(
				null, eclipse_commands, null, eclipse_vm);
		Assert.assertEquals(args.size(),3);
		Assert.assertEquals(eclipse_commands,args.get(0));
		Assert.assertEquals("-vm",args.get(1));
		Assert.assertEquals(eclipse_vm,args.get(2));
	}

	/**
	 * The -vm argument should be stripped from the eclipse.commands segment.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testBuildCommandLine_InheritedVm() throws Exception {
		ArrayList<String> args = LauncherPlugin.getDefault().buildCommandLine(
				null, eclipse_commands_vm, null, eclipse_vm);
		Assert.assertEquals(args.size(),3);
		Assert.assertEquals(eclipse_commands,args.get(0));
		Assert.assertEquals("-vm",args.get(1));
		Assert.assertEquals(eclipse_vm,args.get(2));
	}

	@Test
	public void testBuildCommandLine_InheritedVmSpecified()
			throws Exception {
		ArrayList<String> args = LauncherPlugin.getDefault().buildCommandLine(
				null, eclipse_commands_vm, null, null);
		Assert.assertEquals(args.size(),3);
		Assert.assertEquals(eclipse_commands,args.get(0));
		Assert.assertEquals("-vm",args.get(1));
		Assert.assertEquals("myvm",args.get(2));
	}

	@Test
	public void testBuildCommandLine_VmArgs() throws Exception {
		ArrayList<String> args = LauncherPlugin.getDefault().buildCommandLine(
				null, eclipse_commands, eclipse_vmargs, null);
		Assert.assertEquals(args.size(),7);
		Assert.assertEquals(eclipse_commands,args.get(0));
		Assert.assertEquals("-vmargs",args.get(1));
		Assert.assertEquals("some",args.get(2));
	}
}
