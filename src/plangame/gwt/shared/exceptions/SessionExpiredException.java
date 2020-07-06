/**
 * @file SessionExpiredException.java
 * @brief [brief description]
 *
 * This file is created at Almende B.V. It is open-source software and part of the Common
 * Hybrid Agent Platform (CHAP). A toolbox with a lot of open-source tools, ranging from
 * thread pools and TCP/IP components to control architectures and learning algorithms.
 * This software is published under the GNU Lesser General Public license (LGPL).
 *
 * Copyright � 2012 Joris Scharpff <joris@almende.com>
 *
 * @author       Joris Scharpff
 * @date         11 sep. 2012
 * @project      NGI
 * @company      Almende B.V.
 */
package plangame.gwt.shared.exceptions;



/**
 * @author Joris Scharpff
 */
public class SessionExpiredException extends Exception {
	private static final long serialVersionUID = 1L;

	/**
	 * Creates new session expired exception
	 */
	public SessionExpiredException( ) {
		super( );
	}
	
	/**
	 * Creates a new session expired exception
	 * 
	 * @param msg The error message
	 */
	public SessionExpiredException( String msg ) {
		super( msg );
	}
}
