/**
 * @file JoinResult.java
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
 * @date         23 nov. 2012
 * @project      NGI
 * @company      Almende B.V.
 */
package plangame.gwt.shared.gameresponse;

import plangame.gwt.shared.clients.Client;
import plangame.gwt.shared.state.GameServerInfo;
import plangame.gwt.shared.state.GameServerState;
import plangame.model.object.BasicID;

/**
 * Container for join game result
 *
 * @author Joris Scharpff
 */
@SuppressWarnings ("serial" )
public class JoinGameResponse extends GameResponse {
	/** The game server information */
	protected GameServerInfo serverinfo;

	/** The state of the server we are now connected to */
	protected GameServerState serverstate;
	
	/** The game client result from joining */
	protected Client client;
	
	/** Empty constructor for GWT RPC */
	@Deprecated protected JoinGameResponse( ) { }
	
	/**
	 * Creates a join game result
	 * 
	 * @param gameID The game ID
	 * @param serverinfo The info of the game server we joined
	 * @param serverstate The state of the server we connected to
	 * @param client The game client resulting from the join
	 */
	public JoinGameResponse( BasicID gameID, GameServerInfo serverinfo, GameServerState serverstate, Client client ) {
		super( gameID );
		
		this.serverinfo = serverinfo;
		this.serverstate = serverstate;
		this.client = client;
	}

	/** @return The game server info */
	public GameServerInfo getServerInfo( ) { return serverinfo; }
	
	/** @return The server state */
	public GameServerState getServerState( ) { return serverstate; }
	
	/** @return The game client */
	public Client getClient( ) { return client; }
}
