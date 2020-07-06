/**
 * @file EventManager.java
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
 * @date         7 jan. 2013
 * @project      NGI
 * @company      Almende B.V.
 */
package plangame.gwt.server.events;

import java.util.ArrayList;
import java.util.List;

import plangame.gwt.server.GameServer;
import plangame.gwt.server.Server;
import plangame.gwt.server.resource.locale.Lang;
import plangame.gwt.shared.LogType;
import plangame.gwt.shared.clients.Client;
import plangame.gwt.shared.events.Event;
import plangame.gwt.shared.exceptions.ClientNotConnectedException;
import plangame.gwt.shared.exceptions.EventException;
import plangame.model.object.BasicID;
import plangame.model.object.ObjectMap;

/**
 * Class handling all client/server event related infrastructure 
 *
 * @author Joris Scharpff
 */
public class EventManager {
	/** The server using this event manager */
	protected Server server;
	
	/** Map of clients listening for events */
	protected ObjectMap<Client, List<Event>> eventmap;
		
	/** The event manager keeps on running until this flag is false */
	private boolean serverup;

	/** Sleep time between polling of events (msec) */
	protected final static int EVENT_SLEEP = 500;
	
	/**
	 * Creates a new EventManager for the server and starts it
	 * 
	 * @param server The server
	 */
	public EventManager( ) {
		serverup = true;
		
		// create event map and response map list
		eventmap = new ObjectMap<Client, List<Event>>( );
		
		
		/* Removed: no longer necessary, everything is done through asynchronous
		 * events instead of response maps  
		 *  start( ); */
	}
	
	/**
	 * Sets the server using this event manager
	 * 
	 * @param server The server
	 */
	public void setServer( Server server ) {
		this.server = server;
	}
	
	/**
	 * Starts the event managing
	 */
	protected void start( ) {		
		// separate routine for handling event callbacks
		new Thread( new Runnable( ) {
			@Override
			public void run( ) {
				// keep running as long as the server is active
				while( serverup ) {										
					// sleep a while
					try {
						Thread.sleep( EVENT_SLEEP );
					} catch( InterruptedException e ) {
						// nothing to do here
					}
				}
			}
		}, "Game-EventManager" ).start( );						
	}
	
	/**
	 * Stops handling server events
	 */
	public void stop( ) {
		// stop all handlers
		serverup = false;
	}
	
	/**
	 * Adds a client to the event map
	 * 
	 * @param client The client to add
	 */
	public void addClient( Client client ) {
		assert (eventmap.containsKey( client )) : "Client is already registered for events";
		server.log( LogType.Event, "Added client '" + client + "' to event map" );
		eventmap.put( client, new ArrayList<Event>( ) );
	}
	
	/**
	 * Resets the event map for the client
	 * 
	 * @param client The client to reset
	 */
	public void resetClient( Client client ) {
		server.log( LogType.Event, "Reset event map for client '" + client + "'" );
		eventmap.put( client, new ArrayList<Event>( ) );
	}
	
	/**
	 * Removes a client from the event map
	 * 
	 * @param client The client to remove
	 * @throws ClientNotConnectedException if the client was not in the event map
	 */
	public void removeClient( Client client ) throws ClientNotConnectedException {
		if( !eventmap.containsKey( client ) )
			throw new ClientNotConnectedException( Lang.get( "ClientNotConnected", client.getID( ).toString( ) ) );
		server.log( LogType.Event, "Removed client '" + client + "' from the event map" );
		eventmap.remove( client );
	}

	/**
	 * Returns the pending event list for the client
	 * 
	 * @param client The client
	 */
	protected List<Event> getEventList( Client client ) {
		return eventmap.get( client );
	}
	
	/**
	 * Checks if the client is registered
	 * 
	 * @param client The client
	 * @return True if the client is registered at the EM
	 */
	public boolean isRegistered( Client client ) {
		return eventmap.containsKey( client );
	}
	
	/**
	 * Appends the event to the list of pending events for the client. These
	 * events are processed by listen( )
	 * 
	 * @param client The client
	 * @param event The event
	 * @return True if the event is added
	 */
	protected boolean addEvent( Client client, Event event ) {
		if( getEventList( client ) == null ) return false;
		
		return getEventList( client ).add( event );
	}
	
	/**
	 * Polls the event queue for the given client
	 * 
	 * @param client The client to poll for
	 * @return The list of pending events or null if the server is no longer
	 * communicating with the client
	 * @throws EventException if the client is not registered in the manager
	 */
	public List<Event> listen( Client client ) throws EventException {
		// check if the client is registered
		if( !isRegistered( client ) )
			throw new EventException( Lang.get( "NotRegisteredForEvents", client.getID( ).toString( ) ) );
		
		return getEvents( client );
	}
	
	/**
	 * Gets the current pending events for the clients, removes them from the
	 * client event queue
	 * 
	 * @param client The client to get events for
	 * @return The events or null if no event
	 */
	protected List<Event> getEvents( Client client ) {
		final List<Event> events = new ArrayList<Event>( );
		
		// remove all pending events (one a time to avoid synchronisation issues)
		while( getEventList( client ).size( ) > 0 )
			events.add( getEventList( client ).remove( 0 ) );
		
		return events;
	}
	
	/**
	 * Fires an event for all SP clients connected to the game server, the GM and
	 * score board are not notified.
	 * 
	 * @param gameserver The game server
	 * @param event The event
	 * @return The number of notified clients
	 */
	public int fireEvent( GameServer gameserver, Event event ) {
		return fireEvent( gameserver, false, false, event );
	}
	
	/**
	 * Fires an event for all clients connected to the game server
	 * 
	 * @param gameserver The game server
	 * @param notifygm True to also send out the event to the GM
	 * @param notifysb True to also send out the event to the SB
	 * @param event The event
	 * @return The number of notified clients
	 */
	public int fireEvent( GameServer gameserver, boolean notifygm, boolean notifysb, Event event ) {
		int fired = 0;
		for( Client c : gameserver.getClients( ) )
			fired += (fireEvent( gameserver.getID( ), c, event ) ? 1 : 0);
		
		// send it also to the GM
		if( notifygm && gameserver.getManager( ) != null )
			fired += fireEvent( gameserver.getID( ), gameserver.getManager( ), event ) ? 1 : 0;
		
		// and SB
		if( notifysb & gameserver.getScoreBoard( ) != null )
			fired += fireEvent( gameserver.getID( ), gameserver.getScoreBoard( ), event ) ? 1 : 0;
		
		server.log( LogType.Event, "Event broadcast notified " + fired + " client(s), sender: " + event.getSenderID( ) );		
		
		return fired;
	}
	
	
	/**
	 * Fires an event for the specified client
	 * 
	 * @param senderID The ID of the sender
	 * @param client The client to receive the event
	 * @param event The event to send
	 * @return True if the event has been added to the ebent list
	 */
	public boolean fireEvent( BasicID senderID, Client client, Event event ) {
		// set event sender
		event.setSender( senderID );
		
		server.log( LogType.Event, "Event '" + event.getName( ) + "' fired for client " + client.getID( ) );
		return addEvent( client, event );
	}
		
	/**
	 * Fires the event but maps all responses, calls the callback function when
	 * all responses are in
	 * 
	 * @param gameserver The game server awaiting client responses
	 * @param event The event to send out
	 * @param callback The callback function to call when all responses are in
	 * @return The response map
	 */
	/*
	public EventResponseMap fireEvent( GameServer gameserver, Event event, final EventResponseCallback callback ) {
		// add the response map
		final EventResponseMap map = new EventResponseMap( gameserver.getClients( ), callback );
		responsemaps.add( map );
		
		// fire the event to all clients of the server and return the number of
		// notified clients
		fireEvent( gameserver, event );
		
		return map;
	} */
	
	/**
	 * Fires the event and waits (blocks) for the given maximum duration for the
	 * client to process it.
	 * 
	 * @param senderID The ID of the sender
	 * @param client The client listening for the event
	 * @param event The event to fire
	 * @param maxwaittime The maximum wait time in milliseconds
	 * @throws EventTimeOutException if the wait has timed out or the client is
	 * disconnected during wait
	 */
	public void fireEventAndWait( BasicID senderID, Client client, Event event, long maxwaittime ) throws EventTimeOutException {
		// fire the event as usual
		fireEvent( senderID, client, event );
		
		// but now wait for it to be processed
		long timerem = maxwaittime;
		while( getEventList( client ).contains( event ) ) {
			try {
				Thread.sleep( EVENT_SLEEP );
			} catch( InterruptedException e ) { }
			timerem -= EVENT_SLEEP;
			
			// check if wait time has expired
			if( timerem < 0 ) {
				// last check if the event has been processed
				if( !getEventList( client ).contains( event ) ) break;
				
				throw new EventTimeOutException( Lang.get( "EventTimeOut", client.getID( ).toString( ) ) );
			}
		}
	}
}
