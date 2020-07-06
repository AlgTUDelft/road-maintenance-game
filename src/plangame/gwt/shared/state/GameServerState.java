/**
 * @file ServerState.java
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
 * @date         27 nov. 2012
 * @project      NGI
 * @company      Almende B.V.
 */
package plangame.gwt.shared.state;

import java.io.Serializable;

import plangame.gwt.shared.clients.SPClient;
import plangame.gwt.shared.enums.ClientState;
import plangame.gwt.shared.enums.GameState;
import plangame.model.object.ObjectMap;

/**
 * The current server state
 *
 * @author Joris Scharpff
 */
@SuppressWarnings ("serial" )
public class GameServerState implements Serializable {
	/** The current execution state */
	protected GameState state;
	
	/** The client states */
	protected ObjectMap<SPClient, ClientState> clients;
	
	/** Empty constructor for GWT RPC */
	@Deprecated protected GameServerState( ) { }
	
	/**
	 * Constructs a new server state
	 * 
	 * @param gameID The game server ID sending the response
	 * @param serverinfo The server information
	 * @param state The current game state
	 * @param clients The client states
	 */
	public GameServerState( GameServerInfo serverinfo, GameState state, ObjectMap<SPClient, ClientState> clients ) {
		this.state = state;
		this.clients = clients;
	}
	
	/** @return The current game state */
	public GameState getGameState( ) { return state; }
	
	/** @return The client states */
	public ObjectMap<SPClient, ClientState> getClientStates( ) { return clients; }
}
