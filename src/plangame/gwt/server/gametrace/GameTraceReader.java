/**
 * @file GameTraceReader.java
 * @brief Short description of file
 *
 * This file is created at Almende B.V. It is open-source software and part of the Common
 * Hybrid Agent Platform (CHAP). A toolbox with a lot of open-source tools, ranging from
 * thread pools and TCP/IP components to control architectures and learning algorithms.
 * This software is published under the GNU Lesser General Public license (LGPL).
 *
 * Copyright © 2014 Joris Scharpff <joris@almende.com>
 *
 * @author       Joris Scharpff
 * @date         22 dec. 2014
 * @project      NGI
 * @company      Almende B.V.
 */
package plangame.gwt.server.gametrace;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import plangame.game.Game;
import plangame.game.plans.DelayStatus;
import plangame.game.plans.JointPlan;
import plangame.game.plans.PlanChange;
import plangame.game.plans.PlanChange.PlanChangeType;
import plangame.game.player.Player;
import plangame.gwt.client.gamedata.ClientGameData;
import plangame.gwt.client.gamedata.GameData;
import plangame.gwt.server.gametrace.GameTracer.TracerVersion;
import plangame.gwt.server.gametrace.TraceMsg.KEYS;
import plangame.gwt.server.gametrace.TraceMsg.TraceType;
import plangame.gwt.shared.enums.ClientState;
import plangame.gwt.shared.enums.GameState;
import plangame.gwt.shared.state.GameInfo;
import plangame.model.object.BasicID;
import plangame.model.tasks.Portfolio;
import plangame.model.tasks.TaskMethod;
import plangame.model.time.TimePoint;
import plangame.util.gameparser.GameParser;
import plangame.util.gameparser.xml.GameXMLParser;

/**
 * Reads game traces from a specified file, does not require connection to an
 * actual game server to reproduce results and therefore this is implemented in
 * a separate class.
 *
 * @author Joris Scharpff
 */
public class GameTraceReader {
	/** The trace file to read */
	protected File tracefile;
	
	/** The trace version number */
	protected TracerVersion version;
	
	/** Tomcat7 root for game files */
	protected String webroot;
	
	/** The lines read from the trace file */
	protected List<TraceMsg> tmsgs;
	
	/** The index of the last read trace line */
	protected int index;
	
	/** The current game we are tracing */
	protected Game game;
	
	/** Game data for computations */
	protected ClientGameData data;
	
	/**
	 * Creates a new empty game trace reader
	 */
	public GameTraceReader( ) {
		tracefile = null;
		tmsgs = new ArrayList<TraceMsg>( );
		index = -1;
		webroot = ".";
	}
	
	/**
	 * Creates a new game trace reader
	 * 
	 * @param tracefile The file to read the trace from
	 * @throws IOException if the file cannot be read
	 * @throws GameTraceException if the file contains an invalid trace msg
	 */
	public GameTraceReader( String tracefile ) throws IOException, GameTraceException {
		this( );
		
		readTraceFile( tracefile );
	}
	
	/**
	 * @return The trace file
	 */
	public File getTraceFile( ) {
		return tracefile;
	}
	
	/**
	 * @return The current game that is loaded
	 * @throws GameTraceException if no game is loaded
	 */
	public Game getGame( ) throws GameTraceException {
		if( game == null )
			throw new GameTraceException( "No game is running!" );
		return game;
	}
	
	/**
	 * @return The data of the current game
	 * @throws GameTraceException if the game has not started yet
	 */
	public GameData getGameData( ) throws GameTraceException {
		if( data == null || data.getData( ) == null )
			throw new GameTraceException( "Game has not yet started!" );
		return data.getData( );
	}
	
	/**
	 * @return The current joint plan
	 * @throws GameTraceException if the game has not started yet
	 */
	public JointPlan getJointPlan( ) throws GameTraceException {
		if( data == null || data.getData( ) == null )
			throw new GameTraceException( "Game has not yet started!" );
		return data.getJointPlan( );
	}
	
	/**
	 * Resets the game tracer
	 * @throws GameTraceException if no game trace is read
	 */
	public void reset( ) throws GameTraceException {
		if( tmsgs.size( ) == 0 )
			throw new GameTraceException( "No game trace has been read!" );
		
		// reset index and forget about the game
		index = -1;
		game = null;
		data = null;
	}

	/**
	 * Can be invoked to run the trace line program when a filename is specified 
	 * 
	 * @param args The command line arguments
	 */
	public static void main( String[] args ) {
		if( args.length < 1 )
			throw new RuntimeException( "Specify a trace file to read!" );
		
		// create tracer and run a full trace
		try {
			final GameTraceReader tr = new GameTraceReader( args[ 0 ] );
			if( args.length == 2 )
				tr.setWebRoot( args[ 1 ] );
			
			String res = null;
			int i = 0;
			while( (res = tr.step( )) != null )
				System.out.println( (i++) + ": " + res );
			
		} catch( Exception e ) {
			System.err.println( "Error while running trace:" );
			e.printStackTrace( );
		}
	}
	
	
	/**
	 * Reads the specified trace file entirely and stores the traces
	 * 
	 * @param tracefile The file containing the traces
	 * @throws IOException if the file does not exist or is invalid
	 * @throws GameTraceException if the trace is invalid
	 */
	public void readTraceFile( String tracefile ) throws IOException, GameTraceException {
		final File f = new File( tracefile );
		if( !f.exists( ) ) throw new IOException( "File '" + tracefile + "' does not exist" );
		if( !f.isFile( ) || !f.canRead( ) ) throw new IOException( "Invalid trace file '" + tracefile + "'" );
		
		// clear current trace
		tmsgs.clear( );
		
		// read all the lines
		BufferedReader br = new BufferedReader( new FileReader( f ) );
		String l;
		while( (l = br.readLine( )) != null ) {
			// read the trace message
			final TraceMsg t = TraceMsg.read( l );
			tmsgs.add( t );
		}
		
		br.close( );		
		this.tracefile = f;
	}
	
	/**
	 * Sets the root of the tomcat7 server for game model
	 * 
	 * @param root The root directory
	 */
	public void setWebRoot( String root ) {
		this.webroot = root;
	}
	
	/**
	 * Executes the next step of the trace
	 * 
	 * @return The result message or null if done
	 * @throws GameTraceException if the execute of the step failed
	 */
	public String step( ) throws GameTraceException {
		// go to next message if any
		index++;
		if( index >= tmsgs.size( ) )
			return null;
		
		// get the msg
		final TraceMsg t = tmsgs.get( index );
		return execute( t );
	}
	
	/**
	 * Steps and executes until the a message of the specified type is
	 * encountered
	 * 
	 * @param type The type of message
	 * @return The result of the message
	 * @throws GameTraceException if no game is loaded, an execute failed or no
	 * message of the type is present in the game
	 */
	public String stepTo( TraceType type ) throws GameTraceException {
		return stepTo( type, true );
	}
	
	/**
	 * Steps and executes until the a message of the specified type is
	 * encountered
	 * 
	 * @param type The type of message
	 * @param executing True if the execution phase should be included in the
	 * search
	 * @return The result of the message
	 * @throws GameTraceException if no game is loaded, an execute failed or no
	 * message of the type is present in the game
	 */
	public String stepTo( TraceType type, boolean executing ) throws GameTraceException {
		// now step until the trace message is start game
		TraceMsg t = null;
		String step = "";
		boolean isexecuting = false;
		do {
			index++;
			if( index >= tmsgs.size( ) )
				throw new GameTraceException( "No message of this type in trace!" );
			
			// execute the current message
			t = tmsgs.get( index );
			isexecuting |= (t.getType( ) == TraceType.StartedExecution);
			step = execute( t );
		} while( t.getType( ) != type && (executing || !isexecuting) && index < tmsgs.size( ) - 1 );
		
		return step;
	}
	
	/**
	 * @return The last executed trace message or "(none") if no message has been
	 * executed 
	 */
	public String getLastStep( ) {
		if( index < 0 )
			return "(none)";
		
		return tmsgs.get( index ).toString( );
	}
	
	/**
	 * Executed the specified trace message
	 * 
	 * @param tmsg The message to execute
	 * @return The result message
	 * @throws GameTraceException if the message contains invalid data
	 */
	protected String execute( TraceMsg tmsg ) throws GameTraceException {
		try {
			// check if a game exists (when needed)
			if( tmsg.getType( ) != TraceType.TraceInfo && tmsg.getType( ) != TraceType.GameLoaded )
				if( game == null ) throw new GameTraceException( "No game has been loaded!" );				
			
			switch( tmsg.getType( ) ) {				
				// forced the client into a specific state
				case ClientStateForced: {
					final Player p = getPlayer( tmsg );
					final ClientState prev = ClientState.valueOf( tmsg.getValue( KEYS.StateOld ) );
					final ClientState curr = ClientState.valueOf( tmsg.getValue( KEYS.StateNew ) );
					return "Forced state of " + p + " from " + prev + " to " + curr;
				}
				
				case EndedExecution:
					return "Execution was paused";
				
				// the game has ended
				case GameEnded: {
					return "The game has ended";
				}
				
				// create a new game
				case GameLoaded: {
					final String gamefile = tmsg.getValue( KEYS.GameFile );
					//final int players = Integer.parseInt( tmsg.getValue( KEYS.MaxPlayers ) );
					final GameParser gp = new GameXMLParser( new File( webroot, gamefile ).getAbsolutePath( ) );
					game = gp.read( BasicID.makeValidID( "TraceGame" ) );
					return "Created new game";
				}
				
				// forced the game state into a specific state
				case GameStateForced: {
					final GameState prev = GameState.valueOf( tmsg.getValue( KEYS.StateOld ) );
					final GameState curr = GameState.valueOf( tmsg.getValue( KEYS.StateNew ) );
					return "Game state was forced from " + prev + " to " + curr;
				}
				
				// starts the game
				case GameStarted: {
					data = new ClientGameData( );
					data.setGameInfo( new GameInfo( game.getID( ), game.getPlayers( ), game.getPortfolios( ), game.getPeriod( ), game.getInfra( ), game.getMechanism( ) ) );					
					data.setJointPlan( game.getJointPlan( ) );
					return "Game has started";
				}
				
				// plan has changed
				case PlanChanged: {
					final Player p = getPlayer( tmsg );
					final PlanChange pc = getPlanChange( p, tmsg.getValue( KEYS.PlanChange ) );
					
					//System.out.println( pc );
					//game.getJointPlan( ).applyChange( pc, false );
					data.changeJointPlan( pc, false );
					return "Plan changed: " + pc;
				}
				
				// player has submitted its plan
				case PlanSubmitted: {
					final Player p = getPlayer( tmsg );
					return "Plan submitted by " + p;
				}
				
				// adds a player to the game
				case PlayerAdded: {
					final String ID = tmsg.getValue( KEYS.PlayerID );
					final Player p = new Player( BasicID.makeValidID( ID ), ID );
					game.addPlayer( p );
					return "Added player " + p;
				}
				
				// player removed from the game
				case PlayerRemoved: {
					final Player p = getPlayer( tmsg );
					return "Player " + p + " was dropped from the game";
				}				
				
				// player responded to a joint plan
				case PlayerResponded: {
					final Player p = getPlayer( tmsg );
					final boolean accept = Boolean.parseBoolean( tmsg.getValue( KEYS.AcceptResponse ) );
					return p + " " + (accept ? "accepted" : "rejected") + " the joint plan"; 
				}

				// sets the portfolio for a player
				case PortfolioAssigned: {
					final Player p = getPlayer( tmsg );
					final Portfolio pf = getPortfolioID( tmsg );
					game.setPortfolioOwner( p, pf );
					return "Assigned portfolio '" + pf + "' to " + p;
				}

				// player requested a plan suggestion
				case RequestedSuggestion: {
					final Player p = getPlayer( tmsg );
					return "Player " + p + " requested a plan suggestion";
				}
				
				// a response of all agents is in to the joint plan
				case RespondedAll: {
					final boolean accept = Boolean.parseBoolean( tmsg.getValue( KEYS.AcceptResponse ) );
					return "The players have " + (accept ? "accepted" : "rejected" ) + " the joint plan";
				}
				
				// one execution step has ended, apply delays if any
				case RoundEnded: {
					// get the time
					final TimePoint t = getTime( tmsg );
					
					// first set status of completed methods
					final String compl = tmsg.getValue( KEYS.TasksCompleted );
					if( compl.trim( ).length( ) > 0 ) {
						final String[] complmeth = compl.split( ";" );
						for( String m : complmeth ) {
							final TaskMethod tm = getMethod( m );
							
							// get previous status and set it to not-delayed if it was pending
							final DelayStatus d = data.getJointPlan( ).getPlanned( tm.getTask( ) ).getDelayStatus( );
							if( d == DelayStatus.Pending )
								data.changeJointPlan( PlanChange.status( tm, DelayStatus.AsPlanned ), false );
						}
					}
						
					// now set delay status of delayed methods
					final String del = tmsg.getValue( KEYS.TasksDelayed );
					if( del.trim( ).length( ) > 0 ) {
						final String[] delmeth = del.split( ";" );
						for( String m : delmeth ) {
							final TaskMethod tm = getMethod( m );
							data.changeJointPlan( PlanChange.status( tm, DelayStatus.Delayed ), false );
						}
					}
					
					return "Ended execution of round " + t;
				}
				
				// one round is executed
				case RoundExecuted: {
					final TimePoint t = getTime( tmsg );
					return "Round " + t + " has been executed";
				}
				
				// started the plan execution
				case StartedExecution: {
					return "Started execution";
				}
				
				// started a plan round
				case StartedPlanning: {
					return "Started plan round";
				}
				
				// the execution of the joint plan was stopped
				case StoppedExecution:
					return "Execution paused";
				
				// all players submitted their plans
				case SubmittedAll: {
					return "All players submitted their plan";
				}
				
				// trace information
				case TraceInfo: {
					version = TracerVersion.valueOf( tmsg.getValue( KEYS.VersionNumber ) );
					return "Trace started, version " + version;
				}
					
				// unknown action
				default:
					throw new GameTraceException( "Unknown trace message type: " + tmsg.getType( ) );
			}
		} catch( ClassCastException cce ) {
			throw new GameTraceException( "Invalid data type in value field: " + cce );
		} catch( Exception e ) {
			e.printStackTrace( );
			throw new GameTraceException( "Exception during execution of " + tmsg );
		}
	}
	
	/**
	 * @param tmsg The trace message
	 * @return The time in the message
	 */
	protected TimePoint getTime( TraceMsg tmsg ) {
		return new TimePoint( Integer.parseInt( tmsg.getValue( KEYS.GameTime ) ) );
	}
	
	/**
	 * @param tmsg The trace message
	 * @return The player based on the player ID field
	 */
	protected Player getPlayer( TraceMsg tmsg ) {
		return game.getPlayerByID( BasicID.makeValidID( tmsg.getValue( KEYS.PlayerID ) ) );
	}
	
	/**
	 * @param tmsg The trace message
	 * @return The portfolio based on the portfolio ID field
	 */
	protected Portfolio getPortfolioID( TraceMsg tmsg ) {
		return game.getPortfolioByID( BasicID.makeValidID( tmsg.getValue( KEYS.PortfolioID ) ) );
	}
	
	/**
	 * Deserialises a plan change trace message
	 * 
	 * @param player The player
	 * @param pc The plan change as string
	 * @return The plan change
	 */
	@SuppressWarnings("null") // object is only used when not null
	protected PlanChange getPlanChange( Player player, String pc ) {
		// get basic change description
		final String[] p = pc.split( ";" );
		final PlanChangeType type = PlanChangeType.valueOf( p[0] );
		final TaskMethod meth = getMethod( player.getPortfolio( ), p[1] );
		
		// get time and possibly previous method
		TimePoint time;
		try {
		 time = new TimePoint( Integer.parseInt( p[2] ) );
		} catch( IllegalArgumentException iae ) { time = null; }
		TaskMethod prev = null;
		if( p.length == 4 ) {
			prev = getMethod( player.getPortfolio( ), p[3] );
		}
		
		// return the correct change operation based on the type
		switch( type ) {
			case MethodAdded: return PlanChange.add( meth, time );
			case MethodChanged: return PlanChange.change( data.getJointPlan( ).getPlanned( prev.getTask( ) ), meth, time );
			case MethodDelaySet: throw new RuntimeException( "Should not be set in trace!" );
			case MethodMoved: return PlanChange.move( data.getJointPlan( ).getPlanned( prev.getTask( ) ), time );
			case MethodRemoved: return PlanChange.remove( data.getJointPlan( ).getPlanned( prev.getTask( ) ) );
			
			default:
				throw new RuntimeException( "Unknown planchange type!" );
		}
	}
	
	/**
	 * Finds a method from a full ID
	 * 
	 * @param pf The portfolio that contains the method
	 * @param mID The full method ID
	 * @return The method
	 */
	protected TaskMethod getMethod( Portfolio pf, String mID ) {
		final String[] IDs = mID.split( "\\." );
		return pf.getTaskMethodByID( IDs[0], IDs[1] );		
	}
	
	/**
	 * Finds a method from a full ID
	 * 
	 * @param mID The full method ID
	 * @return The method
	 */
	protected TaskMethod getMethod( String mID ) {
		for( Portfolio pf : game.getPortfolios( ) ) {
			final TaskMethod tm = getMethod( pf, mID );
			if( tm != null )
				return tm;
		}
		
		return null;
	}

	
	/**
	 * Prints the entire trace to sysout
	 */
	public void printAll( ) {
		for( TraceMsg t : tmsgs ) {
			System.out.println( t );
		}
	}
}
