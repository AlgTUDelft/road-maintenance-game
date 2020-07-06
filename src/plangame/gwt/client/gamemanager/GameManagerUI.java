/**
 * @file SPWidget.java
 * @brief Short description of file
 *
 * This file is created at Almende B.V. It is open-source software and part of the Common
 * Hybrid Agent Platform (CHAP). A toolbox with a lot of open-source tools, ranging from
 * thread pools and TCP/IP components to control architectures and learning algorithms.
 * This software is published under the GNU Lesser General Public license (LGPL).
 *
 * Copyright � 2012 Joris Scharpff <joris@almende.com>
 *
 * @author       Joris Scharpff
 * @date         12 dec. 2012
 * @project      NGI
 * @company      Almende B.V.
 */
package plangame.gwt.client.gamemanager;

import java.util.List;

import plangame.game.plans.PlanStepResult;
import plangame.gwt.client.gamedata.ClientGameData;
import plangame.gwt.client.gamemanager.dialogs.ChangeClientStateDialog;
import plangame.gwt.client.gamemanager.dialogs.ChangeGameStateDialog;
import plangame.gwt.client.gamemanager.dialogs.DelayDialog;
import plangame.gwt.client.gamemanager.dialogs.DelayDialogResult;
import plangame.gwt.client.gamemanager.dialogs.ExecuteDialog;
import plangame.gwt.client.gamemanager.dialogs.PortfolioDialog;
import plangame.gwt.client.gameview.GameViewUI;
import plangame.gwt.client.resource.locale.Lang;
import plangame.gwt.client.widgets.controller.ControlWidget;
import plangame.gwt.client.widgets.dialogs.DialogHandler;
import plangame.gwt.client.widgets.help.HelpWidget;
import plangame.gwt.client.widgets.map.MapWidget;
import plangame.gwt.client.widgets.plan.PlanWidget;
import plangame.gwt.client.widgets.playermanager.PlayerManagerWidget;
import plangame.gwt.shared.ExecutionInfo;
import plangame.gwt.shared.clients.SPClient;
import plangame.gwt.shared.enums.ClientState;
import plangame.gwt.shared.enums.GameState;
import plangame.gwt.shared.state.GameServerInfo;
import plangame.model.tasks.Portfolio;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * The interface code for the service provider
 *
 * @author Joris Scharpff
 */
public class GameManagerUI extends GameViewUI {	
	/** The UI binder for the interface */
	protected interface GameManagerUIBinder extends UiBinder<Widget, GameManagerUI> {}
	
	/** The UI elements */
	@UiField protected TabLayoutPanel tabMain;
	@UiField protected PlanWidget wdPlan;
	@UiField protected PlayerManagerWidget wdPlayers;
	@UiField protected MapWidget wdMap;
	@UiField protected ControlWidget wdController;
	@UiField protected HelpWidget wdHelp;
	
	/**
	 * Creates the game manager user interface
	 * 
	 * @param gm The game manager using this widget
	 */
	protected GameManagerUI( GameManager gm ) {
		super( gm );
		
		// make sure data is refreshed when the tab is selected
		tabMain.addSelectionHandler( new SelectionHandler<Integer>( ) {
			@Override
			public void onSelection( SelectionEvent<Integer> event ) {
				if( event.getSelectedItem( ) == 0 )
					wdPlayers.getData( ).refresh( );
			}
		} );
	}
	
	/**
	 * @see plangame.gwt.client.ClientViewUI#initUI()
	 */
	@Override
	protected Widget initUI( ) {
		return ((GameManagerUIBinder) GWT.create( GameManagerUIBinder.class )).createAndBindUi( this );
	}
	
	/**
	 * @see plangame.gwt.client.gameview.GameViewUI#registerDataWidgets(plangame.gwt.client.gamedata.ClientGameData)
	 */
	@Override
	protected void registerDataWidgets( ClientGameData gamedata ) {
		// FIXME enable for plan drawing
		//gamedata.addUpdateListener( wdPlan );
		//wdPlan.setGameData( gamedata );
	}
	
	/**
	 * @see plangame.gwt.client.gameview.GameViewUI#updateServerInfo(plangame.gwt.shared.state.GameServerInfo)
	 */
	@Override
	protected void updateServerInfo( GameServerInfo info ) {
		// nothing much to do
	}
	
	/**
	 * Shows a dialog to start execution
	 * 
	 * @param initsleep The initial value for the round sleep
	 * @param handler The dialog handler
	 */
	protected void showExecuteDialog( long initsleep, DialogHandler<ExecutionInfo> handler ) {
		new ExecuteDialog( initsleep, handler ).show( );
	}
	
	/**
	 * Shows a dialog for the GM to choose what task methods should be delayed
	 * 
	 * @param results The results to be processed
	 * @param handler The dialog handler
	 */
	protected void showDelayDialog( PlanStepResult results, DialogHandler<DelayDialogResult> handler ) {
		new DelayDialog( results, handler ).show( );
	}
	
	/**
	 * Shows a dialog for the GM to assign portfolio to a player
	 * 
	 * @param client The client to assign a portfolio
	 * @param portfolios The list of available portfolios
	 * @param handler The dialog result handler 
	 */
	protected void showAssignDialog( SPClient client, List<Portfolio> portfolios, DialogHandler<Portfolio> handler ) {
		new PortfolioDialog( client.getPlayer( ), portfolios, handler ).show( );
	}
	
	/**
	 * Shows a dialog for the GM to change the game state
	 * 
	 * @param state The current game state
	 * @param handler The dialog handler
	 */
	protected void showChangeGameState( GameState state, DialogHandler<GameState> handler ) {
		new ChangeGameStateDialog( state, handler ).show( );
	}
	
	/**
	 * Shows a dialog for the GM to change the player state
	 * 
	 * @param client Tghe client
	 * @param state It's current state
	 * @param handler The dialog result handler
	 */
	protected void showChangeClientState( SPClient client, ClientState state, DialogHandler<ClientState> handler ) {
		new ChangeClientStateDialog( client, state, handler ).show( );
	}
	
	/**
	 * Adds a client to the UI
	 * 
	 * @param client The client
	 */
	protected void addClient( SPClient client ) {
		wdPlayers.getData( ).addItem( client );
	}
	
	/**
	 * Updates the info of a client using the new object, the previous object
	 * will be removed from the table. If no previous entry, the client will be
	 * added
	 * 
	 * @param client The client to update
	 */
	protected void updateClient( SPClient client ) {
		wdPlayers.getData( ).setItem( client );
	}
	
	/**
	 * Returns the selected client (in the GameWidget)
	 * 
	 * @return The client or null if no client is selected
	 */
	public SPClient getSelectedClient( ) {
		return wdPlayers.getData( ).getSelectedItem( );
	}
	
	/**
	 * Updates the help text to match the state
	 * 
	 * @param newstate The new game state 
	 */
	protected void updateHelp( GameState newstate ) {
		// set help title and text using resource bundle
		final String title;
		switch( newstate ) {
			case Initialising: title = Lang.text.GM_StateInitialisingTitle( ); break;
			case Starting: title = Lang.text.GM_StateStartingTitle( ); break;
			case Idle: title = Lang.text.GM_StateIdleTitle( ); break;
			case Planning: title = Lang.text.GM_StatePlanningTitle( ); break;
			case Accept: title = Lang.text.GM_StateAcceptTitle( ); break;
			case Executing: title = Lang.text.GM_StateExecutingTitle( ); break;
			case Finished: title = Lang.text.GM_StateFinishedTitle( ); break;
			
			default:
				assert false : "Unknown state!";
			title = "";
		}
		wdHelp.setHelpTitle( title );

		// set help title and text using resource bundle
		final String text;
		switch( newstate ) {
			case Initialising: text = Lang.text.GM_StateInitialising( ); break;
			case Starting: text = Lang.text.GM_StateStarting( ); break;
			case Idle: text = Lang.text.GM_StateIdle( ); break;
			case Planning: text = Lang.text.GM_StatePlanning( ); break;
			case Accept: text = Lang.text.GM_StateAccept( ); break;
			case Executing: text = Lang.text.GM_StateExecuting( ); break;
			case Finished: text = Lang.text.GM_StateFinished( ); break;
			
			default:
				assert false : "Unknown state!";
				text = "";
		}
		wdHelp.setHelpText( text );
	}
}
