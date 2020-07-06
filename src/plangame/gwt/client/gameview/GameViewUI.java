/**
 * @file GameViewWidget.java
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
 * @date         3 jan. 2013
 * @project      NGI
 * @company      Almende B.V.
 */
package plangame.gwt.client.gameview;

import plangame.gwt.client.ClientViewUI;
import plangame.gwt.client.gamedata.ClientGameData;
import plangame.gwt.client.gameview.dialogs.JoinGameDialog;
import plangame.gwt.client.gameview.dialogs.JoinGameDialogResult;
import plangame.gwt.client.widgets.dialogs.DialogHandler;
import plangame.gwt.shared.state.GameServerInfo;

/**
 * Basic game view widget for game manager and service provider UI
 *
 * @author Joris Scharpff
 */
public abstract class GameViewUI extends ClientViewUI {

	/**
	 * Creates a new game view widget for managed by the specified game view.
	 * 
	 * @param gameview The game view managing the UI
	 */
	public GameViewUI( GameView gameview ) {		
		super( gameview );
	}
	
	/**
	 * Called by the game view to enable the UI to register for game data updates
	 * 
	 * @param gamedata The client game data
	 */
	protected abstract void registerDataWidgets( ClientGameData gamedata );

	/**
	 * Shows the join server dialog
	 * 
	 * @param player True if this dialog is for a SP player
	 * @param handler The dialog handler to handle the result
	 */
	protected void showJoinGame( boolean player, DialogHandler<JoinGameDialogResult> handler ) {
		new JoinGameDialog( handler, player ).show( );		
	}
	
	/**
	 * Called whenever the server info is changed, implementing classes should
	 * update their UI based on the new settings.
	 * 
	 * @param serverinfo The new server info
	 */
	protected abstract void updateServerInfo( GameServerInfo serverinfo );
		
	/**
	 * Type cast to game view
	 * 
	 * @see plangame.gwt.client.ClientViewUI#getView()
	 */
	@Override
	protected GameView getView( ) {
		return (GameView)super.getView( );
	}
}
