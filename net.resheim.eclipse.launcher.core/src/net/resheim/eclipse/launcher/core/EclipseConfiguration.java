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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * This type is used to read an Eclipse configuration file (eclipse.ini) and add
 * to or modify settings found therein. This can be used when creating a set of
 * command line arguments for starting Eclipse. Typically the ini-file is
 * loaded, modified and the final result used as command line arguments. This
 * type only as API for modifying certain parts of the <i>eclipse.ini</i>
 * configuration.
 *
 * @since 1.0
 */
public class EclipseConfiguration {

	/** List of virtual machine start-up arguments */
	private Set<String> vmargs;

	/**
	 * Creates a new configuration instance from the contents of the file.
	 *
	 * @param in
	 *            the *.ini file input stream
	 * @throws IOException
	 */
	public EclipseConfiguration(InputStream in) throws IOException {
		readVmArgs(in);
	}

	/**
	 * Returns a list of virtual machine arguments as read from the Eclipse
	 * configuration file. Duplicates are removed.
	 *
	 * @return a list of -vm arguments
	 * @throws IOException
	 */
	public Set<String> getVmArgs() throws IOException {
		return vmargs;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (String string : vmargs) {
			sb.append(string);
			sb.append("\n"); //$NON-NLS-1$
		}
		return sb.toString();
	}

	private void readVmArgs(InputStream is) throws IOException {
		vmargs = new LinkedHashSet<>();
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		String in = null;
		boolean record = false;
		while ((in = br.readLine()) != null) {
			in = in.trim();
			if (record) {
				vmargs.add(in);
			}
			if ("-vmargs".equals(in)) { //$NON-NLS-1$
				record = true;
			}
		}
	}

	/**
	 * Remove the specified virtual machine argument.
	 *
	 * @param argument
	 *            the argument to remove
	 */
	public void removeVmSetting(String argument) {
		Set<String> new_vmargs = new LinkedHashSet<>();
		for (String string : vmargs) {
			if (!string.equals(argument)) {
				new_vmargs.add(string);
			}
		}
		vmargs = new_vmargs;
	}

	private void setVmMemory(String size, boolean max) {
		String key = "-Xm" + (max ? "x" : "s"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

		boolean replaced = false;
		Set<String> new_vmargs = new LinkedHashSet<>();
		for (String string : vmargs) {
			if (string.startsWith(key)) {
				new_vmargs.add(key + size);
				replaced = true;
			} else {
				new_vmargs.add(string);
			}
		}
		if (!replaced) {
			new_vmargs.add(key + size);
		}
		vmargs = new_vmargs;
	}

	/**
	 * Specifies the -Xms virtual machine argument
	 *
	 * @param size
	 *            the amount of memory to use
	 */
	public void setVmXms(String size) {
		setVmMemory(size, false);
	}

	/**
	 * Specifies the -Xmx virtual machine argument
	 *
	 * @param string
	 *            the amount of memory to use
	 */
	public void setVmXmx(String string) {
		setVmMemory(string, true);
	}

	public void setVmArgument(String key, String value) {
		boolean replaced = false;
		Set<String> new_vmargs = new LinkedHashSet<>();
		for (String string : vmargs) {
			if (string.startsWith(key)) {
				new_vmargs.add(key + "=" + value); //$NON-NLS-1$
				replaced = true;
			} else {
				new_vmargs.add(string);
			}
		}
		if (!replaced) {
			new_vmargs.add(key + "=" + value); //$NON-NLS-1$
		}
		vmargs = new_vmargs;
	}

	/**
	 * Specifies remote debug parameters.
	 *
	 * @param port
	 *            port number to use
	 * @param suspend
	 *            whether or not to suspend at startup
	 */
	public void setRemoteDebug(int port, boolean suspend) {
		Set<String> new_vmargs = new LinkedHashSet<>();
		// Remove previous argument if present
		for (String string : vmargs) {
			if (!string.startsWith("-Xrunjdwp:transport")) { //$NON-NLS-1$
				new_vmargs.add(string);
			}
		}
		new_vmargs.add("-Xdebug"); //$NON-NLS-1$
		StringBuilder sb = new StringBuilder();
		sb.append("-Xrunjdwp:transport=dt_socket,address="); //$NON-NLS-1$
		sb.append(port);
		sb.append(",server=y,suspend="); //$NON-NLS-1$
		sb.append(suspend ? "y" : "n"); //$NON-NLS-1$ //$NON-NLS-2$
		new_vmargs.add(sb.toString());
		vmargs = new_vmargs;
	}
}
