/**
 * @file GameServer.java
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
 * @date         5 sep. 2012
 * @project      NGI
 * @company      Almende B.V.
 */
package plangame.gwt.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import plangame.game.Game;
import plangame.game.plans.JointPlan;
import plangame.game.plans.Plan;
import plangame.game.plans.PlanChange;
import plangame.game.plans.PlanError;
import plangame.game.plans.PlanStepResult;
import plangame.game.plans.PlanTask;
import plangame.game.player.PlanPreference;
import plangame.game.player.Player;
import plangame.gwt.server.events.EventManager;
import plangame.gwt.server.gametrace.GameTracer;
import plangame.gwt.server.resource.locale.Lang;
import plangame.gwt.shared.DebugGlobals;
import plangame.gwt.shared.ExecutionInfo;
import plangame.gwt.shared.GameServerConfig;
import plangame.gwt.shared.LogType;
import plangame.gwt.shared.clients.GMClient;
import plangame.gwt.shared.clients.GameClient;
import plangame.gwt.shared.clients.SBClient;
import plangame.gwt.shared.clients.SPClient;
import plangame.gwt.shared.enums.ClientState;
import plangame.gwt.shared.enums.ExecutionMode;
import plangame.gwt.shared.enums.GameState;
import plangame.gwt.shared.events.AcceptEvent;
import plangame.gwt.shared.events.AcceptedEvent;
import plangame.gwt.shared.events.AssignPortfolioEvent;
import plangame.gwt.shared.events.DisconnectEvent.DisconnectReason;
import plangame.gwt.shared.events.Event;
import plangame.gwt.shared.events.ExecutedEvent;
import plangame.gwt.shared.events.GameServerEvent;
import plangame.gwt.shared.events.GameServerEvent.GameServerEventType;
import plangame.gwt.shared.events.JoinEvent;
import plangame.gwt.shared.events.PlanChangeEvent;
import plangame.gwt.shared.events.PlanEvent;
import plangame.gwt.shared.events.ProcessPendingEvent;
import plangame.gwt.shared.events.StartGameEvent;
import plangame.gwt.shared.events.UpdateClientStateEvent;
import plangame.gwt.shared.events.UpdateStateEvent;
import plangame.gwt.shared.exceptions.GameServerConfigException;
import plangame.gwt.shared.exceptions.GameServerException;
import plangame.gwt.shared.exceptions.InvalidClientStateException;
import plangame.gwt.shared.exceptions.InvalidGameStateException;
import plangame.gwt.shared.exceptions.InvalidPlanException;
import plangame.gwt.shared.gameresponse.GameResults;
import plangame.gwt.shared.gameresponse.JoinGameResponse;
import plangame.gwt.shared.gameresponse.RestoreResponse;
import plangame.gwt.shared.requests.JoinGameRequest;
import plangame.gwt.shared.serverresponse.ConnectResponse;
import plangame.gwt.shared.state.GameInfo;
import plangame.gwt.shared.state.GameServerInfo;
import plangame.gwt.shared.state.GameServerState;
import plangame.model.object.BasicID;
import plangame.model.object.BasicObject;
import plangame.model.object.ObjectMap;
import plangame.model.tasks.Portfolio;
import plangame.model.tasks.Task;
import plangame.model.time.TimeDuration;
import plangame.planner.Instance;
import plangame.planner.Planner;
import plangame.planner.PlannerException;
import plangame.planner.dp.GreedyDPPlanner;
import plangame.planner.random.RandomPlanner;
import plangame.planner.taskplanner.TaskPlanner;
import plangame.util.gameparser.GameParser;


/**
 * @author Joris Scharpff
 */
@SuppressWarnings ("serial" )
public class GameServer extends BasicObject {
	/** The parent server */
	protected Server server;
	
	/** The game we are currently running */
	protected Game game;
	
	/** The game configuration */
	protected GameServerConfig config;
	
	/** The game file */
	protected String gamefile;

	/** The current game state */
	protected GameState state;

	/** The connected game manager */
	protected GMClient manager;
	
	/** The connected score board client */
	protected SBClient scoreboard;
		
	/** Currently connected service provider clients and their states */
	protected ObjectMap<SPClient, ClientState> clients;
		
	/** Tracer for game reconstruction */
	protected GameTracer tracer;
	
	/** Intermediate joint plan that is update on every change made by clients.
	 * When all players submitted their plans this plan will be set as the new
	 * joint plan.
	 */
	protected JointPlan intermplan;
	
	/** The current execution info */
	protected ExecutionInfo execinfo;
	
	
	/**
	 * Creates a new game server with the specified name
	 * 
	 * @param server The parent server
	 * @param ID The server ID
	 * @param name The server name
	 * @param reader The reader to load a game file
	 * @param config The game server configuration
	 * @throws GameServerException if the game file could not be loaded
	 */
	public GameServer( Server server, BasicID ID, String name, GameParser reader, GameServerConfig config ) throws GameServerException {
		super( ID, name );
				
		this.server = server;
		this.config = config;
		
		// set state to initialising
		setGameState( GameState.Initialising );
		
		// create empty client list
		clients = new ObjectMap<SPClient, ClientState>( );
				
		// load the game
		loadGame( reader );
		
		// set max clients to portfolio list size?
		if( config.getMaxNumPlayers( ) == -1 ) {
			try {
				config.setMaxNumPlayers( game.getPortfolios( ).size( ) );
			} catch( GameServerConfigException e ) {
				throw new GameServerException( ID, "Failed to set maximum player number" );
			}
		}
		
		// create game tracer if required, trace == false will construct a dummy
		// so that we don't have to check whether it is active on each use
		try {
			tracer = new GameTracer( this, config.trace( ) );
		} catch( IOException ioe ) {
			throw new GameServerException( ID, "Failed to create game tracer: " + ioe.getMessage( ) );
		}		
		tracer.gameLoaded( gamefile, config.getMaxNumPlayers( ) );
		
		log( LogType.Info, "Gameserver '" + getID( ) + "' now active" );
	}
	
	/**
	 * Loads the game using the specified reader
	 * 
	 * @param reader The GameReader that is used to load the game
	 * @throws GameServerException if the game failed to load
	 */
	public void loadGame( GameParser reader ) throws GameServerException {
		// load the game
		try {
			// try to read the game
			this.game = reader.read( getID( ) );
			this.gamefile = reader.getInputFile( );
			log( LogType.Info, "Game file '" + gamefile + "' loaded" );
			
			// check if there are enough portfolios for all players
			final int maxplayers = getConfiguration( ).getMaxNumPlayers( );
			if( game.getPortfolios( ).size( ) > maxplayers && maxplayers != -1 )
				log( LogType.Warning, "Not enough portfolios (" + game.getPortfolios( ).size( ) + ") defined in the game for the maximum number of clients (" + maxplayers + ")" );
			
		} catch( Exception e ) {
			log( LogType.Error, "Failed to load game file '" + reader.getInputFile( ) + "'" );
			
			System.out.println( e.getMessage( ) );
			e.printStackTrace( );
			throw new GameServerException( getID( ), e.getMessage( ) );
		}
	}
	
	/**
	 * Ends the current game in progress
	 * 
	 * @return The number of notified clients
	 * @throws GameServerException if no game is running
	 */
	public int endGame( ) throws GameServerException {
		if( game == null ) throw new GameServerException( getID( ), Lang.get( "NoGameToEnd" ) );
		
		// notify all the clients that the game is stopping
		final int notified = getEM( ).fireEvent( this, new GameServerEvent( GameServerEventType.End ) );
		
		// clear the current game
		game = null;
		gamefile = null;
		
		// trace the event
		tracer.gameEnded( );
		
		// return the number of notified clients
		return notified;
	}
	
	/**
	 * Sets the game manager client for the current game
	 * 
	 * @param manager The new game manager
	 */
	public void setManager( GMClient manager ) {
		this.manager = manager;
	}
	
	/**
	 * @return The game manager client
	 */
	public GMClient getManager( ) {
		return manager;
	}
	
	/**
	 * Sets the score board client for the current game
	 * 
	 * @param scoreboard The new score board client
	 */
	public void setScoreBoard( SBClient scoreboard ) {
		this.scoreboard = scoreboard;
	}
	
	/**
	 * @return The score board client
	 */
	public SBClient getScoreBoard( ) {
		return scoreboard;
	}
	
	/**
	 * @return The list of connected Service Provider clients
	 */
	public List<SPClient> getClients( ) {
		return clients.getKeys( );
	}
	
	/**
	 * Retrieves information of the current server
	 * 
	 * @return The server information
	 */
	public GameServerInfo getServerInfo( ) {
		return new GameServerInfo( getID( ), game.toString( ), game.getDescription( ), getGameFile( ), getPlayerCount( ), getConfiguration( ) );
	}
	
	/**
	 * Retrieves information about the current game state
	 * 
	 * @return The server state
	 */
	public GameServerState getServerState( ) {
		return new GameServerState( getServerInfo( ), state, clients );
	}
	
	/**
	 * Retrieves the static game information
	 * 
	 * @return The game info
	 */
	public GameInfo getGameInfo( ) {
		return new GameInfo( game.getID( ), game.getPlayers( ), getPortfolios( ), game.getPeriod( ), game.getInfra( ), game.getMechanism( ) );
	}
	
	/**
	 * Retrieves the clients with the specified ID
	 * 
	 * @param clientID The client ID
	 * @return The clients or null if no match has been found
	 */
	public SPClient getClientByID( BasicID clientID ) {
		return BasicObject.fromList( clients.getKeys( ), clientID );
	}
	
	/**
	 * Reconnects a client to the game server
	 * 
	 * @param client The client that is reconnecting
	 * @return The connection response
	 * @throws GameServerException if the client type is unknown
	 */
	public ConnectResponse reconnect( GameClient client ) throws GameServerException {
		// check what client is reconnecting
		final boolean connected;
		if( client instanceof SPClient ) {
			// check if the client is still in the list
			final SPClient spc = getClientByID( client.getID( ) );
			connected = (spc != null);
		} else if( client instanceof GMClient ) {
			// check if this client was the manager
			connected = (getManager( ) != null) && getManager( ).equals( client );
		} else if( client instanceof SPClient ) {
			// check if this client was the score board
			connected = (getScoreBoard( ) != null) && getScoreBoard( ).equals( client );
		} else {
			final String msg = Lang.get( "UnknownClientType" );
			log( LogType.Error, msg );
			throw new GameServerException( getID( ), msg );
		}

		if( !connected ) {
			final String msg = Lang.get( "ClientWasNotInGame" );
			log( LogType.Verbose, msg );
			throw new GameServerException( getID( ), msg );
		}
		
		// notify GM of reconnect of players
		if( client instanceof SPClient )
			setClientState( (SPClient)client, ClientState.Reconnecting );

		// restore the session
		return null;// new ConnectResponse( server.getID( ), client, getServerInfo( ) );
	}
		
	/**
	 * Adds a clients to the game if possible
	 * 
	 * @param client The client that wants to join
	 * @param request The join game request info
	 * @return The corresponding client object of the game
	 * @throws GameServerException if the join failed
	 */
	@SuppressWarnings("null")
	public JoinGameResponse join( GameClient client, JoinGameRequest request ) throws GameServerException {
		if( client == null )
			logThrow( Lang.get( "JoinNullClient" ) );
		
		// might be rejoin after disconnect, check if the client was connected
		boolean wasconnected = false;
		wasconnected |= client.equals( getManager( ) );
		for( GameClient c : getClients( ) )
			wasconnected |= client.equals( c );
		
		// only try new connection if it was not connected
		if( wasconnected ) {
			if( client instanceof SPClient )
				setClientState( (SPClient) client, ClientState.Reconnecting );
		
			return new JoinGameResponse( getID( ), getServerInfo( ), getServerState( ), client );
		}
		
		// check what type of client wants to join
		if( client instanceof SPClient ) {
			// Service Provider
			final SPClient sp = (SPClient)client;
			
			// check what state we are in, only join in the Start state
			if( state != GameState.Initialising )
				logThrow( Lang.get( "JoinRunningGame" ), LogType.Info );			
			
			// check if we are still allowed to add a client
			if( getPlayerCount( ) == getConfiguration( ).getMaxNumPlayers( ) )
				logThrow( Lang.get( "JoinGameFull" ), LogType.Info );
			
			// add client to the game
			clients.put( sp, ClientState.Initialising );
			
			// create a player
			final Player player = new Player( client.getID( ), request.getPlayerName( ) );
						
			// add the player to the game
			addPlayer( sp, player );

			// notify the GM and SB of the join
			notifyGM( new JoinEvent( client ), true );
			
			// if auto assign is on, the player will be assigned the first available
			// portfolio
			if( DebugGlobals.autoAssign( ) ) {
				for( Portfolio pf : game.getPortfolios( ) ) {
					if( pf.getPlayer( ) != null )
						continue;
					
					assignPortfolio( sp, pf.getID( ) );
					break;
				}
			} else {
				setClientState( sp, ClientState.AwaitingPortfolio );
			}

			log( LogType.Game, "Service Provider client '" + client + "' joined the game!" );
		} else if( client instanceof GMClient ) {
			// Game Manager
			final GMClient gm = (GMClient)client;
			
			// check if there's already a GM active
			if( getManager( ) != null && !getManager( ).equals( gm ) ) {
				/* Removed: GM's are always allowed to connect, possibly replacing the current GM
				logThrow( Lang.get( "GMAlreadyConnected", getManager( ).getID( ).toString( ) ), LogType.Info ); */
				
				// log the replace
				log( LogType.Warning, "Game manager '" + gm + "' is replacing current manager '" + getManager( ) );
			}
			
			// set the game manager			
			setManager( gm );
			log( LogType.Game, "Gamemanager '" + gm.getID( ) + "' joined the game!" );			
		} else if( client instanceof SBClient ){
			// Score Board
			final SBClient sb = (SBClient) client;
			
			// check if there was another score board active
			if( getScoreBoard( ) != null && !getScoreBoard( ).equals( sb ) )
				log( LogType.Warning, "Score board '" + sb + "' is replacing current score board '" + getScoreBoard( ) );
			
			// set the score board
			setScoreBoard( sb );
			log( LogType.Game, "Score board '" + sb.getID( ) + "' is now monitoring the game!" );			
		} else {
			// unknown
			logThrow( Lang.get( "UnknownClientType" ) );
		}
		
		// set game ID for the client, marking it connected
		client.setGameID( getID( ) );
		
		// return the result
		return new JoinGameResponse( getID( ), getServerInfo( ), getServerState( ), client );
	}
	
	/**
	 * Sets the player and portfolio for the client
	 * 
	 * @param client The client
	 * @param player Its player information
	 * @throws GameServerException if the client already has a player in the game
	 */
	@SuppressWarnings("null")
	public void addPlayer( SPClient client, Player player ) throws GameServerException {
		// check if the player is null
		if( player == null )
			logThrow( Lang.get( "PlayerNull" ) );
		
		// check if the client does not already have a player in this game
		if( game.getPlayerByID( player.getID( ) ) != null )
			logThrow( Lang.get( "PlayerAlreadyInGame", player.getID( ).toString( ) ) );
				
		// link the client to a game client
		game.addPlayer( player );
		client.setPlayer( player );
		
		// trace the action
		tracer.playerAdded( player );
		
		log( LogType.Game, "Player '" + client + "' joined the game" );
	}
	
	/**
	 * Assigns the portfolio to the specified client
	 * 
	 * @param client The client
	 * @param pfID The ID of the portfolio to assign
	 * @throws GameServerException if either the portfolio does not exist, the
	 * portfolio was already assigned to a client or the client was already
	 * assigned a different portfolio
	 */
	@SuppressWarnings("null")
	public void assignPortfolio( SPClient client, BasicID pfID ) throws GameServerException {
		// get the portfolio by its ID
		final Portfolio portfolio = game.getPortfolioByID( pfID );
		
		// check if the portfolio is part of this game
		if( portfolio == null )
			logThrow( Lang.get( "UnknownPortfolio", pfID.toString( ) ) );
		
		// and not assigned already to another player
		if( portfolio.getPlayer( ) != null && !client.getPlayer( ).equals( portfolio.getPlayer( ) ) )
			logThrow( Lang.get( "PortfolioAlreadyAssigned", portfolio.getDescription( ), portfolio.getPlayer( ).getDescription( ) ) );

		// set the portfolio owner
		game.setPortfolioOwner( client.getPlayer( ), portfolio );
		
		// notify the client and GM of its new portfolio
		final AssignPortfolioEvent pfevent = new AssignPortfolioEvent( client, portfolio );
		getEM( ).fireEvent( getID( ), client, pfevent );
		notifyGM( pfevent, true );
		
		// set the client state to ready for start
		setClientState( client, ClientState.WaitingToStart );
				
		// trace & log
		tracer.portfolioAssigned( client.getPlayer( ), pfID );
		log( LogType.Game, "Assigned portfolio '" + portfolio.getDescription( ) + "' to player '" + client + "'" ); 
	}
	
	/**
	 * Disconnects the client from the game
	 * 
	 * @param client The client to disconnect
	 * @param reason The reason for disconnecting, null if client disconnected
	 * @return True if disconnect was successful
	 * @throws GameServerException if the client is not part of this game
	 */
	public boolean disconnect( GameClient client, DisconnectReason reason ) throws GameServerException {		
		// remove the player from the game
		if( client instanceof SPClient ) {
			final SPClient sp = (SPClient)client;
			
			// remove the client from the list
			if( clients.remove( sp ) != null )
				throw new GameServerException( getID( ), Lang.get( "DisconnectNotInGame", client.getID( ).toString( ) ) );			
			
			removePlayer( sp.getPlayer( ) );
		} else if( client instanceof GMClient ) {
			final GMClient gm = (GMClient)client;
			
			if( !gm.equals( manager ) )
				throw new GameServerException( getID( ),  Lang.get( "DisconnectNotInGame", client.getID( ).toString( ) )  );
			
			setManager( null );
		} else if( client instanceof SBClient ) { 
			final SBClient sb = (SBClient) client;
			
			if( !sb.equals( manager ) )
				throw new GameServerException( getID( ),  Lang.get( "DisconnectNotInGame", client.getID( ).toString( ) )  );
			
			setScoreBoard( null );			
		} else {
			throw new GameServerException( getID( ),  Lang.get( "UnknownClientType" ) );
		}
		
		// notify game manager of the disconnect (this is only successful if it was
		// not the manager disconnecting)
		if( client instanceof SPClient ) {
			notifyGM( new UpdateClientStateEvent( (SPClient)client, ClientState.Disconnected ), true );
		}
		
		return true;
	}
	
	/**
	 * Client requests all relevant server and game info to restore its view
	 * 
	 * @param client The game client that is restoring
	 * @return The RestoreResponse object containing all required information
	 */
	public RestoreResponse restoreClient( GameClient client ) {
		// if this is an SP, include possible intermediate plan changes
		final JointPlan jplan = new JointPlan( getJointPlan( ) );
		if( (client instanceof SPClient) && intermplan != null )
			jplan.setPlan( intermplan.getPlan( ((SPClient)client).getPlayer( ) ) );
		
		// if this is a GM, include possible pending step results, this is null if
		// not in execution
		final ExecutionInfo einfo = ((client instanceof GMClient) ? execinfo : null );
		
		// send back the response
		return new RestoreResponse( getID( ), getServerInfo( ), getServerState( ), getGameInfo( ), jplan, einfo );
	}
	
	/**
	 * Removes a player from the game
	 * 
	 * @param player The player to remove
	 * @return True if successful
	 */
	private boolean removePlayer( Player player ) {
		if( !game.removePlayer( player ) )
			return false;
		
		// trace the removal of the player
		tracer.playerRemoved( player );
		return true;
	}
	
	/**
	 * @return The current joint plan
	 */
	public JointPlan getJointPlan( ) {
		return game.getJointPlan( );
	}
	
	/**
	 * Starts the game, notifies all connected clients
	 * 
	 * @throws GameServerException if the game could not be started (no players
	 * or players without portfolio)
	 */
	public void startGame( ) throws GameServerException {
		// check if there is at least one player
		if( game.getPlayers( ).size( ) == 0 )
			logThrow( Lang.get( "StartNoPlayers" ) );			

		// check if all players have a portfolio assigned
		for( Player p : game.getPlayers( ) )
			if( p.getPortfolio( ) == null )
				logThrow( Lang.get( "StartUnassigned" ) );		

		// change state to starting
		setGameState( GameState.Starting );
	}
			
	/**
	 * Initiates a new plan round
	 * 
	 * @throws InvalidGameStateException if starting a new plan round is not
	 * allowed in the current state
	 */
	public void initPlanRound( ) throws InvalidGameStateException {
		// check if this action is allowed in the current state
		checkState( GameState.Idle );
		
		// start new planning round 
		log( LogType.Game, "Starting a new plan round" );
		setGameState( GameState.Planning );
	}
		
	/**
	 * Receives a plan from a client
	 * 
	 * @param client The client submitting the plan
	 * @param plan The plan
	 * @return The joint plan resulting from all the submitted joint plan
	 */
	public void submitPlan( SPClient client, Plan plan ) {
		// player might be lagging behind a little, ignore submit if so
		if( !inState( GameState.Planning ) || !inState( client, ClientState.InPlanning ) ) {
			// ignore the submit, but log the ignored report
			log( LogType.Warning, "Client '" + client.getDescription( ) + "' submitted plan in wrong state (Game: " + getGameState( ).toString( ) + ", Client: " + getClientState( client ).toString( ) + ")" );
			return;
		}

		// store the client response in the intermediate plan
		intermplan.setPlan( plan );

		// log & trace
		log( LogType.Game, "Player " + client + " has submitted his plan" );
		tracer.planSubmitted( game.getTime( ), client.getPlayer( ) );		
		
		// set the client state to submitted to acknowledge receipt of the plan
		setClientState( client, ClientState.Submitted );
	}
	
	/**
	 * Receives a player accept / decline response
	 * 
	 * @param client The client
	 * @param accept True if the client accepts the current joint plan
	 */
	public void acceptPlan( SPClient client, boolean accept ) {
		// player might be lagging behind a little, ignore submit if so
		if( !inState( GameState.Accept ) || !inState( client, ClientState.Accepting ) ) {
			// ignore the submit, but log the ignored report
			log( LogType.Warning, "Client '" + client.getDescription( ) + "' accepted/declined plan in wrong state (Game: " + getGameState( ).toString( ) + ", Client: " + getClientState( client ).toString( ) + ")" );
			return;
		}

		// player has responded
		tracer.playerResponded( game.getTime( ), client.getPlayer( ), accept );		
		
		// set the client state based on its response
		setClientState( client, (accept ? ClientState.Accepted : ClientState.Declined ) );		
	}	
	
	/**		
	 * Uses the planning tool to compute a suggestion for the task or the entire
	 * plan
	 * 
	 * @param client The client to find a suggestion for
	 * @param jplan The current joint plan
	 * @param task The task to find suggestion for, null for entire plan
	 * @param pref The planning preference for the player
	 * @return The suggested plan
	 * @throws PlannerException if the planner failed
	 */
	public Plan getSuggestion( SPClient client, JointPlan jplan, Task task, PlanPreference pref ) throws PlannerException {
		final Instance inst = new Instance( game, client.getPlayer( ) );
		inst.setJointPlan( jplan );
		inst.setPlanPreference( client.getPlayer( ), pref );
		
		// only plan for the task methods of the task
		if( task != null ) {
			inst.addFixedMethods( inst.getPlanMethods( ) );
			inst.addPlanMethods( task.getMethods( ) );
		}
		
		// trace the suggestion
		tracer.requestedSuggestion( game.getTime( ), client.getPlayer( ), task, pref );
		
		// run the planner and return the result
		final Planner planner;
		if( task != null )
			planner = new TaskPlanner( );
		else
			planner = new GreedyDPPlanner( );
		
		// find suggestion for the player
		return planner.plan( inst ).getPlan( client.getPlayer( ) );
	}
	
	/**
	 * The client notifies the server of a change in his plan
	 * 
	 * @param client The client notifying
	 * @param change The plan change
	 * @throws InvalidGameStateException if the state is not planning
	 * @throws InvalidClientStateException if the client is not in the planning state
	 */
	public void planChanged( SPClient client, PlanChange change ) throws InvalidGameStateException, InvalidClientStateException {
		// make sure we are in the correct state
		checkState( GameState.Planning );
		checkClientState( client, ClientState.InPlanning );
				
		// update the intermediate plan
		intermplan.applyChange( change, false );
		
		// notify SB of change
		if( getScoreBoard( ) != null )
			getEM( ).fireEvent( getID( ), getScoreBoard( ), new PlanChangeEvent( client, change ) );
		
		// trace the change
		tracer.planChanged( game.getTime( ), client.getPlayer( ), change );
	}
	
	/**
	 * Initialises the execution of the game
	 *
	 * @param execinfo The execution info
	 * @throws InvalidGameStateException if the server was not expecting an
	 * execute event
	 * @throws InvalidPlanException if the current joint plan is not valid
	 */
	public void initExecution( ExecutionInfo execinfo ) throws InvalidGameStateException, InvalidPlanException {
		checkState( GameState.Idle );
		
		// check if the joint plan is valid
		final List<PlanError> errors = getJointPlan( ).validate( );
		if( errors.size( ) != 0 )
			throw new InvalidPlanException( Lang.get( "ExecuteInvalidPlan" ), errors );
		
		// set the execution mode and sleep
		this.execinfo = new ExecutionInfo( execinfo );
		
		// set the game state
		setGameStateExecuting( );
	}
	
	/**
	 * Stops the current running execution of the game
	 * 
	 * @throws InvalidGameStateException if the state is not executing
	 */
	public void stopExecution( ) throws InvalidGameStateException {
		checkState( GameState.Executing );
		
		// log
		log( LogType.Game, "Game manager interrupted execution (" + game.getTime( ).toString( ) + ")!" );
		tracer.stoppedExecution( game.getTime( ) );
		
		// set execution mode to one round to stop execution after
		execinfo.setMode( ExecutionMode.OneRound );
	}

	/**
	 * GM sends the updated PlanStepResults in which pending methods have been
	 * handled.
	 * 
	 * @param results The PlanStepResults
	 * @param stop True to stop after the processing
	 * @throws GameServerException if there are still methods pending delay
	 * @throws InvalidPlanException if the now resulting joint plan is invalid
	 * @throws InvalidGameStateException if the state is not executing
	 */
	public void handlePending( PlanStepResult results, boolean stop ) throws GameServerException, InvalidGameStateException, InvalidPlanException {
		checkState( GameState.Executing );
		
		// update the stored execution results
		execinfo.updateResult( results );
		
		// end the current game round
		endStep( );
		
		// and continue if possible
		if( !stop ) {
			continueExec( );
		} else {
			endExecution( );
		}
	}
	
	/**
	 * Notifies the game manager and possibly the score board of any event
	 * 
	 * @param event The event to send to the GM
	 * @param scoreboard True to also notify the score board
	 * @return True if it was send successfully
	 */
	private boolean notifyGM( Event event, boolean scoreboard ) {
		if( scoreboard && getScoreBoard( ) != null )
			getEM( ).fireEvent( getID( ), getScoreBoard( ), event );
		
		if( getManager( ) == null ) return false;
		return getEM( ).fireEvent( getID(), getManager( ), event );
	}
	
	/**
	 * @return The current game state
	 */
	protected GameState getGameState( ) {
		return state;
	}
	
	/**
	 * Sets the current game state, not to be used for executing
	 * 
	 * @param newstate The new game state
	 */
	protected void setGameState( GameState newstate ) {
		if( newstate == GameState.Executing )
			throw new RuntimeException( "Use setGameStateExecuting instead!" );
		
		state = newstate;
		log( LogType.Verbose, "Server changed state to '" + state.toString( ) + "'" );
				
		// notify GM of game state change
		notifyGM( new UpdateStateEvent( newstate ), false );
		
		// call onGameStateChange to perform new state
		onGameStateChange( newstate );
	}
	
	/**
	 * Switches the current game state to executing, explicit function because it
	 * can throw an exception if execution can not be done (anymore)
	 * 
	 * @throws InvalidPlanException if the state is excecuting and the plan is
	 * not valid
	 */
	protected void setGameStateExecuting( ) throws InvalidPlanException {
		state = GameState.Executing;
		log( LogType.Verbose, "Server changed state to '" + state.toString( ) + "'" );
				
		// notify GM of game state change
		notifyGM( new UpdateStateEvent( GameState.Executing ), false );
		
		// call onGameStateChange to perform new state
		stateExecuting( );		
	}
		
	/** @return The game that is running on this server */
	public Game getGame( ) {
		return game;
	}
	
	/** @return The game file */
	public String getGameFile( ) {
		return gamefile;
	}
	
	/** @return The number of playing clients */
	public int getPlayerCount( ) {
		return getClients( ).size( );
	}
	
	/** @return The game configuration settings */
	public GameServerConfig getConfiguration( ) {
		return config;
	}

	/**
	 * Checks the current server state throws exception if invalid
	 * 
	 * @param checkstates The state(s) in which the server should be
	 * @throws InvalidGameStateException if the state is invalid
	 */
	protected void checkState( GameState... checkstates ) throws InvalidGameStateException {
		if( !inState( checkstates ) ) {
			log( LogType.Warning, "The game server is not in the required state (current state: " + state + ")" );
			throw new InvalidGameStateException( Lang.get( "InvalidGameState" ), state );
		}		
	}
	
	/**
	 * Checks the current client state and throws exception if invalid
	 * 
	 * @param client The client
	 * @param checkstates The state(s) in which the server <i>should</i> be
	 * @throws InvalidClientStateException if the state is invalid
	 */
	protected void checkClientState( SPClient client, ClientState... checkstates ) throws InvalidClientStateException {
		if( !inState( client, checkstates ) ) {
			log( LogType.Warning, "The client '" + client.getDescription( ) + "' not in the required state (current state: " + state + ")" );
			throw new InvalidClientStateException( Lang.get( "InvalidClientState" ), client, state );
		}		
	}
	
	/**
	 * Logs a message of the specified type
	 * 
	 * @param type The type of the log message
	 * @param message The message to log
	 * @return True iff the message is logged
	 */
	public boolean log( LogType type, String message ) {
		return server.logGameServerMessage( getID( ), message, type );
	}
	
	/**
	 * Throws a GameServerException and logs it with the specified log type
	 * 
	 * @param errormsg The error description
	 * @param logtype The log message type
	 * @throws GameServerException
	 */
	private void logThrow( String errormsg, LogType logtype ) throws GameServerException {
		log( logtype, errormsg );
		throw new GameServerException( getID( ), errormsg );		
	}
	
	/**
	 * Throws a GameServerException and logs it as an error
	 * 
	 * @param errormsg The error description
	 * @throws GameServerException the exception to throw
	 */
	private void logThrow( String errormsg ) throws GameServerException {
		logThrow( errormsg, LogType.Error );
	}
	
	/**
	 * @return The event manager (of the parent server)
	 */
	private EventManager getEM( ) {
		return server.getEM( );
	}
	
	/**
	 * Sets the status for all SP clients and notifies the GM (once)
	 * 
	 * @param state The new status
	 */
	private void setClientStates( ClientState state ) {
		// set states for clients and notify them individually about their change
		for( SPClient c : clients.getKeys( ) ) {
			clients.put( c, state );
			getEM( ).fireEvent( getID( ), c, new UpdateClientStateEvent( c, state ) );
		}
		
		// notify GM of all changes in one event
		notifyGM( new UpdateClientStateEvent( clients.getKeys( ), state ), false );
		
		// call on client state change to check for possible state transitions
		onClientStateChange( clients.getKeys( ), state );
	}
	
	/**
	 * Sets the SP client's status and notifies the client and the GM of the new
	 * state
	 * 
	 * @param spclient The SP client
	 * @param state The new status
	 */
	private void setClientState( SPClient client, ClientState state ) {
		// change the state
		clients.put( client, state );
		
		// notify SP and client
		final UpdateClientStateEvent update = new UpdateClientStateEvent( client, state );
		getEM( ).fireEvent( getID( ), client, update );
		notifyGM( update, false );
		
		// call on client state change to check for possible state transitions
		final List<SPClient> clients = new ArrayList<SPClient>( );
		clients.add( client );
		onClientStateChange( clients, state );
	}
		
	/**
	 * Returns the current state of the SP client
	 * 
	 * @param spclient The SP Client
	 * @return The SPState object for the client
	 */
	protected ClientState getClientState( SPClient client ) {
		return clients.get( client );
	}
	
	/**
	 * Forces the game to change to the specified state, sends out a update
	 * message to notify the GM of the change
	 * 
	 * @param state The new game state
	 * @return The previous game state
	 */
	public GameState forceGameState( GameState state ) {
		final GameState oldstate = getGameState( );
		setGameState( state );
		
		// log & trace the state change
		log( LogType.Game, "Forced game state to change to '" + state.toString( ) + "' (from '" + (oldstate != null ? oldstate.toString( ) : "null") + "')" );
		tracer.forcedGameState( oldstate, state );
		
		return oldstate;
	}
	
	/**
	 * Forces the game to change the state of the specified client, sends out
	 * an update to both the client and the GM to notify them of the change
	 * 
	 * @param client The client to update the state of
	 * @param state The new state
	 * @return The previous client state
	 * @throws GameServerException if the client state was unknown to the server,
	 * however after the state is set to make sure we can always enforce a new
	 * state
	 */
	public ClientState forceClientState( SPClient client, ClientState state ) throws GameServerException {
		// get old status
		final ClientState oldstate = getClientState( client );
		setClientState( client, state );

		// trace the state change
		log( LogType.Game, "Forced client '" + client + "' state to change to '" + state.toString( ) + "' (from '" + (oldstate != null ? oldstate.toString( ) : "null") + "')" );
		tracer.forcedClientState( client, oldstate, state );
		
		// throw exception if the client had no previous state
		if( oldstate == null )
			throw new GameServerException( server.getID( ), Lang.get( "ClientStateUnknown" ) );
		
		// return the old state
		return oldstate;		
	}
	
	/**
	 * Checks if the game is in one of the specified states
	 * 
	 * @param states The list of states
	 * @return True if the game state is one of the specified states
	 */
	private boolean inState( GameState...states ) {
		for( GameState gs : states )
			if( gs.equals( getGameState( ) ) ) return true;
			
		return false;
	}
	
	/**
	 * Checks if the client is in one of the specified states
	 * 
	 * @param client The client to check
	 * @param states The set of allowed states
	 * @return True if the client state is one of the states
	 */
	private boolean inState( SPClient client, ClientState... states ) {		
		// at least one of the specified states must match the current client state
		final ClientState cs = getClientState( client );
		for( ClientState s : states )
			if( cs.equals( s ) ) return true;
		
		// none matched, return false	
		return false;		
	}
	
	/**
	 * Returns true if the state of each client matches one of the specified
	 * states
	 * 
	 * @param clients The clients
	 * @param states The set of allowed states
	 * @return True iff all clients states are in the allowed set
	 */
	private boolean inState( List<SPClient> clients, ClientState... states ) {
		// check for all the clients
		for( SPClient c : clients )
			if( !inState( c, states )  ) return false;
		
		return true;
	}
	
	/**
	 * Returns true if at least one of the clients matches one of the specified
	 * states
	 * 
	 * @param clients The clients
	 * @param states The set of states
	 * @return True if at least one client is in any of the states
	 */
	private boolean anyInState( List<SPClient> clients, ClientState... states ) {
		for( SPClient client : clients )
			for( ClientState state : states ) 
				if( getClientState( client ).equals( state ) )
					return true;
		
		return false;
	}
	
	/**
	 * Returns all the portfolios defined by the game
	 * 
	 * @return The list of portfolios
	 */
	public List<Portfolio> getPortfolios( ) {
		return game.getPortfolios( );
	}
	
	/**
	 * Called when the game state is updated
	 * 
	 * @param newstate The new game state
	 */
	private void onGameStateChange( GameState newstate ) {
		switch( newstate ) {
			case Initialising: stateInitialise( ); break;
			case Starting: stateStarting( ); break;
			case Idle: stateIdle( ); break;
			case Planning: statePlanning( ); break;
			case Accept: stateAccepting( ); break;
			case Executing: throw new RuntimeException( "Can only be called directly!" );
			case Finished: stateFinished( ); break;
		}
	}
	
	/**
	 * Called when one or more client states are updated
	 * 
	 * @param clients The client affected by the update
	 * @param nestate The new state of the clients
	 */
	private void onClientStateChange( List<SPClient> clients, ClientState newstate ) {
		// log the client state change
		for( SPClient client : clients )
			log( LogType.Verbose, "Changed state of client '" + client  + "' to " + newstate.toString( ) );
		
		// check for state transitions
		// if all players have now submitted, transition to accept phase
		if( inState( getClients( ), ClientState.Submitted ) ) {
			log( LogType.Game, "All players have submitted their plans, starting accept/decline round" );
			tracer.submittedAll( game.getTime( ) );			
			setGameState( GameState.Accept );
			return;
		}
		
		// check the accept phase states
		// if all players accepted, we can accept the joint plan
		if( inState( getClients( ), ClientState.Accepted ) ) {
			acceptPlan( true );
			return;
		}
		
		// not all players have accepted, depending on the game mode replan
		// immediately on any decline or wait for all responses
		if( (config.singleDecline( ) && anyInState( clients, ClientState.Declined )) ||
				(!config.singleDecline( ) && inState( getClients(), ClientState.Accepted, ClientState.Declined )) ) {
			acceptPlan( false );
			return;
		}
	}
	                                                                                  

	/**
	 * Initialise state
	 */
	private void stateInitialise( ) {
		// nothing to do but wait
	}
	
	/**
	 * Starting the game
	 */
	private void stateStarting( ) {
		// trace the game event
		log( LogType.Game, "The game is ready to start!" );
		tracer.gameStarted( getClients( ).size( ), game.getPeriod( ).getWeeks( ) );
						
		// DEBUG initialise a random joint plan
		if( DebugGlobals.autoInitialPlan( ) ) {
			final RandomPlanner p = new RandomPlanner( );
			try {
				getJointPlan( ).update( p.plan( new Instance( game ) ) );
			} catch( PlannerException e ) {
				log( LogType.Warning, "Failed to generate initial joint plan: " + e.getMessage( ) );
			}
		}
		
		// notify the GM of the start
		final JointPlan jplan = new JointPlan( getJointPlan( ) );
		getEM( ).fireEvent( this, true, true, new StartGameEvent( getGameInfo( ), jplan ) );		
		
		// change the state to idle
		setGameState( GameState.Idle );
	}
	
	/**
	 * Start of a new round
	 */
	private void stateIdle( ) {		
		// update client states
		setClientStates( ClientState.Idle );		
	}
	
	/**
	 * Planning state 
	 */
	private void statePlanning( ) {
		// make a copy of the current joint plan to set as the new intermediate
		// plan
		intermplan = new JointPlan( getJointPlan( ) );
		
		// send out the start plan event
		getEM( ).fireEvent( this, new PlanEvent( true ) );

		// update client states
		setClientStates( ClientState.InPlanning );
		
		// trace the game play
		tracer.startedPlanning( game.getTime( ) );
	}
	
	/**
	 * Acceptance state
	 */
	private void stateAccepting( ) {
		// set the joint plan to match the intermediate plan
		if( intermplan != null ) {
			getJointPlan( ).update( intermplan );
			intermplan = null;
		} else {
			log( LogType.Warning, "Accept state started but no intermediate joint plan was set" );
		}
		
		// send out the accept event so they have the latest joint plan
		getEM( ).fireEvent( this, false, true, new AcceptEvent( getJointPlan( ) ) );
		
		// set client states
		setClientStates( ClientState.Accepting );
	}
	
	/**
	 * Handles the response after the accept round
	 * 
	 * @param accept True if the plan should be accepted
	 */
	private void acceptPlan( boolean accept ) {		
		// log & trace
		log( LogType.Game, "All players have responded to the joint plan, the plan has been " + (accept ? "accepted" : "declined") );
		tracer.respondedAll( game.getTime( ), accept );

		// notify all players and GM of the result
		getEM( ).fireEvent( this, true, false, new AcceptedEvent( accept ) );
		
		// check if all the clients have accepted the joint plan
		if( !accept ) {
			// no, replan
			setGameState( GameState.Planning );
		} else {
			// go to the idle
			setGameState( GameState.Idle );			
		}
	}
	
	/**
	 * Starts the execute state
	 * 
	 * @throws InvalidPlanException if the state cannot be set to executing
	 */
	private void stateExecuting( ) throws InvalidPlanException {
		// check execution mode, might not be set when forced into the state
		if( execinfo == null ) {
			log( LogType.Warning, "Execution started but no mode was specified, assuming one round execution mode." );
			execinfo.setMode( ExecutionMode.OneRound );
		}
		
		// log and trace
		log( LogType.Game, "Starting execution of the joint plan!" );
		tracer.startedExecution( game.getTime( ), execinfo.getMode( ) );
		
		// update client states
		setClientStates( ClientState.Executing );
		
		// execute the plan!
		try {
			execute( );
		} catch( InvalidGameStateException e ) { /* never thrown here */ }
		catch( InvalidPlanException ipe ) {
			// cannot execute current plan (anymore), switch back to idle state and
			// pass the exception
			setGameState( GameState.Idle );
			throw ipe;
		}
	}
	
	/**
	 * Executes and processes one week of the game
	 * 
	 * @param mode The execution mode
	 * @throws InvalidGameStateException if the game is not executing
	 * @throws InvalidPlanException if the joint plan is invalid to execute
	 * 
	 */
	private void execute( ) throws InvalidGameStateException, InvalidPlanException {		
		checkState( GameState.Executing );
		
		// validate the current plan
		final List<PlanError> errors = getJointPlan( ).validate( );
		if( errors.size( ) != 0 )
			throw new InvalidPlanException( Lang.get( "ExecuteInvalidPlan" ), errors );
		
		// execute one step
		final PlanStepResult result = game.step( new TimeDuration( 1 ) );
		execinfo.setResult( result );

		// check if we are done
		if( result == null ) {
			setGameState( GameState.Finished );
			return;
		}
		
		// log and trace
		log( LogType.Game, "Joint plan has been executed in week " + game.getTime( ).toString( ) );
		tracer.roundExecuted( game.getTime( ), result );
				
		
		// randomly process pending methods if required
		if( execinfo.isMode( ExecutionMode.Continuous ) && result.hasTasksPending( ) ) {
			while( result.hasTasksPending( ) ) {
				final PlanTask pt = result.getPending( ).get( 0 );
				result.delayTask( pt, ((new Random( )).nextDouble( ) < pt.getMethod( ).getDelayRisk( ) ) );
			}
		}
		
		// process pending methods if required
		if( result.hasTasksPending( ) ) { 
			sendProcessRequest( );
		} else {
			try {
				endStep( );
				if( !execinfo.isMode( ExecutionMode.OneRound ) )
					continueExec( );
				else
					endExecution( );
			} catch( GameServerException e ) { /* never thrown */	}
		}
	}
	
	/**
	 * Sends out a request to the GM to process delay pending methods
	 */
	private void sendProcessRequest( ) {
		// send an event to the GM to process the results
		notifyGM( new ProcessPendingEvent( execinfo.getPendingResults( ) ), false );
	}
	
	/**
	 * Ends the current step of the execution
	 * 
	 * @throws GameServerException if the step is ended but not all pending
	 * methods have been processed
	 */
	private void endStep( ) throws GameServerException {
		// end the round
		try {
			game.endRound( execinfo.getPendingResults( ) );
		} catch( IllegalArgumentException ia ) {
			// still methods pending delay
			throw new GameServerException( getID(), Lang.get( "EndStepPending" ) );
		}
		
		// trace the end of the step
		tracer.roundEnded( game.getTime( ), execinfo.getPendingResults( ) );
		
		// create results object
		final GameResults res = new GameResults( getID( ), game.getTime( ), game.getJointPlan( ), execinfo.getPendingResults( ).getCompleted( ), execinfo.getPendingResults( ).getDelayed( ) );		

		// all is processed, forget about pending results
		execinfo.clearResults( );
		
		// build results object
		getEM( ).fireEvent( this, true, true, new ExecutedEvent( res ) );
	}
	
	/**
	 * Continues execution of the plan
	 * 
	 * @throws InvalidGameStateException if the game state is invalid
	 * @throws InvalidPlanException if the joint plan cannot be executed in its current state 
	 */
	private void continueExec( ) throws InvalidGameStateException, InvalidPlanException {		
		// delay before executing the next round
		try {
			Thread.sleep( execinfo.getSleep( ) );
		} catch( InterruptedException ie ) { }
		
		execute( );
	}
	
	/**
	 * Ends the execution process
	 */
	private void endExecution( ) {
		// log & trace
		log( LogType.Game, "Ended execution" );
		tracer.endedExecution( game.getTime( ) );
				
		// change game state
		setGameState( GameState.Idle );
	}
		
	/**
	 * Game is finished
	 */
	private void stateFinished( ) {
		// log and trace
		log( LogType.Game, "The game has been finished!" );
		tracer.gameEnded( );
		
		// update client state
		setClientStates( ClientState.Finished );
	}
}
