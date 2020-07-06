/**
 * @file Callback.java
 * @brief Short description of file
 *
 * This file is created at Almende B.V. It is open-source software and part of the Common
 * Hybrid Agent Platform (CHAP). A toolbox with a lot of open-source tools, ranging from
 * thread pools and TCP/IP components to control architectures and learning algorithms.
 * This software is published under the GNU Lesser General Public license (LGPL).
 *
 * Copyright © 2013 Joris Scharpff <joris@almende.com>
 *
 * @author       Joris Scharpff
 * @date         12 jun. 2013
 * @project      NGI
 * @company      Almende B.V.
 */
package plangame.gwt.client.util;

/**
 * General callback function, mostly for callback after RPC response
 *
 * @author Joris Scharpff
 * @param <T> The callback type
 */
public abstract class Callback<T> {
	/**
	 * The callback function
	 * 
	 * @param result The result that should be passed on callback
	 */
	public abstract void call( final T result );
}
