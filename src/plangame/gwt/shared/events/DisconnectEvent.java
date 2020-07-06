/**
 * @file ServerEvent.java
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
 * @date         28 nov. 2012
 * @project      NGI
 * @company      Almende B.V.
 */
package plangame.gwt.shared.events;

import plangame.model.object.BasicID;

/**
 * Fired when a client is disconnected, e.g. because of restarts or kick
 *
 * @author Joris Scharpff
 */
@SuppressWarnings ("serial" )
public class DisconnectEvent extends Event {
	/** The ID of the client that is disconnected (for GM notification) */
	protected BasicID clientID; 
	
	/** The reason for disconnecting*/
	protected DisconnectReason reason;
	
	/** Disconnect event types */
	public enum DisconnectReason {
		/** The client requested a disconnect or closed the browser */
		ClientDisconnect,
		/** The server disconnects the client */
		ServerDisconnect,
		/** The game server to which the client was connected is restarting */
		Restart,
		/** The game server to which the client was connected in shutting down */
		Shutdown;
	}
	
	/** Empty constructor for GWT RPC */
	@Deprecated protected DisconnectEvent( ) { }

	/**
	 * Creates a new disconnect event
	 * 
	 * @param clientID The ID of the client to disconnect
	 * @param reason The reason
	 */
	public DisconnectEvent( BasicID clientID, DisconnectReason reason ) {
		super( );
		
		this.clientID = clientID;
		this.reason = reason;
	}
	
	/**
	 * @return The ID of the client that is disconnected
	 */
	public BasicID getClientID( ) {
		return clientID;
	}
	
	/**
	 * @return The disconnect reason
	 */
	public DisconnectReason getReason( ) {
		return reason;
	}
	
	/**
	 * @see plangame.gwt.shared.events.Event#getName()
	 */
	@Override
	public String getName( ) {
		return "Disconnect Event";
	}
}
