/**
 * @file GameTracer.java
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
 * @date         29 mei 2013
 * @project      NGI
 * @company      Almende B.V.
 */
package plangame.gwt.server.gametrace;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.List;

import plangame.game.plans.PlanChange;
import plangame.game.plans.PlanStepResult;
import plangame.game.plans.PlanTask;
import plangame.game.player.PlanPreference;
import plangame.game.player.Player;
import plangame.gwt.server.GameServer;
import plangame.gwt.server.Server;
import plangame.gwt.server.gametrace.TraceMsg.KEYS;
import plangame.gwt.server.gametrace.TraceMsg.TraceType;
import plangame.gwt.shared.LogType;
import plangame.gwt.shared.clients.SPClient;
import plangame.gwt.shared.enums.ClientState;
import plangame.gwt.shared.enums.ExecutionMode;
import plangame.gwt.shared.enums.GameState;
import plangame.model.object.BasicID;
import plangame.model.tasks.Task;
import plangame.model.time.TimePoint;

/**
 * Class that logs all game actions/events such that the gameplay can easiliy
 * be reconstructed afterwards
 *
 * @author Joris Scharpff
 */
public class GameTracer {
	/** The game server we are tracing */
	protected GameServer gameserver;
	
	/** The trace file writer */
	protected PrintWriter writer;
	
	/** Trace file folder */
	public final static String TRACE_DIR = "traces/";
	
	/** Tracer version numbers */
	protected enum TracerVersion {
		V1;
	}
	
	/** Current tracer version number */
	protected final static TracerVersion currversion = TracerVersion.V1;	
		
	/**
	 * Creates a new game tracer
	 * 
	 * @param gameserver The game server we are tracing 
	 * @throws IOException if the trace folder or file cannot be created 
	 */
	public GameTracer( GameServer gameserver ) throws IOException {
		this( gameserver, true );
	}
	
	/**
	 * Creates a mew tracer
	 * 
	 * @param gameserver The game server we are tracing
	 * @param trace Use the tracer, can be set to false to use this as a dummy
	 * @throws IOException if the trace folder or file cannot be created 
	 */
	public GameTracer( GameServer gameserver, boolean trace ) throws IOException {
		this.gameserver = gameserver;
		
		// dummy?
		if( !trace ) return;
		
		// check if the trace dir exists
		Server.ensureDir( TRACE_DIR );
		
		// create a new trace file writer
		final File tfile = Server.createFile( TRACE_DIR + gameserver.getID( ) + "_" + Server.getTimeStamp( new Date( ) ) + ".log", false );
		writer = new PrintWriter( tfile );
		
		// add trace 
		traceInfo( );
		
		Server.getInstance( ).logGameServerMessage( gameserver.getID( ), "Created trace file '" + tfile + "'", LogType.Game );
	}
	
	/**
	 * Adds version number information
	 */
	protected void traceInfo( ) {
		final TraceMsg tmsg = new TraceMsg( TraceType.TraceInfo );
		tmsg.addKey( KEYS.VersionNumber, currversion.toString( ) );
		
		trace( tmsg );
	}
	
	/**
	 * GM forced the game state to change
	 * 
	 * @param oldstate The previous game state
	 * @param newstate The new game state (enforced by the GM)
	 */
	public void forcedGameState( GameState oldstate, GameState newstate ) {
		final TraceMsg tmsg = new TraceMsg( TraceType.GameStateForced );
		tmsg.addKey( KEYS.StateOld, (oldstate != null ? oldstate.toString( ) : "null") );
		tmsg.addKey( KEYS.StateNew, newstate.toString( ) );
		
		trace( tmsg );
	}
	
	/**
	 * GM forced the client state to change
	 * 
	 * @param client The client
	 * @param oldstate The previous client state
	 * @param newstate The new client state (enforced by the GM)
	 */
	public void forcedClientState( SPClient client, ClientState oldstate, ClientState newstate ) {
		final TraceMsg tmsg = new TraceMsg( TraceType.ClientStateForced );
		addPlayer( tmsg, client.getPlayer( ) );
		tmsg.addKey( KEYS.StateOld, (oldstate != null ? oldstate.toString( ) : "null") );
		tmsg.addKey( KEYS.StateNew, newstate.toString( ) );
		
		trace( tmsg );
	}


	/**
	 * Logs the loading of a game
	 * 
	 * @param gamefile The game file
	 * @param maxclients The number of clients that can connect to the game
	 */
	public void gameLoaded( String gamefile, int maxclients  ) {
		final TraceMsg tmsg = new TraceMsg( TraceType.GameLoaded );
		tmsg.addKey( KEYS.GameFile, gamefile );
		tmsg.addKey( KEYS.MaxPlayers, "" + maxclients );
		
		trace( tmsg );
	}
	
	/**
	 * Logs the start of the game
	 * 
	 * @param players The number of players present at the start
	 * @param period The game period in weeks
	 */
	public void gameStarted( int players, int period ) {
		final TraceMsg msg = new TraceMsg( TraceType.GameStarted );
		msg.addKey( KEYS.NumPlayers, "" + players );
		msg.addKey( KEYS.GamePeriod, "" + period );
		
		trace( msg );
	}
	
	/**
	 * Logs the game end
	 */
	public void gameEnded( ) {
		trace( new TraceMsg( TraceType.GameEnded ) );
	}
	
	/**
	 * Player is added to the game
	 * 
	 * @param player The player that was added
	 */
	public void playerAdded( Player player ) {
		final TraceMsg msg = new TraceMsg( TraceType.PlayerAdded );
		addPlayer( msg, player );
		
		trace( msg );
	}
	
	/**
	 * Player is removed from game (disconnect)
	 * 
	 * @param player The player that was removed
	 */
	public void playerRemoved( Player player ) {
		final TraceMsg msg = new TraceMsg( TraceType.PlayerRemoved );
		addPlayer( msg, player );
		
		trace( msg );
	}
	
	/**
	 * Portfolio is assigned to a player
	 * 
	 * @param player The player that was assigned a portfolio
	 * @param pfID The ID of the portfolio that was assigned
	 */
	public void portfolioAssigned( Player player, BasicID pfID ) {
		final TraceMsg msg = new TraceMsg( TraceType.PortfolioAssigned );
		addPlayer( msg, player );
		msg.addKey( KEYS.PortfolioID, pfID.toString( ) );
		
		trace( msg );		
	}
	
	/**
	 * Execution has started
	 * 
	 * @param round The round (week) number
	 * @param mode The mode of execution
	 */
	public void startedExecution( TimePoint round, ExecutionMode mode ) {
		final TraceMsg msg = new TraceMsg( TraceType.StartedExecution );
		addRound( msg, round );
		msg.addKey( KEYS.ExecutionMode, mode.toString( ) );
		
		trace( msg );
	}
	
	/**
	 * Round has been executed
	 * 
	 * @param round The round (week) number
	 * @param results The execution results 
	 */
	public void roundExecuted( TimePoint round, PlanStepResult results ) {
		final TraceMsg msg = new TraceMsg( TraceType.RoundExecuted );
		addRound( msg, round );
		msg.addKey( KEYS.TasksStarted, getTaskString( results.getStarted( ) ) );
		msg.addKey( KEYS.TasksPending, getTaskString( results.getPending( ) ) );
		
		trace( msg );
	}
	
	/**
	 * Round has been processed and ended
	 * 
	 * @param round The round
	 * @param results The processed execution results
	 */
	public void roundEnded( TimePoint round, PlanStepResult results ) {
		final TraceMsg msg = new TraceMsg( TraceType.RoundEnded );
		addRound( msg, round );
		msg.addKey( KEYS.TasksCompleted, getTaskString( results.getCompleted( ) ) );
		msg.addKey( KEYS.TasksDelayed, getTaskString( results.getDelayed( ) ) );
		
		trace( msg );
	}
	
	/**
	 * Execution is completed
	 * 
	 * @param round The last executed round (week) number
	 */
	public void endedExecution( TimePoint round ) {
		final TraceMsg msg = new TraceMsg( TraceType.EndedExecution );
		addRound( msg, round );
		
		trace( msg );
	}
	
	/**
	 * The GM interrupted the plan execution
	 * 
	 * @param round The round in which the stop was issued
	 */
	public void stoppedExecution( TimePoint round ) {
		final TraceMsg msg = new TraceMsg( TraceType.StoppedExecution );
		addRound( msg, round );
		
		trace( msg );
	}

	/**
	 * Planning has been started in the round
	 * 
	 * @param round The round (week) number
	 */
	public void startedPlanning( TimePoint round ) {
		final TraceMsg msg = new TraceMsg( TraceType.StartedPlanning );
		addRound( msg, round );
		
		trace( msg );
	}
	
	/**
	 * The player has changed his/her plan
	 * 
	 * @param round The round (week) number
	 * @param player The player that changed its plan
	 * @param change The change
	 */
	public void planChanged( TimePoint round, Player player, PlanChange change ) {
		final TraceMsg msg = new TraceMsg( TraceType.PlanChanged );
		addRound( msg, round );
		addPlayer( msg, player );
		msg.addKey( KEYS.PlanChange, getPlanChangeString( change ) );
		
		trace( msg );
	}
	
	/**
	 * Plan submitted by the player
	 * 
	 * @param round The round (week) number
	 * @param player The player that submitted the plan
	 */
	public void planSubmitted( TimePoint round, Player player ) {
		final TraceMsg msg = new TraceMsg( TraceType.PlanSubmitted );
		addRound( msg, round );
		addPlayer( msg, player );
		
		trace( msg );
	}
	
	/**
	 * All players have submitted their plan
	 * 
	 * @param round The round (week) number
	 */
	public void submittedAll( TimePoint round ) {
		final TraceMsg msg = new TraceMsg( TraceType.SubmittedAll );
		addRound( msg, round );
		
		trace( msg );
	}
	
	/**
	 * The player responded to the joint plan
	 * 
	 * @param round The round (week) number
	 * @param player The player that responded
	 * @param response Its response
	 */
	public void playerResponded( TimePoint round, Player player, boolean response ) {
		final TraceMsg msg = new TraceMsg( TraceType.PlayerResponded );
		addRound( msg, round );
		addPlayer( msg, player );
		msg.addKey( KEYS.AcceptResponse, "" + response );
		
		trace( msg );
	}
	
	/**
	 * All players have responded
	 * 
	 * @param round The round (week) number
	 * @param response The aggregated response
	 */
	public void respondedAll( TimePoint round, boolean response ) {
		final TraceMsg msg = new TraceMsg( TraceType.RespondedAll );
		addRound( msg, round );
		msg.addKey( KEYS.AcceptResponse, "" + response );
		
		trace( msg );
	}
	
	/**
	 * The player has requested a plan suggestion
	 * 
	 * @param round The round (week) number
	 * @param player The player
	 * @param task The task to request for
	 * @param prefs The player plan preferences
	 */
	public void requestedSuggestion( TimePoint round, Player player, Task task, PlanPreference prefs ) {
		final TraceMsg msg = new TraceMsg( TraceType.RequestedSuggestion );
		addRound( msg, round );
		addPlayer( msg, player );
		msg.addKey( KEYS.TaskID, (task != null ? task.getID( ).toString( ) : "null" ) );
		msg.addKey( KEYS.PlanPreference, getPlanPrefString( prefs ) );
		
		trace( msg );
	}
	
	/**
	 * Writes the trace message into the trace file
	 * 
	 * @param message The message to write
	 */
	private void trace( TraceMsg message ) {
		// check if the tracer is not a dummy
		if( writer == null ) return;
		
		// output the message
		writer.println( message.write( ) );
		writer.flush( );
	}
	
	/**
	 * Adds the round number key to the trace msg
	 * 
	 * @param msg The trace message
	 * @param round The round number
	 */
	private void addRound( TraceMsg msg, TimePoint round ) {
		msg.addKey( KEYS.GameTime, "" + round.getWeek( ) );
	}
	
	/**
	 * Adds the player ID to the message
	 * 
	 * @param msg The message
	 * @param player The player 
	 */
	private void addPlayer( TraceMsg msg, Player player ) {
		msg.addKey( KEYS.PlayerID, player.getID( ).toString( ) );
	}
	
	/**
	 * Concatenates all methods in the list into a ';' separated string
	 * 
	 * @param list The method list
	 * @return The string
	 */
	private String getTaskString( List<PlanTask> list ) {
		String methods = "";
		for( PlanTask pt : list )
			methods += pt.getMethod( ).getID( ).toString( ) + ";";
		if( methods.length( ) > 0 ) methods.substring( 0, methods.length( ) - 2 );
		
		return methods;
	}
	
	/**
	 * Serialises a plan change
	 * 
	 * @param change The plan change
	 * @return The string that describes the plan change
	 */
	// FIXME improve
	private String getPlanChangeString( PlanChange change ) {
		String c = change.getType( ).toString( ) + ";";
		c += change.getMethod( ).getID( ) + ";";
		c += (change.getTime( ) != null ? change.getTime( ).getWeek( ) : "-1") + ";";
		c += (change.getPrevious( ) != null ? change.getPrevious( ).getMethod( ).getID( ) : "" );
		return c;
	}
	
	/**
	 * Serialises the player plan preferences
	 * 
	 * @param prefs The plan preferences
	 * @return The string that describes the preferences
	 */
	private String getPlanPrefString( PlanPreference prefs ) {
		String p = "";
		p += prefs.getCostWeight( ) + ";";
		p += prefs.getQualityWeight( ) + ";";
		p += prefs.getTTLWeight( ) + ";";
		return p;
	}
}
