/**
 * @file GameResponse.java
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
 * @date         10 dec. 2012
 * @project      NGI
 * @company      Almende B.V.
 */
package plangame.gwt.shared.gameresponse;

import java.io.Serializable;

import plangame.model.object.BasicID;

/**
 * Base class for all response for game queries
 *
 * @author Joris Scharpff
 */
@SuppressWarnings ("serial" )
public abstract class GameResponse implements Serializable {
	/** The sending game ID */
	protected BasicID gameID;
	
	/** Constructor for GWT */
	@Deprecated protected GameResponse( ) { }
	
	/**
	 * Creates a new game response
	 * 
	 * @param gameID The ID of the responding server
	 */
	public GameResponse( BasicID gameID ) {
		this.gameID = gameID;
	}
	
	/**
	 * @return The game ID of the responding game
	 */
	public BasicID getGameID( ) {
		return gameID;
	}
}
