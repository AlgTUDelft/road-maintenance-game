/**
 * @file EventTimeOutException.java
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
 * @date         7 jan. 2013
 * @project      NGI
 * @company      Almende B.V.
 */
package plangame.gwt.server.events;

/**
 * Exception thrown when waiting for a client to process events times out, to
 * prevent deadlocks.
 *
 * @author Joris Scharpff
 */
@SuppressWarnings("serial")
public class EventTimeOutException extends Exception {
	/**
	 * Creates a new exception
	 */
	public EventTimeOutException( ) {
		super( );
	}
	
	/**
	 * Creates the exception with the specified message
	 * 
	 * @param msg The message
	 */
	public EventTimeOutException( String msg ) {
		super( msg );
	}
}
