/**
 * @file ConnectResponse.java
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
package plangame.gwt.shared.serverresponse;

import plangame.gwt.shared.clients.Client;
import plangame.gwt.shared.state.GameServerInfo;
import plangame.model.object.BasicID;

/**
 * Response when a client (re-)connects to the server
 *
 * @author Joris Scharpff
 */
@SuppressWarnings ("serial" )
public class ConnectResponse extends ServerResponse {	
	/** The resulting client object */
	protected Client client;
	
	/** The game server info (on reconnect) */
	protected GameServerInfo gameinfo;
	
	/** Empty constructor for GWT RPC */
	@Deprecated protected ConnectResponse( ) { }
	
	/**
	 * Creates a response to be send when a client connects to the server
	 * 
	 * @param serverID The ID of the server it connects to
	 * @param client The client objects that results
	 */
	public ConnectResponse( BasicID serverID, Client client ) {
		super( serverID );
		this.client = client;
		
		this.gameinfo = null;
	}

	/** @return The game client */
	public Client getClient( ) { return client; }
}
