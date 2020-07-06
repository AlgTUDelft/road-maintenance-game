/**
 * @file Dialog.java
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
 * @date         5 jan. 2013
 * @project      NGI
 * @company      Almende B.V.
 */
package plangame.gwt.client.widgets.dialogs;



/**
 * Custom DialogBox implementation using OK and Cancel callbacks
 *
 * @author Joris Scharpff
 * @param <T> The object that is returned by the dialog
 */
public abstract class ObjectDialog<T> extends Dialog {
	/** The dialog handler */
	protected DialogHandler<T> handler;
	

	/**
	 * Creates a new dialog
	 * 
	 * @param title The dialog title
	 * @param handler The dialog callback handler
	 */
	public ObjectDialog( String title, DialogHandler<T> handler ) {
		super( title );
		
		this.handler = handler;
	}
	
	/**
	 * Fires dialog OK event, hides the dialog
	 * 
	 * @param result The object resulting from the dialog
	 */
	protected void OK( T result ) {
		hide( );
		handler.OK( result );
		handler = null;
	}
	
	/**
	 * Fires cancel event, hides the dialog
	 */
	protected void Cancel( ) {
		hide( );
		handler.Cancel( );
		handler = null;
	}
}
