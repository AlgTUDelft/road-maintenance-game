/**
 * @file ClientEventListener.java
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
 * @date         18 mrt. 2013
 * @project      NGI
 * @company      Almende B.V.
 */
package plangame.gwt.client;

import java.io.Serializable;
import java.util.List;

import plangame.gwt.client.resource.locale.Lang;
import plangame.gwt.client.util.RPCCallback;
import plangame.gwt.shared.LogType;
import plangame.gwt.shared.events.DisconnectEvent;
import plangame.gwt.shared.events.DisconnectEvent.DisconnectReason;
import plangame.gwt.shared.events.Event;
import plangame.gwt.shared.exceptions.EventException;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.RepeatingCommand;

/**
 * Listener for all client side events
 *
 * @author Joris Scharpff
 */
// FIXME proper ending/restore of listening on refresh
@SuppressWarnings("serial")
public class ClientEventListener implements Serializable {
	/** Time in msec between event polling */
	private final static int EVENT_POLL_DELAY = 250;
	
	/** The client view we are listening for */
	protected ClientView clientview;
	
	/** Should we still listen for events? */
	protected boolean listening;
	
	/** Empty constructor for GWT RPC */
	@Deprecated protected ClientEventListener( ) { }
	
	/**
	 * Creates a new event listener for the specified client view
	 * 
	 * @param clientview The client view to listen for
	 */
	public ClientEventListener( ClientView clientview ) {
		this.clientview = clientview;
	}
	
	/**
	 * Polls the server occasionally for events, notifies the client when one or
	 * more event are received
	 */
	protected void listen( ) {
		clientview.getRPC( ).listen( clientview.getClient( ).getID( ), new RPCCallback<List<Event>>( ) {
			@Override public void success( List<Event> result ) {
				// are we still listening?
				if( !listening ) return;
				
				// stop listening if the server says so
				if( result == null ) {
					stop( );
					return;
				}
				
				// check if there is an event for us to process
				for( Event e : result )
					event( e );
			}
			
			/**
			 * @see plangame.gwt.client.util.RPCCallback#failure(java.lang.Throwable)
			 */
			@Override
			public void failure( Throwable caught ) {
				if( caught instanceof EventException )
					listening = false;
				else
					super.failure( caught );
			}

			@Override protected String getFailureText( ) { return Lang.text.Client_ListenFail( ); }
		} );
	}

	/**
	 * Called when an event is received from the server, the event should be
	 * handled by the implementing game view. The function should return false
	 * if the client wants to end listening for events
	 * 
	 * @param event The event that was received
	 */
	protected void event( Event event ) {
		clientview.log( LogType.Event, "Received event '" + event.getName( ) + "', sender ID: " + event.getSenderID( ) );
		
		// try to handle the event at the client implementation
		final boolean handled = clientview.onEvent( event );
		
		// hack for disconnect
		if( (event instanceof DisconnectEvent) && ((DisconnectEvent)event).getReason( ) == DisconnectReason.ServerDisconnect ) {
			stop( );
		}
		
		// check if the event was handled
		if( !handled )
			clientview.error( "Unknown event received: " + event.getName( ) );
	}
	
	
	/**
	 * Starts listening for events
	 */
	public void start( ) {
		clientview.log( LogType.Event, "Started listening" );
		listening = true;
		
		Scheduler.get( ).scheduleFixedDelay( new RepeatingCommand( ) {
			/**
			 * Polls the server for events, is executed until listening is false
			 * 
			 * @see com.google.gwt.core.client.Scheduler.RepeatingCommand#execute()
			 */
			@Override
			public boolean execute( ) {
				// may be that we have already stopped listening
				if( !listening ) return false;
				
				// listen for events
				listen( );
				return listening;
			} }, EVENT_POLL_DELAY );
	}
	
	/**
	 * Stops listening for events
	 */
	public void stop( ) {
		clientview.log( LogType.Event, "Stopped listening" );
		listening = false;
	}
}
