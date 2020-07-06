/**
 * @file ServerOfflineException.java
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
 * @date         6 sep. 2012
 * @project      NGI
 * @company      Almende B.V.
 */
package plangame.gwt.shared.exceptions;



/**
 * Thrown whenever any operation on a client is performed but the client is noy
 * of the correct type for the operation 
 * 
 * @author Joris Scharpff
 */
@SuppressWarnings("serial")
public class InvalidClientTypeException extends Exception {

	/**
	 * Creates a new exception
	 */
	public InvalidClientTypeException( ) {
		super( );
	}
	
	/**
	 * Creates a new exception with the specified message
	 * 
	 * @param msg The error message
	 */
	public InvalidClientTypeException( String msg ) {
		super( msg );
	}
}
