/**
 * @file EventException.java
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
 * @date         28 mrt. 2013
 * @project      NGI
 * @company      Almende B.V.
 */
package plangame.gwt.shared.exceptions;

/**
 * Event handling related errors
 *
 * @author Joris Scharpff
 */
@SuppressWarnings("serial")
public class EventException extends Exception {

	/** Creates an empty exception */
	public EventException( ) { super( ); }
	
	/**
	 * Creates an Event Exception with the specified message
	 *
	 * @param msg The error message
	 */
	public EventException( String msg ) { super( msg ); }
}
