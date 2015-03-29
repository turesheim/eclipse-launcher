/*******************************************************************************
 * Copyright (c) 2014-2015 Torkild U. Resheim.
 *
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Torkild U. Resheim - initial API and implementation
 *******************************************************************************/
package net.resheim.eclipse.launcher.core;

import java.util.List;

/**
 * Implementors detect and store information about <i>Java Runtime
 * Environments</i> (JRE) found on the host system.
 *
 * @author Torkild U. Resheim
 * @since 2.0
 */
public interface IJavaLocatorService {

	/**
	 * Gathers and returns a list of all Java Runtime Environments found on the
	 * host. If none are found, the list will be empty.
	 *
	 * @return a list of JRE's
	 */
	public List<JRE> getRuntimes();

}
