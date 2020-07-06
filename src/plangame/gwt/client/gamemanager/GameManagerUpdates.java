/**
 * @file GameManagerUpdates.java
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
 * @date         28 aug. 2013
 * @project      NGI
 * @company      Almende B.V.
 */
package plangame.gwt.client.gamemanager;

import plangame.gwt.shared.clients.SPClient;
import plangame.gwt.shared.enums.ClientState;
import plangame.gwt.shared.enums.GameState;
import plangame.gwt.shared.gameresponse.GameResults;
import plangame.model.object.ObjectMap;

/**
 * All updates from the GameManager view
 *
 * @author Joris Scharpff
 */
public interface GameManagerUpdates {
	/**
	 * Called when the game state is changed
	 * 
	 * @param oldstate The previous game state
	 * @param newstate The new game state
	 */
	public void onGameStateChange( GameState oldstate, GameState newstate );
	
	/**
	 * Called when the client state is changed for one or more clients
	 * 
	 * @param newstates The new client state map
	 */
	public void onClientStateChange( ObjectMap<SPClient, ClientState> newstates );
	
	/**
	 * Called when new execution results are in
	 * 
	 * @param oldresults The old game results
	 * @param newresults The new game results
	 */
	public void onGameResultsChange( GameResults oldresults, GameResults newresults );
}
