/**
 * @file ServerOfflineException.java
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
 * @date         6 sep. 2012
 * @project      NGI
 * @company      Almende B.V.
 */
package plangame.gwt.shared.exceptions;

import plangame.model.object.BasicID;




/**
 * @author Joris Scharpff
 */
@SuppressWarnings ("serial" )
public class GameServerException extends Exception {
	/** The ID of the gameserver throwing the exception */
	protected BasicID gameID;
	
	/** Empty constructor for GWT */
	@Deprecated protected GameServerException( ) { } 
	
	/**
	 * Creates a new exception
	 * 
	 * @param gameID The game server ID
	 */
	public GameServerException( BasicID gameID ) {
		super( );
		
		this.gameID = gameID;
	}
	
	/**
	 * Creates a new exception with the specified message
	 * 
	 * @param gameID The ID of the game server throwing the exception
	 * @param msg The error message
	 */
	public GameServerException( BasicID gameID, String msg ) {
		super( msg );
		
		this.gameID = gameID;
	}
}
