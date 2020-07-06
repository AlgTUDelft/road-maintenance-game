/**
 * @file GameView.java
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

import plangame.game.plans.PlanChange;
import plangame.game.plans.PlanException;
import plangame.game.plans.PlanTask;
import plangame.gwt.client.ClientView;
import plangame.gwt.client.gamedata.ClientGameData;
import plangame.gwt.client.gameview.dialogs.JoinGameDialogResult;
import plangame.gwt.client.resource.ClientResource;
import plangame.gwt.client.resource.locale.Lang;
import plangame.gwt.client.rpc.ClientRPCAsync;
import plangame.gwt.client.rpc.GameClientRPCAsync;
import plangame.gwt.client.util.ClientUtil;
import plangame.gwt.client.util.RPCCallback;
import plangame.gwt.client.widgets.dialogs.DialogHandler;
import plangame.gwt.shared.DebugGlobals;
import plangame.gwt.shared.GameServerConfig;
import plangame.gwt.shared.LogType;
import plangame.gwt.shared.clients.Client;
import plangame.gwt.shared.clients.Client.ClientType;
import plangame.gwt.shared.clients.GameClient;
import plangame.gwt.shared.enums.GameState;
import plangame.gwt.shared.events.Event;
import plangame.gwt.shared.events.GameServerEvent;
import plangame.gwt.shared.events.StartGameEvent;
import plangame.gwt.shared.gameresponse.JoinGameResponse;
import plangame.gwt.shared.gameresponse.RestoreResponse;
import plangame.gwt.shared.requests.JoinGameRequest;
import plangame.gwt.shared.state.GameServerInfo;
import plangame.gwt.shared.state.GameServerState;
import plangame.model.object.BasicID;

/**
 * Base client for both game manager and service providers
 *
 * @author Joris Scharpff
 */
public abstract class GameView extends ClientView implements GameViewUpdates {	
	/** Current game data known in the client */
	private ClientGameData data;
	
	/** Game server state information */
	private GameServerState serverstate;
	
	/** Game server descriptive information */
	private GameServerInfo serverinfo;

	/**
	 * Creates a new game view, stores the RPC proxy object
	 * 
	 * @param RPCProxy The RPC proxy for RPC calls to the server
	 * @param resources The client resources bundle
	 */
	protected GameView( ClientRPCAsync RPCProxy, ClientResource resources ) {
		super( RPCProxy, resources );

		// store the instance
		instance = this;
		
		// create new game data object
		data = new ClientGameData( );
		getUI( ).registerDataWidgets( data );
	}	
	
	/**
	 * @return The GameView that is running on the client's browser
	 */
	public static GameView getInstance( ) {
		assert (instance != null) : "No game client instance is running (yet)!";
		return (GameView)instance;
	}
	
	/**
	 * @return The current game data object
	 */
	public ClientGameData getGameData( ) { 
		return data;
	}
	
	/**
	 * Sets the client information from the given server state, calls 
	 * onServerStateChange after setting shared info for custom view code.
	 * 
	 * @param state The new server state
	 */
	private void setServerState( GameServerState state ) {
		// set server info
		final GameServerState oldstate = state;
		serverstate = state;
		
		// call change function
		onServerStateChange( oldstate, state );
	}
	
	/**
	 * Gets the current server state
	 * 
	 * @return The game server state information
	 */
	public GameServerState getServerState( ) {
		return serverstate;
	}
	
	/**
	 * Sets the current game server description
	 * 
	 * @param serverinfo The new server information
	 */
	public void setServerInfo( GameServerInfo serverinfo ) {
		this.serverinfo = serverinfo;
		
		onServerInfoChange( serverinfo );
		getUI( ).updateServerInfo( serverinfo );
	}
	
	/** @return The current game server info */
	public GameServerInfo getServerInfo( ) { return serverinfo; }
	
	/**
	 * Gets the current game server configuration
	 * 
	 * @return The config or null if no game server info is known
	 */
	public GameServerConfig getGameConfig( ) {
		return (getServerInfo( ) != null ? getServerInfo( ).getConfig( ) : null );
	}
	
	/**
	 * Updates the plan using the specified plan change
	 * 
	 * @param change The plan change
	 * @param validate True if the change should be validated
	 * @return The planned task that results from the change
	 * @throws PlanException if validate is true and any exception is encountered
	 */
	public PlanTask applyChange( PlanChange change, boolean validate ) throws PlanException {
		return getGameData( ).changeJointPlan( change, validate );
	}
	
	/**
	 * @return The UI widget
	 */
	@Override
	public GameViewUI getUI( ) {
		return (GameViewUI)super.getUI( );
	}
		
	/**
	 * Default action for any game view is to restore its client state after a
	 * reconnect
	 * 
	 * @see plangame.gwt.client.ClientView#onClientConnect(boolean)
	 */
	@Override
	protected void onClientConnect( boolean reconnect ) {
		if( reconnect ) restoreClientState( );
		else {
			// DEBUG connects to the test game
			if( DebugGlobals.autoConnect( ) ) {
				// uses the player name specified in the URL if possible
				final String playername = ClientUtil.getToken( "playername" );
				final JoinGameRequest request = new JoinGameRequest( getClient( ).getID( ), BasicID.makeValidID( DebugGlobals.testGameServer( ) ), (playername != null ? playername : getClient( ).getID( ).toString( ) ) );				
				joinGame( request );
			} else
				showJoinGame( );
		}
	}
	
	/**
	 * Retrieves the current server list and shows the join game dialog
	 */
	private void showJoinGame( ) {
		getUI( ).showJoinGame( getClientType( ).equals( ClientType.ServiceProvider ), new DialogHandler<JoinGameDialogResult>( ) {
			@Override
			public void OK( JoinGameDialogResult res ) {
				// try and join the game server
				final String playername = res.playername != null ? res.playername : "GM";
				final JoinGameRequest request = new JoinGameRequest( getClient( ).getID( ), res.gameID, playername );
				joinGame( request );
			}

			@Override
			public void Cancel( ) {
				disconnect( );
			}
		} );
	}
	
	/**
	 * Tries to join the game, calls the onJoin function on success and
	 * onJoinFailure if join failed
	 * 
	 * @param serverID The ID of the server to join
	 */
	protected void joinGame( JoinGameRequest request ) {
		
		getRPC( ).joinGame( request, new RPCCallback<JoinGameResponse>( Lang.text.GV_JoiningGame( ) ) {
			/**
			 * @see plangame.gwt.client.util.RPCCallback#success(java.lang.Object)
			 */
			@Override
			public void success( JoinGameResponse result ) {
				joined( result.getClient( ), result.getServerInfo( ), result.getServerState( ), false );
			}
			/**
			 * @see plangame.gwt.client.util.RPCCallback#onFailure(java.lang.Throwable)
			 */
			@Override
			public void failure( Throwable caught ) {
				onJoinFailure( caught );
			}
			
			@Override protected String getFailureText( ) { return Lang.text.GV_JoinGameFail( ); }
			
		} );
	}
	
	/**
	 * Player has joined a game server
	 * 
	 * @param client The updated client
	 * @param serverinfo The information on the game server
	 * @param serverstate The state of the game server
	 * @param reconnect True iff this join is part of a reconnect
	 */
	private void joined( Client client, GameServerInfo serverinfo, GameServerState serverstate, boolean reconnect ) {
		// client has been updated, reset it
		getClient( ).update( client );
		
		// update server state information
		setServerInfo( serverinfo );
		
		// update game state		
		setServerState( serverstate );
		
		// log the join
		log( LogType.Game, "Joined the game" + (reconnect ? " after a re-connect" : "") );
				
		// call function for view specific implementation
		onJoin( reconnect );
	}
	
	/**
	 * Called when a player has successfully joined a game. The default action is
	 * to restore the client state. Override this function for different
	 * behaviour
	 * 
	 * @param reconnect True iff this join is part of a reconnect
	 */
	protected void onJoin( boolean reconnect ) {
		// show a message
		if( !reconnect )
			notify( Lang.text.GV_Joined( getServerInfo( ).toString( ) ) );
	}
	
	/**
	 * Called when the join game failed, shows the join dialog again by default.
	 * Override this function to specify other behaviour.
	 * 
	 * @param caught The exception that is associated with the failure
	 * @return Whether the exception has been handled successfully
	 */
	protected void onJoinFailure( Throwable caught ) {
		showJoinGame( );
	}	
	
	/**
	 * Restores the client view state from the server state
	 */
	protected final void restoreClientState( ) {
		log( LogType.Verbose, "Restoring client state" );
		// check if we are connected
		if( getClient( ) == null )
			error( "Tried to get server state while not connected to any server!" );
		
		getRPC( ).restoreClient( getClient( ).getID( ), new RPCCallback<RestoreResponse>( Lang.text.GV_RestoringState( ) ) {
			@Override
			public void success( RestoreResponse result ) {
				// set all information required for the game & interface
				setServerInfo( result.getServerInfo( ) );
				setServerState( result.getServerState( ) );

				// restore game data
				getGameData( ).restore( result.getGameInfo( ), result.getJointPlan( ) );
				
				// fire onClientRestore event
				onClientRestore( result );
			}
			
			@Override protected String getFailureText( ) { return Lang.text.GV_RestoreStateFail( ); }
		} );
	}	
	
	/**
	 * Called when an event is received from the server
	 * 
	 * @param event The event that was received
	 * @return True if event has been handled, false if disconnecting
	 */
	@Override
	protected boolean onEvent( Event event ) {		
		// is this a event we can process here?
		if( event instanceof StartGameEvent ) {
			// start the game
			return handleStartGame( (StartGameEvent) event );
		} else if( event instanceof GameServerEvent ) {
			// game server event
			return handleGameEvent( (GameServerEvent) event );
		}
		
		// unknown event, try in parent view
		return super.onEvent( event );
	}	
	
	/**
	 * Handles the start of the game, calls onStartGame afterwards
	 * 
	 * @param event The start game event
	 * @return true if handled successful
	 */
	private boolean handleStartGame( StartGameEvent event ) {
		getGameData( ).restore( event.getGameInfo( ), event.getJointPlan( ) );
		
		// call onStartGame function for view custom code
		onStartGame( );
		
		// event handled successful
		return true;
	}
	
	/**
	 * Handles a game server event, returns true on success
	 * 
	 * @param event The game server event
	 * @return True iff the event is handled successfully
	 */
	protected boolean handleGameEvent( GameServerEvent event ) {
		switch( event.getEventType( ) ) {
			case Restart:
				onGameRestart( );
				return true;
				
			case End:
				onGameEnd( );
				return true;
		}
		
		// unknown event
		return false;
	}
	
	/**
	 * Called when the game server sends an restart event. Default behaviour is to
	 * restore the client state, can be overwritten for custom implementation.
	 */
	protected void onGameRestart( ) {
		log( LogType.Game, "The game server is restarting" );
		notify( Lang.text.GV_GameRestart( ) );
		restoreClientState( );
	}
	
	/**
	 * Called when the game server is ending this game. Default behaviour is to
	 * notify the client
	 */
	protected void onGameEnd( ) {
		log( LogType.Game, "The game is ending" );
		notify( Lang.text.GV_GameEnd( ) );
		
		showDisconnected( );
	}

	/**
	 * Called when the game state is changed.
	 * 
	 * @param oldstate The previous game state
	 * @param newstate The current game state
	 */
	protected void onStateChange( GameState oldstate, GameState newstate ) {
	}

	/**
	 * Override to type cast the RPC proxy
	 * 
	 * @see plangame.gwt.client.ClientView#getRPC()
	 */
	@Override
	protected GameClientRPCAsync getRPC( ) {
		return (GameClientRPCAsync)super.getRPC( );
	}
	
	/**
	 * Override to type cast the client
	 * 
	 * @see plangame.gwt.client.ClientView#getClient()
	 */
	@Override
	public GameClient getClient( ) {
		return (GameClient)super.getClient( );
	}
}
