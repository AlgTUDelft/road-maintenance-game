/**
 * @file ExecuteEvent.java
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
 * @date         23 sep. 2012
 * @project      NGI
 * @company      Almende B.V.
 */
package plangame.gwt.shared.events;

import plangame.gwt.shared.clients.Client;


/**
 * @author Joris Scharpff
 */
@SuppressWarnings("serial")
public class JoinEvent extends Event {
	/** The client that is joining */
	protected Client client;
	
	/** Empty constructor for GWT RPW */
	@Deprecated protected JoinEvent( ) { }
	
	/**
	 * Creates a new join event
	 * 
	 * @param client The client joining the game
	 * @param portfolios The available portfolios
	 */
	public JoinEvent( Client client ) {
		super( );
		
		this.client = client;
	}

	/** @return The result container */
	public Client getClient( ) { return client; }

	/**
	 * @see plangame.gwt.shared.events.Event#getName()
	 */
	@Override
	public String getName( ) {
		return "Join Game";
	}
}
