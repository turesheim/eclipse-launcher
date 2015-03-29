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

/**
 * Simple type that represents a <i>Java Runtime Environment</i> located on the
 * host computer.
 *
 * @since 2.0
 */
public class JRE {

	private String arch;

	private String name;

	private String path;

	private String version;

	private String vendor;

	public String getArch() {
		return arch;
	}

	public String getName() {
		return name;
	}

	public String getPath() {
		return path;
	}

	public String getVersion() {
		return version;
	}

	public void setArch(String arch) {
		this.arch = arch;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	@Override
	public String toString() {
		return name + " [" + version + "] (" + vendor + ")"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	public String getVendor() {
		return vendor;
	}

	public void setVendor(String vendor) {
		this.vendor = vendor;
	}
}
