/**
 * @file UpdateStateEvent.java
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
 * @date         20 aug. 2013
 * @project      NGI
 * @company      Almende B.V.
 */
package plangame.gwt.shared.events;

import plangame.gwt.shared.enums.GameState;


/**
 * Notifies the GM of a game state change
 *
 * @author Joris Scharpff
 */
@SuppressWarnings("serial")
public class UpdateStateEvent extends Event {
	/** The new game state */
	protected GameState state;
	
	/** Empty constructor for GWT RPC */
	@Deprecated protected UpdateStateEvent( ) { }
	
	/**
	 * Creates a new update state event
	 * 
	 * @param state The new game state
	 */
	public UpdateStateEvent( GameState state ) {
		this.state = state;
	}
	
	/**
	 * @return The new game state
	 */
	public GameState getGameState( ) {
		return state;
	}
	
	/**
	 * @see plangame.gwt.shared.events.Event#getName()
	 */
	@Override
	public String getName( ) {
		return "Update state event";
	}
}
