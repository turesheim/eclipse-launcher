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
 * @since 2.0
 */
public class LaunchException extends RuntimeException {

	private static final long serialVersionUID = -6565582263302816896L;

	public LaunchException() {
		super();
	}

	public LaunchException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public LaunchException(String message, Throwable cause) {
		super(message, cause);
	}

	public LaunchException(String message) {
		super(message);
	}

	public LaunchException(Throwable cause) {
		super(cause);
	}

}
