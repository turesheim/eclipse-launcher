/*******************************************************************************
 * Copyright (c) 2012-2015 Torkild U. Resheim and others
 *
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Torkild U. Resheim - initial API and implementation
 *   Martin D'Aloia - Add support to use a path to the libjli.dylib as -vm argument
 *******************************************************************************/
package net.resheim.eclipse.launcher.core;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

/**
 * @since 2.0
 */
@SuppressWarnings("nls")
public class LauncherPluginTest extends LauncherPlugin {

	private static final String WORKSPACE = "workspace";

	/** eclipse.commands=<inherited commands> */
	private static final String ECLIPSE_COMMANDS = "<eclipse.commands>";

	/** eclipse.commands=<inherited commands> */
	private static final String ECLIPSE_COMMANDS_WS = "<eclipse.commands>\n-data\nmyworkspace\n";

	/** eclipse.commands=<inherited commands> */
	private static final String ECLIPSE_COMMANDS_VM = "<eclipse.commands>\n-vm\nmyvm\n";

	/** eclipse.vmargs=<arguments for the virtual machine> */
	private static final String ECLIPSE_VMARGS = "some\narguments\nfor\nthe\nvm";

	/** <path to the virtual machine> */
	private static final String ECLIPSE_VM = "/path/to/vm";

	/** <full path to the virtual machine> */
	private static final String ECLIPSE_VM_FULL = "/path/to/vm/Contents/Home/bin/java";

	/** <full path to the virtual machine library> */
	private static final String ECLIPSE_VM_FULL_JVM_LIB = "/path/to/vm/Contents/Home/jre/lib/server/libjvm.dylib";

	/** <full path to the launcher interface library> */
	private static final String ECLIPSE_VM_FULL_JLI_LIB = "/path/to/vm/Contents/Home/jre/lib/server/libjli.dylib";

	/** <full path to the virtual machine> */
	private static final String ECLIPSE_VM_HOME = "/path/to/vm/Contents/Home";

	/**
	 * Test to verify that the command line is correctly built when only
	 * commands have been submitted.
	 */
	@Test
	public void testBuildCommandLineWithNoWorkspace() {
		LauncherPlugin.getDefault();
		List<String> args = LauncherPlugin.buildCommandLine(null, ECLIPSE_COMMANDS, null, null);
		Assert.assertEquals(1, args.size());
		Assert.assertEquals(ECLIPSE_COMMANDS, args.get(0));
	}

	/**
	 * Test to verify that the command line is correctly built when commands and
	 * workspace has been specified.
	 */
	@Test
	public void testBuildCommandLineWithWorkspace() {
		LauncherPlugin.getDefault();
		List<String> args = LauncherPlugin.buildCommandLine(WORKSPACE, ECLIPSE_COMMANDS, null, null);
		Assert.assertEquals(3, args.size());
		Assert.assertEquals(ECLIPSE_COMMANDS, args.get(0));
		Assert.assertEquals("-data", args.get(1));
		Assert.assertEquals(WORKSPACE, args.get(2));
	}

	/**
	 * Test to verify that the command line is correctly built when original
	 * workspace has been specified at the command line and a new workspace is
	 * also specified. The -data argument must be stripped from the
	 * eclipse.commands segment and a new "-data workspace" is replacing it.
	 */
	@Test
	public void testBuildCommandLineWithInheritedWorkspace() {
		LauncherPlugin.getDefault();
		List<String> args = LauncherPlugin.buildCommandLine(WORKSPACE, ECLIPSE_COMMANDS_WS, null, null);
		Assert.assertEquals(3, args.size());
		Assert.assertEquals(ECLIPSE_COMMANDS, args.get(0));
		Assert.assertEquals("-data", args.get(1));
		Assert.assertEquals(WORKSPACE, args.get(2));
	}

	@Test
	public void testBuildCommandLineWithInheritedWorkspaceSpecified() {
		LauncherPlugin.getDefault();
		List<String> args = LauncherPlugin.buildCommandLine(null, ECLIPSE_COMMANDS_WS, null, null);
		Assert.assertEquals(1, args.size());
		Assert.assertEquals(ECLIPSE_COMMANDS, args.get(0));
	}

	@Test
	public void testBuildCommandLineWithVm() {
		LauncherPlugin.getDefault();
		List<String> args = LauncherPlugin.buildCommandLine(null, ECLIPSE_COMMANDS, null, ECLIPSE_VM);
		Assert.assertEquals(3, args.size());
		Assert.assertEquals("-vm", args.get(0));
		Assert.assertEquals(ECLIPSE_VM_FULL, args.get(1));
		Assert.assertEquals(ECLIPSE_COMMANDS, args.get(2));
	}

	@Test
	public void testBuildCommandLineWithVmHome() {
		LauncherPlugin.getDefault();
		List<String> args = LauncherPlugin.buildCommandLine(null, ECLIPSE_COMMANDS, null, ECLIPSE_VM_HOME);
		Assert.assertEquals(3, args.size());
		Assert.assertEquals("-vm", args.get(0));
		Assert.assertEquals(ECLIPSE_VM_FULL, args.get(1));
		Assert.assertEquals(ECLIPSE_COMMANDS, args.get(2));
	}

	@Test
	public void testBuildCommandLineWithVmFull() {
		LauncherPlugin.getDefault();
		List<String> args = LauncherPlugin.buildCommandLine(null, ECLIPSE_COMMANDS, null, ECLIPSE_VM_FULL);
		Assert.assertEquals(3, args.size());
		Assert.assertEquals("-vm", args.get(0));
		Assert.assertEquals(ECLIPSE_VM_FULL, args.get(1));
		Assert.assertEquals(ECLIPSE_COMMANDS, args.get(2));
	}

	/**
	 * The -vm argument should be stripped from the eclipse.commands segment.
	 *
	 * @throws Exception
	 */
	@Test
	public void testBuildCommandLineWithInheritedVm() {
		LauncherPlugin.getDefault();
		List<String> args = LauncherPlugin.buildCommandLine(null, ECLIPSE_COMMANDS_VM, null, ECLIPSE_VM);
		Assert.assertEquals(3, args.size());
		Assert.assertEquals("-vm", args.get(0));
		Assert.assertEquals(ECLIPSE_VM_FULL, args.get(1));
		Assert.assertEquals(ECLIPSE_COMMANDS, args.get(2));
	}

	/**
	 * The "-vm" option was specified in arguments and must be replaced.
	 *
	 * @throws Exception
	 */
	@Test
	public void testBuildCommandLineWithInheritedVmSpecified() {
		LauncherPlugin.getDefault();
		List<String> args = LauncherPlugin.buildCommandLine(null, ECLIPSE_COMMANDS_VM, null, null);
		Assert.assertEquals(3, args.size());
		Assert.assertEquals("-vm", args.get(0));
		Assert.assertEquals("myvm/Contents/Home/bin/java", args.get(1));
		Assert.assertEquals(ECLIPSE_COMMANDS, args.get(2));
	}

	@Test
	public void testBuildCommandLineWithVmArgs() {
		LauncherPlugin.getDefault();
		List<String> args = LauncherPlugin.buildCommandLine(null, ECLIPSE_COMMANDS, ECLIPSE_VMARGS, null);
		Assert.assertEquals(7, args.size());
		Assert.assertEquals(ECLIPSE_COMMANDS, args.get(0));
		Assert.assertEquals("-vmargs", args.get(1));
		Assert.assertEquals("some", args.get(2));
	}

	@Test
	public void testBuildCommandLineWithJVMDylib() {
		LauncherPlugin.getDefault();
		List<String> args = LauncherPlugin.buildCommandLine(null, ECLIPSE_COMMANDS, null, ECLIPSE_VM_FULL_JVM_LIB);
		Assert.assertEquals(3, args.size());
		Assert.assertEquals("-vm", args.get(0));
		Assert.assertEquals(ECLIPSE_VM_FULL_JVM_LIB, args.get(1));
		Assert.assertEquals(ECLIPSE_COMMANDS, args.get(2));
	}

	@Test
	public void testBuildCommandLineWithJLIDylib() {
		LauncherPlugin.getDefault();
		List<String> args = LauncherPlugin.buildCommandLine(null, ECLIPSE_COMMANDS, null, ECLIPSE_VM_FULL_JLI_LIB);
		Assert.assertEquals(3, args.size());
		Assert.assertEquals("-vm", args.get(0));
		Assert.assertEquals(ECLIPSE_VM_FULL_JLI_LIB, args.get(1));
		Assert.assertEquals(ECLIPSE_COMMANDS, args.get(2));
	}

}
