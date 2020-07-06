/**
 * @file GameClient.java
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

import plangame.model.object.BasicID;
import plangame.model.object.BasicObject;


/**
 * Abstract class for all participating clients of the game
 * 
 * @author Joris Scharpff
 */
@SuppressWarnings ("serial" )
public abstract class Client extends BasicObject {	
	/** Client type */
	protected ClientType type;
	
	/**
	 * Possible client types
	 *
	 * @author Joris Scharpff
	 */
	@SuppressWarnings("javadoc")
	public enum ClientType {
		GameManager,
		ScoreBoard,
		ServiceProvider,
		ServerManager;
		
		/**
		 * @return The ID prefix used for this client
		 */
		public String getIDPrefix( ) {
			switch( this ) {
				case GameManager:
					return "GM";
					
				case ScoreBoard:
					return "SB";

				case ServiceProvider:
					return "SP";
					
				case ServerManager:
					return "SM";

				default:
					return null;
			}
		}
	}

	
	/** Empty constructor for GWT */
	@Deprecated protected Client( ) { }
	
	/**
	 * Creates a new client with the specified ID and name
	 * 
	 * @param ID The client ID
	 * @param name The client name
	 * @param type The client type
	 */
	public Client( BasicID ID, ClientType type ) {
		super( ID );
		
		this.type = type;
	}
	
	/**
	 * Updates the client info to match the specified client
	 * 
	 * @param client The game client
	 */
	public abstract void update( Client client );

	/**
	 * @return The client type
	 */
	public ClientType getClientType( ) { 
		return type;
	}
	
	/**
	 * DEBUG prints client information in console
	 */
	@Deprecated
	public void print( ) {
		System.out.println( "Client (" + type.toString( ) + ") [" + ID + "]: name: " + toString( ) );
	}
}
