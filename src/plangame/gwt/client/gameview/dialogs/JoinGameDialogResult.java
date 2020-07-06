/**
 * @file JoinGameDialogResult.java
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
 * @date         9 mei 2013
 * @project      NGI
 * @company      Almende B.V.
 */
package plangame.gwt.client.gameview.dialogs;

import plangame.model.object.BasicID;

/**
 * Result of the join game dialog
 * 
 * @author Joris Scharpff
 */
public class JoinGameDialogResult {
	/** The ID of the game we want to join */
	public final BasicID gameID;
	
	/** The preferred player name */
	public final String playername;
	
	/**
	 * Creates a new join game result (GM)
	 * 
	 * @param gameID The ID of the game we want to join
	 */
	public JoinGameDialogResult( BasicID gameID ) {
		this( gameID, null );
	}
	
	/**
	 * Creates a new join game result (SP)
	 * 
	 * @param gameID The ID of the game we want to join
	 * @param playername The preferred player name
	 */
	public JoinGameDialogResult( BasicID gameID, String playername ) {
		this.gameID = gameID;
		this.playername = playername;
	}
}
