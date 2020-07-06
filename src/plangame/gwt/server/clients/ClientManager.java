/**
 * @file ClientManager.java
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
 * @date         24 mrt. 2013
 * @project      NGI
 * @company      Almende B.V.
 */
package plangame.gwt.server.clients;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import plangame.gwt.server.Server;
import plangame.gwt.server.resource.locale.Lang;
import plangame.gwt.shared.LogType;
import plangame.gwt.shared.clients.Client;
import plangame.gwt.shared.clients.Client.ClientType;
import plangame.gwt.shared.clients.GMClient;
import plangame.gwt.shared.clients.SBClient;
import plangame.gwt.shared.clients.SMClient;
import plangame.gwt.shared.clients.SPClient;
import plangame.gwt.shared.exceptions.ClientNotConnectedException;
import plangame.gwt.shared.exceptions.SessionExpiredException;
import plangame.model.exceptions.InvalidObjectException;
import plangame.model.object.BasicID;
import plangame.model.object.BasicObject;
import plangame.model.object.ObjectMap;

/**
 * Server side manager for keeping track of connected clients
 *
 * @author Joris Scharpff
 */
public class ClientManager {	
	/** Max client ID */
	private final static int MAX_CLIENT_ID = 1000000;
	
	/** Number of seconds before removing a closing client from the list */
	private final static int CLIENT_DISCONNECT_TIME = 300;	
	
	/** The server we are managing clients for */
	protected Server server;
	
	/** List of connected clients */
	protected List<Client> clients;
	
	/** Client removal flag, if it is in this list it is scheduled for remove */
	protected ObjectMap<Client, Long> removetime;
	
	/** True as long as the client manager is running */
	protected boolean running;
		
	/**
	 * Creates a new ClientManager
	 */
	@Deprecated protected ClientManager( ) { }
	
	/**
	 * Creates a new client manager for the specified server
	 * 
	 * @param server The server
	 */
	public ClientManager( Server server ) {
		this.server = server;

		// creates a new client list and remove flag map
		clients = new ArrayList<Client>( );
		removetime = new ObjectMap<Client, Long>( );
		
		// set the client manager as running
		running = true;
		
		// create thread for client removal
		new Thread( new Runnable( ) {
			
			@Override
			public void run( ) {
				while( running ) {
					manage( );
					
					try {
						Thread.sleep( 500 );
					} catch( InterruptedException ie ) { }
				}
			}
		}, "Game-ClientManager" ).start( );
	}
	
	/**
	 * Checks the client list for possible removals, this is done periodically
	 */
	// TODO use queue for O(1) check instead of O(n)
	protected void manage( ) {
		// this function locks removetime, because it might be that some client
		// is closing in the middle of this check
		synchronized( removetime ) {
			// check all clients in the remove map if we should remove them now
			for( Client client : removetime.getKeys( ) ) {
				// check if the client should be removed 
				final long remtime = removetime.get( client );
				if( remtime > System.currentTimeMillis( ) ) continue;
				
				// remove its flag from the map
				removetime.remove( client );
					
				try {
					remove( client );
					server.log( LogType.Verbose, "Removed client '" + client + "' from server" );
				} catch( ClientNotConnectedException se ) {
					// client is already removed most likely, still send warning
					server.log( LogType.Warning, "Failed to disconnect flagged client '" + client + "'" );
				}
			}
		}
	}
	
	/**
	 * Creates a new client and adds it to the server list. Generates a unique
	 * ID for the client
	 * 
	 * @param type The type of client to generate
	 * @return The new client object
	 * @throws InvalidObjectException if the client type is unknown
	 */
	public Client newClient( ClientType type ) throws InvalidObjectException {
		// get a new unique ID for the client (lower than GM ID)
		boolean unique = false;
		BasicID clientID = null;
		while( !unique ) {
			clientID = new BasicID( type.getIDPrefix( ) + (new Random( )).nextInt( MAX_CLIENT_ID ) );
			
			// check if the ID is unique
			unique = isUniqueID( clientID ); 
		}
		
		// create the client
		Client client = null;
		switch( type ) {
			case ServerManager:
				client = new SMClient( clientID );
				break;
				
			case ScoreBoard:
				client = new SBClient( clientID );
				break;
			
			case ServiceProvider:
				client = new SPClient( clientID );
				break;
				
			case GameManager:
				client = new GMClient( clientID );
				break;
				
			default:
				final String emsg = Lang.get( "UnknownClientType" );
				throw new InvalidObjectException( emsg );
		}
		
		// add client to the client list		
		clients.add( client );
		return client;
	}
	
	/**
	 * Retrieves the client corresponding to the ID and type, requested on client
	 * reconnect.
	 * 
	 * @param clientID The client ID
	 * @param type The client type
	 * @return The client that was previously stored
	 * @throws SessionExpiredException if the clientID is not known at the server
	 * or the client known at the server has a different type
	 */
	public Client restoreClient( BasicID clientID, ClientType type ) throws SessionExpiredException {
		// restore the client
		final Client client = getClient( clientID );
		if( client == null ) {
			final String msg = Lang.get( "SessionExpired", clientID.toString( ) );
			server.log( LogType.Verbose, msg );
			throw new SessionExpiredException( msg );
		}
		
		// check if the client is of the correct type
		if( !type.equals( client.getClientType( ) ) ) {
			final String msg = Lang.get( "SessionDifferentType", clientID.toString( ) );
			server.log( LogType.Verbose, msg );
			throw new SessionExpiredException( msg );
		}		
		
		// the client is known and can reconnect correctly, clear its removal flag
		try {
			unflagRemoval( client );
		} catch( ClientNotConnectedException se ) {
			// never thrown because we are sure the client is known at the server
		}
		
		return client;
	}
	
	/**
	 * Removes a client from the list, used on disconnect
	 * 
	 * @param client The client to disconnect
	 * @throws ClientNotConnectedException if the client was not connected
	 */
	public void remove( Client client ) throws ClientNotConnectedException {
		if( !clients.remove( client ) )
			throw new ClientNotConnectedException( Lang.get( "ClientNotConnected", client.getID( ).toString( ) ) );
	}
	
	/**
	 * Flags the client for removal, used when a client view indicates a close.
	 * When a client is flagged for removal, it will be removed after some time
	 * unless the flag is cleared (on refresh)
	 * 
	 * @param client The client to flag for removal
	 * @throws ClientNotConnectedException if the client is not connected to the server
	 */
	public void flagRemoval( Client client ) throws ClientNotConnectedException {
		checkClient( client );
		
		// set its flag and use a timed callback to check if we should really
		// remove the client, unless it is already flagged for removal
		if( !removetime.containsKey( client ) )
			removetime.put( client, System.currentTimeMillis( ) + CLIENT_DISCONNECT_TIME * 1000 );
	}
	
	/**
	 * Clears the removal flag, used when the client is reconnecting after a
	 * refresh. The client will no longer be scheduled for removal
	 * 
	 * @param client The client
	 * @throws ClientNotConnectedException if the client is not known at the server
	 */
	public void unflagRemoval( Client client ) throws ClientNotConnectedException {
		checkClient( client );
		
		// remove the client from the remove list, this may fail but only when the
		// client wasn't scheduled for removal anyway
		if( removetime.remove( client ) != null )
			server.log( LogType.Verbose, "Cleared removal flag for client '" + client + "'" );
		else
			server.log( LogType.Verbose, "Client '" + client + "' was not flagged for removal" );
	}
	
	/**
	 * @return The list of connected clients
	 */
	public List<Client> getClients( ) {
		return clients;
	}
	
	/**
	 * Finds the client with the specified ID
	 * 
	 * @param clientID The client ID to find
	 * @return The client or null if there is no client with this ID
	 */
	public Client getClient( BasicID clientID ) {
		return BasicObject.fromList( clients, clientID );
	}
	
	/**
	 * Checks if the specified ID is not used by any client yet
	 * 
	 * @param ID The ID to check
	 * @return True if none of the clients has this ID
	 */
	public boolean isUniqueID( BasicID ID ) {
		return (BasicObject.fromList( clients, ID ) == null);
	}
	
	/**
	 * Checks if the client is connected to the server, otherwise an exception
	 * is thrown
	 * 
	 * @param client The client to verify
	 * @throws ClientNotConnectedException if the client is not connected
	 */
	private void checkClient( Client client ) throws ClientNotConnectedException {
		if( client == null )
			throw new ClientNotConnectedException( Lang.get( "ClientNull" ) );
		
		if( BasicObject.fromList( clients, client.getID( ) ) == null )
			throw new ClientNotConnectedException( Lang.get( "ClientNotConnected", client.getID( ).toString( ) ) );
	}
	
	/**
	 * Stops the client manager
	 */
	public void stop( ) {
		running = false;
	}
}
