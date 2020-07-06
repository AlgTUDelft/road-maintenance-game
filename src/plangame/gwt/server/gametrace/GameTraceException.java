/**
 * @file GameTraceException.java
 * @brief Short description of file
 *
 * This file is created at Almende B.V. It is open-source software and part of the Common
 * Hybrid Agent Platform (CHAP). A toolbox with a lot of open-source tools, ranging from
 * thread pools and TCP/IP components to control architectures and learning algorithms.
 * This software is published under the GNU Lesser General Public license (LGPL).
 *
 * Copyright © 2014 Joris Scharpff <joris@almende.com>
 *
 * @author       Joris Scharpff
 * @date         22 dec. 2014
 * @project      NGI
 * @company      Almende B.V.
 */
package plangame.gwt.server.gametrace;

/**
 *
 * @author Joris Scharpff
 */
@SuppressWarnings("serial")
public class GameTraceException extends Exception {
	/**
	 * Creates a new exception
	 * 
	 * @param msg The exception description
	 */
	public GameTraceException( String msg ) {
		super( msg );
	}
}
