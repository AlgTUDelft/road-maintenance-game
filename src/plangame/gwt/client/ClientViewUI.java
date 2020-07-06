/**
 * @file ClientViewUI.java
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
 * @date         18 mrt. 2013
 * @project      NGI
 * @company      Almende B.V.
 */
package plangame.gwt.client;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

/**
 * Base class for all view UIs
 *
 * @author Joris Scharpff
 */
public abstract class ClientViewUI extends Composite {
	/** The client view associated with this UI */
	private ClientView clientview;

	/**
	 * Creates a new client view widget for managed by the specified view.
	 * 
	 * @param clientview The client view managing the UI
	 */
	public ClientViewUI( ClientView clientview ) {
		this.clientview = clientview;
		
		// initialise the widget
		initWidget( initUI( ) );
	}
	
	/**
	 * Initialises the view UI
	 * 
	 * @return The widget to initialise the view with
	 */
	protected abstract Widget initUI( );
	
	/**
	 * @return The client view associated with the UI
	 */
	protected ClientView getView( ) {
		return clientview;
	}
}
