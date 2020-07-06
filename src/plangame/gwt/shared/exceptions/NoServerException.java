/**
 * @file ServerException.java
 * @brief Short description of file
 *
 * This file is created at Almende B.V. It is open-source software and part of the Common
 * Hybrid Agent Platform (CHAP). A toolbox with a lot of open-source tools, ranging from
 * thread pools and TCP/IP components to control architectures and learning algorithms.
 * This software is published under the GNU Lesser General Public license (LGPL).
 *
 * Copyright © 2012 Joris Scharpff <joris@almende.com>
 *
 * @author       Joris Scharpff
 * @date         10 dec. 2012
 * @project      NGI
 * @company      Almende B.V.
 */
package plangame.gwt.shared.exceptions;

/**
 * Exceptions thrown if there is no server running
 * 
 * @author Joris Scharpff
 */
@SuppressWarnings ("serial" )
public class NoServerException extends Exception {

	/**
	 * Creates empty exception
	 */
	public NoServerException( ) {
		super( );
	}
	
	/**
	 * Creates exception with the specified message
	 * 
	 * @param message The exception description
	 */
	public NoServerException( String message ) {
		super( message );
	}
}
