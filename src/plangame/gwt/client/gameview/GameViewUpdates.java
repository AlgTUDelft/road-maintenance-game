/**
 * @file GameViewUpdates.java
 * @brief Short description of file
 *
 * This file is created at Almende B.V. It is open-source software and part of the Common
 * Hybrid Agent Platform (CHAP). A toolbox with a lot of open-source tools, ranging from
 * thread pools and TCP/IP components to control architectures and learning algorithms.
 * This software is published under the GNU Lesser General Public license (LGPL).
 *
 * Copyright � 2013 Joris Scharpff <joris@almende.com>
 *
 * @author       Joris Scharpff
 * @date         28 aug. 2013
 * @project      NGI
 * @company      Almende B.V.
 */
package plangame.gwt.client.gameview;

import plangame.gwt.shared.gameresponse.RestoreResponse;
import plangame.gwt.shared.state.GameServerInfo;
import plangame.gwt.shared.state.GameServerState;

/**
 * Updates from the GameView and GameViewUI
 *
 * @author Joris Scharpff
 */
public interface GameViewUpdates {	
	/**
	 * Called whenever the game server state information changes
	 * 
	 * @param oldstate The previous state info
	 * @param newstate The new state info
	 */
	public void onServerStateChange( GameServerState oldstate, GameServerState newstate );
	
	/**
	 * Called whenever the game server information changes
	 * 
	 * @param serverinfo The server info
	 */
	public void onServerInfoChange( GameServerInfo serverinfo );

	/**
	 * Called whenever the client has been restored
	 * 
	 * @param restore The restore info
	 */
	public void onClientRestore( RestoreResponse restore );
	
	/**
	 * Called when the game is actually starting
	 */
	public void onStartGame( );
}
