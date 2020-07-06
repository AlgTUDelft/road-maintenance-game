/**
 * @file Player.java
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
 * @date         7 sep. 2012
 * @project      NGI
 * @company      Almende B.V.
 */
package plangame.gwt.shared.clients;

import plangame.game.player.Player;
import plangame.model.exceptions.InvalidModelException;
import plangame.model.object.BasicID;




/**
 * Service Provider client
 * 
 * @author Joris Scharpff
 */
@SuppressWarnings ("serial" )
public class SPClient extends GameClient {

	/** The Player object that is linked to this GameClient */
	protected Player player;

	/** Empty constructor for GWT */
	@Deprecated protected SPClient( ) { }

	/**
	 * Creates the SP client with ID and name
	 * 
	 * @param ID The client ID
	 * @throws InvalidModelException if the name is invalid
	 */
	public SPClient( BasicID ID ) {
		super( ID, ClientType.ServiceProvider );
	}
	
	/**
	 * Updates the client info
	 * 
	 * @see plangame.gwt.shared.clients.Client#update(plangame.gwt.shared.clients.Client)
	 */
	@Override
	public void update( Client client ) {
		assert (client instanceof SPClient) : "Invalid client for update!";
		
		super.update( client );
		setPlayer( ((SPClient)client).getPlayer( ) );
	}
	

	/**
	 * Links the GameClient to a Player object of the running game
	 * 
	 * @param player The player to set for the client
	 */
	public void setPlayer( Player player ) {
		this.player = player;
	}
	
	/**
	 * @return The Player object that is linked to this GameClient
	 */
	public Player getPlayer( ) {
		return player;
	}

}
