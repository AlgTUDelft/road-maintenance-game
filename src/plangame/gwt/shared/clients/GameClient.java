/**
 * @file GameClient.java
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
 * @date         17 mrt. 2013
 * @project      NGI
 * @company      Almende B.V.
 */
package plangame.gwt.shared.clients;

import plangame.model.object.BasicID;

/**
 * Base class for all clients that can play in a game
 *
 * @author Joris Scharpff
 */
@SuppressWarnings("serial")
public abstract class GameClient extends Client {
	/** The ID of the game server it is connected to */
	protected BasicID gameID;
	
	/** Empty constructor for GWT RPC */
	@Deprecated protected GameClient( ) { }
	
	/**
	 * Creates a new game client
	 * 
	 * @param ID The client ID
	 * @param name The client's descriptive name
	 * @param type The type of client
	 */
	public GameClient( BasicID ID, ClientType type ) {
		super( ID, type );
		
		gameID = null;
	}
	
	/**
	 * Updates the client to match the specified client
	 * 
	 * @see plangame.gwt.shared.clients.Client#update(plangame.gwt.shared.clients.Client)
	 */
	@Override
	public void update( Client client ) {
		assert (client instanceof GameClient) :"Invalid client type in update";
		
		gameID = ((GameClient)client).getGameID( );
	}

	/**
	 * Sets the game ID of the client
	 * 
	 * @param gameID The new game ID
	 */
	public void setGameID( BasicID gameID ) {
		this.gameID = gameID;
	}
	
	/**
	 * @return The ID of the game that the client is connected to or null if
	 * not connected yet
	 */
	public BasicID getGameID( ) {
		return gameID;
	}
}
