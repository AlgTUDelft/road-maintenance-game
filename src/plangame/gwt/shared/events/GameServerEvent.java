/**
 * @file ServerEvent.java
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
 * @date         28 nov. 2012
 * @project      NGI
 * @company      Almende B.V.
 */
package plangame.gwt.shared.events;


/**
 * Fired when the server state changes, i.e. restarts or similar events
 *
 * @author Joris Scharpff
 */
@SuppressWarnings ("serial" )
public class GameServerEvent extends Event {
	/** The server event type */
	protected GameServerEventType eventtype;
	
	/** Server event types */
	@SuppressWarnings("javadoc")
	public enum GameServerEventType {
		Restart, // the game is restarting
		End; // the game is ending
	}
	
	/** Empty constructor for GWT RPC */
	@Deprecated protected GameServerEvent( ) { }

	/**
	 * Creates a new game server event
	 * 
	 * @param eventtype The server event type
	 */
	public GameServerEvent( GameServerEventType eventtype ) {
		super( );
		
		this.eventtype = eventtype;
	}
	
	/**
	 * @return The server event type
	 */
	public GameServerEventType getEventType( ) {
		return eventtype;
	}
	
	/**
	 * @see plangame.gwt.shared.events.Event#getName()
	 */
	@Override
	public String getName( ) {
		return "Game server Event";
	}
}
