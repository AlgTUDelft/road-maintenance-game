/**
 * @file ScoreBoard.java
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
 * @date         15 sep. 2013
 * @project      NGI
 * @company      Almende B.V.
 */
package plangame.gwt.client.scoreboard;

import plangame.game.player.Player;
import plangame.gwt.client.ClientViewUI;
import plangame.gwt.client.gameview.GameView;
import plangame.gwt.client.resource.SBResource;
import plangame.gwt.client.resource.locale.Lang;
import plangame.gwt.client.rpc.ClientRPCAsync;
import plangame.gwt.client.widgets.controls.DataTable;
import plangame.gwt.shared.clients.Client;
import plangame.gwt.shared.clients.Client.ClientType;
import plangame.gwt.shared.clients.SPClient;
import plangame.gwt.shared.events.AcceptEvent;
import plangame.gwt.shared.events.AssignPortfolioEvent;
import plangame.gwt.shared.events.Event;
import plangame.gwt.shared.events.ExecutedEvent;
import plangame.gwt.shared.events.JoinEvent;
import plangame.gwt.shared.events.PlanChangeEvent;
import plangame.gwt.shared.gameresponse.RestoreResponse;
import plangame.gwt.shared.state.GameServerInfo;
import plangame.gwt.shared.state.GameServerState;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;

/**
 * Client that functions as a score board for the game
 *
 * @author Joris Scharpff
 */
public class ScoreBoard extends GameView {	
	/**
	 * Creates a new score board client
	 */
	protected ScoreBoard(  ) {
		super( (ClientRPCAsync)GWT.create( SBRPC.class ), (SBResource)GWT.create( SBResource.class ) );
	}
	
	/**
	 * @see plangame.gwt.client.ClientView#initResource()
	 */
	@Override
	protected void initResource( ) {
		super.initResource( );
		getResources( ).sbcss( ).ensureInjected( );
	}
	
	/** @return The score board resources */
	@Override public SBResource getResources( ) { return (SBResource) super.getResources( ); }

	/**
	 * @see plangame.gwt.client.ClientView#getClientType()
	 */
	@Override
	public ClientType getClientType( ) { return ClientType.ScoreBoard; }
	
	/**
	 * @see plangame.gwt.client.ClientView#initUI()
	 */
	@Override
	protected ClientViewUI initUI( ) {
		return new ScoreBoardUI( this );
	}
	
	/**
	 * @see plangame.gwt.client.ClienViewUpdates#onClientChange(plangame.gwt.shared.clients.Client, plangame.gwt.shared.clients.Client)
	 */
	@Override
	public void onClientChange( Client oldclient, Client newclient ) {
	}
	
	/**
	 * @see plangame.gwt.client.gameview.GameViewUpdates#onClientRestore(plangame.gwt.shared.gameresponse.RestoreResponse)
	 */
	@Override
	public void onClientRestore( RestoreResponse restore ) {
	}
	
	/**
	 * @see plangame.gwt.client.gameview.GameViewUpdates#onServerInfoChange(plangame.gwt.shared.state.GameServerInfo)
	 */
	@Override
	public void onServerInfoChange( GameServerInfo serverinfo ) {
		// update the title
		Window.setTitle( "[" + Lang.text.SB_WindowTitle( ) + "]" + (serverinfo != null ? " " + serverinfo.getName( ) : "" ) );
	}
	
	/**
	 * @see plangame.gwt.client.gameview.GameViewUpdates#onServerStateChange(plangame.gwt.shared.state.GameServerState, plangame.gwt.shared.state.GameServerState)
	 */
	@Override
	public void onServerStateChange( GameServerState oldstate, GameServerState newstate ) {
	}
	
	/**
	 * @see plangame.gwt.client.gameview.GameViewUpdates#onStartGame()
	 */
	@Override
	public void onStartGame( ) {
	}
	
	
	
	/**
	 * @see plangame.gwt.client.gameview.GameView#onEvent(plangame.gwt.shared.events.Event)
	 */
	@Override
	protected boolean onEvent( Event event ) {
		// player has joined the game
		if( event instanceof JoinEvent ) {
			final Client c = ((JoinEvent) event).getClient( );
			if( c instanceof SPClient )
				getData( ).addItem( ((SPClient)c).getPlayer( ) ); 
			return true;
		}

		// a portfolio is assigned to a player
		if( event instanceof AssignPortfolioEvent ) {
			getData( ).setItem( ((AssignPortfolioEvent) event).getClient( ).getPlayer( ) );
			return true;
		}
		
		// the joint plan is changed
		if( event instanceof PlanChangeEvent ) {
			getGameData( ).changeJointPlan( ((PlanChangeEvent) event).getPlanChange( ), false );
			return true;
		}
		
		// all plans have been submitted, update the scores
		if( event instanceof AcceptEvent ) {
			// refresh the score table
			getUI( ).updateScores( );
			return true;
		}

		// execution results are in
		if( event instanceof ExecutedEvent ) {
			getGameData( ).updateResults( ((ExecutedEvent) event).getResults( ) );
			
			// refresh the score table
			getUI( ).updateScores( );
			return true;
		}
		
		// not handled yet, try the super class handling
		return super.onEvent( event );
	}
	
	/**
	 * @return The scores data table
	 */
	private DataTable<Player> getData( ) {
		return getUI( ).getScores( ).getData( );
	}
	
	/**
	 * @see plangame.gwt.client.gameview.GameView#getUI()
	 */
	@Override
	public ScoreBoardUI getUI( ) {
		return (ScoreBoardUI)super.getUI( );
	}
}
