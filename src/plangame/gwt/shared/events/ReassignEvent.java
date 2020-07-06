/**
 * @file AcceptEvent.java
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
package plangame.gwt.shared.events;

import plangame.gwt.shared.clients.SPClient;

/**
 * Event that is send to all the clients to start the accept/decline round
 *
 * @author Joris Scharpff
 */
@SuppressWarnings("serial")
public class ReassignEvent extends Event {
	/** The client to be replaced, should match the receiver */
	protected SPClient oldclient;
	
	/** The new client that should be assigned */
	protected SPClient newclient;
	
	/** Empty constructor for GWT RPC */
	@Deprecated protected ReassignEvent( ) { }	
	
	/**
	 * Creates a new AssignEvent
	 * 
	 * @param oldclient The client to be replaced
	 * @param newclient The new client object
	 */
	public ReassignEvent( SPClient oldclient, SPClient newclient ) {
		super( );
		
		this.oldclient = oldclient;
		this.newclient = newclient;
	}

	/** @return The client to be replaced */
	public SPClient getOldClient( ) { return oldclient; }

	/** @return The client to replace with */
	public SPClient getNewClient( ) { return newclient; }

	/**
	 * @see plangame.gwt.shared.events.Event#getName()
	 */
	@Override
	public String getName( ) {
		return "Re-assign client event";
	}
}
