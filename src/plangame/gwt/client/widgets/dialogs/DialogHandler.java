/**
 * @file DialogHandler.java
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
 * @date         25 sep. 2012
 * @project      NGI
 * @company      Almende B.V.
 */
package plangame.gwt.client.widgets.dialogs;


/**
 * Event handler for dialogs, cancel is implemented as empty function as its
 * usual function is to close the dialog. Can be overridden for custom cancel
 * operation.
 * 
 * @author Joris Scharpff
 * @param <T> The type of object that is returned by the handler
 */
public abstract class DialogHandler<T> {
	/**
	 * OK button is chosen
	 * 
	 * @param item The chosen item
	 */
	public abstract void OK( T item );
	
	/**
	 * The dialog is cancelled
	 */
	public void Cancel( ) { }
}
