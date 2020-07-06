/**
 * @file GameServerInfo.java
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
package plangame.gwt.shared.state;

import plangame.gwt.shared.GameServerConfig;
import plangame.model.object.BasicID;
import plangame.model.object.BasicObject;


/**
 * Result container for server information request
 * 
 * @author Joris Scharpff
 */
@SuppressWarnings ("serial" )
public class GameServerInfo extends BasicObject {
	/** The game server name */
	protected String name;
	
	/** The active game file description */
	protected String gamedesc;
	
	/** The active game file */
	protected String gamefile;
		
	/** The number of connected clients */
	protected int clients;
	
	/** Its configuration */
	protected GameServerConfig config;	
	
	/** Empty constructor for GWT RPC */
	@Deprecated protected GameServerInfo( ) {}
	
	/**
	 * Creates a new game server info object
	 * 
	 * @param ID The sending game server ID
	 * @param server The game server
	 * @param name The server name
	 * @param desc The game description
	 * @param gamefile The game file loaded for this game
	 * @param clients The number of connected clients
	 * @param config The game server configuration
	 */
	public GameServerInfo( BasicID ID, String name, String desc, String gamefile, int clients, GameServerConfig config ) {
		super( ID );
		
		this.ID = ID;
		this.name = name;
		this.gamedesc = desc;
		this.gamefile = gamefile;
		this.clients = clients;
		this.config = config;
	}
	
	/** @return The game server name */
	public String getName( ) { return name; }
	
	/** @return The activie game description */
	public String getGameDescription( ) { return gamedesc; }
	
	/** @return The active game name */
	public String getGameFile( ) { return gamefile; }
		
	/** @return The number of connected clients */
	public int getConnectedClients( ) { return clients; }

	/** @return The game configuration settings */
	public GameServerConfig getConfig( ) { return config; }

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString( ) {
		return "[" + getID( ) + "] " + getName( ) + " (" + getConnectedClients( ) + " / " + getConfig( ).getMaxNumPlayers( ) + ")";
	}
	
	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals( Object obj ) {
		if( obj == null || !(obj instanceof GameServerInfo) ) return false;
		final GameServerInfo info = (GameServerInfo) obj;
		
		return getID( ).equals( info.getID( ) ); 
	}
}
