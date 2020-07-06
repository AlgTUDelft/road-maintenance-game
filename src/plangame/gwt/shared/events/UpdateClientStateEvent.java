/**
 * @file UpdateSPState.java
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
 * @date         17 jul. 2013
 * @project      NGI
 * @company      Almende B.V.
 */
package plangame.gwt.shared.events;

import java.util.List;

import plangame.gwt.shared.clients.SPClient;
import plangame.gwt.shared.enums.ClientState;
import plangame.model.object.ObjectMap;

/**
 * Given the game manager an update on the current state of the SP
 *
 * @author Joris Scharpff
 */
@SuppressWarnings("serial")
public class UpdateClientStateEvent extends Event {
	/** The map of new client state(s) */
	protected ObjectMap<SPClient, ClientState> states;
	
	/** Empty GWT constructor */
	@Deprecated protected UpdateClientStateEvent( ) { }
	
	/**
	 * Creates a new update event for a single state update
	 * 
	 * @param client The client
	 * @param state The new State
	 */
	public UpdateClientStateEvent( SPClient client, ClientState state ) {
		states = new ObjectMap<SPClient, ClientState>( );
		states.put( client, state	);
	}
	
	/**
	 * Creates a new update event for a multi-state update
	 * 
	 * @param clients The clients
	 * @param state The new state for all clients
	 */
	public UpdateClientStateEvent( List<SPClient> clients, ClientState state ) {
		states = new ObjectMap<SPClient, ClientState>( );
		for( SPClient client : clients ) 
			states.put( client, state	);
	}
	
	/**
	 * @param client The client to get state of
	 * @return The state, null if not specified
	 */
	public ClientState getState( SPClient client ) {
		return states.get( client );
	}
	
	/**
	 * @return The state object map
	 */
	public ObjectMap<SPClient, ClientState> getStates( ) {
		return states;
	}
	
	/**
	 * @see plangame.gwt.shared.events.Event#getName()
	 */
	@Override
	public String getName( ) {
		return "Update Client State Event";
	}

}
