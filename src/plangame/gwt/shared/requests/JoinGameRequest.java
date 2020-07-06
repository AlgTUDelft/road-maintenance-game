/**
 * @file JoinGameRequest.java
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
 * @date         9 mei 2013
 * @project      NGI
 * @company      Almende B.V.
 */
package plangame.gwt.shared.requests;

import plangame.model.object.BasicID;

/**
 * Request to join a game
 * 
 * @author Joris Scharpff
 */
@SuppressWarnings("serial")
public class JoinGameRequest extends ClientRequest {
	/** The ID of the game to join */
	protected BasicID gameID;
	
	/** The player name that the client chose */
	protected String playername;
	
	/** Empty constructor for GWT RPC */
	@Deprecated protected JoinGameRequest( ) { }
	
	/**
	 * Creates a new request to join a game server
	 * 
	 * @param clientID The ID of the requesting client
	 * @param gameID The ID of the game it wants to join
	 * @param playername The prefered player name
	 */
	public JoinGameRequest( BasicID clientID, BasicID gameID, String playername ) {
		super( clientID );
		
		this.gameID = gameID;
		this.playername = playername;
	}
	
	/**
	 * @return The ID of the game we want to join
	 */
	public BasicID getGameID( ) {
		return gameID;
	}
	
	/**
	 * @return The desired player name
	 */
	public String getPlayerName( ) {
		return playername;
	}
}
