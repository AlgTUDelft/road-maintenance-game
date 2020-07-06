/**
 * @file TraceMsg.java
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

import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 * @author Joris Scharpff
 */
public class TraceMsg {
	/** The time stamp associated with the message */
	private Date stamp;
	
	/** The type of trace message */
	private TraceType type;
	
	/** Keys stored/read in this message */
	private List<TraceKey> keys;
	
	/**
	 * Creates a new trace message
	 * 
	 * @param type The trace message type 
	 */
	protected TraceMsg( TraceType type ) {
		this.type = type;
		
		// set time of creation
		stamp = new Date( );
		
		// creates the keys list
		keys = new ArrayList<TraceKey>( );
	}
	
	/** @return The trace message type */
	protected TraceType getType( ) { return type; }
	
	/** @return The trace time stamp */
	protected Date getStamp( ) { return stamp; }
	
	/**
	 * Adds a new key to the trace message, the value can not contain the pipe
	 * ('|') character
	 * 
	 * @param key The key
	 * @param value The key value
	 */
	protected void addKey( KEYS key, String value ) throws RuntimeException {
		assert value.indexOf( '|' ) == -1 : "Invalid value in trace message"; 
		
		keys.add( new TraceKey( key, value ) );			
	}
	
	/**
	 * Retrieves the value stored for the key
	 * 
	 * @param key The key to get the value of 
	 */
	protected String getValue( KEYS key ) {
		for( TraceKey k : keys )
			if( k.key.equals( key ) ) return k.value;
		
		assert false : "Unknown key specified!";
		return null;
	}
	
	/**
	 * Converts the message to a writable string
	 * 
	 * @return String that serialises the message
	 */
	protected String write( ) {
		String msg = "[" + DateFormat.getDateTimeInstance( DateFormat.SHORT, DateFormat.MEDIUM ).format( stamp ) + "] ";
		msg += type.toString( );
		
		for( TraceKey key : keys ) {
			msg += "|" + key.key.toString( ) + "|" + key.value;
		}
		
		return msg;
	}
	
	/**
	 * Reads a message from a serialised trace string
	 * 
	 * @param msg The message string
	 * @return The trace message
	 * @throws GameTraceException if the trace read is invalid
	 */
	protected static TraceMsg read( String msg ) throws GameTraceException {
		// read the time stamp
		String tstamp = msg.substring( 1, msg.indexOf( "]" ) );
		
		// parse remaining part
		// add extra space if key ends with "|"
		String val = msg.substring( msg.indexOf( "]" ) + 2 );
		if( val.endsWith( "|" ) ) val += " ";
		final String[] params = val.split( "\\|" );
		final TraceMsg m = new TraceMsg( TraceType.valueOf( params[0] ) );
		
		// get key/value pairs
		for( int i = 1; i < params.length; i += 2 ) {
			m.addKey( KEYS.valueOf( params[i] ), params[i + 1] );
		}
		
		try {
			m.stamp = DateFormat.getDateTimeInstance( DateFormat.SHORT, DateFormat.MEDIUM ).parse( tstamp );
		} catch( ParseException pe ) {
			// should not be thrown due to read/write procedure 
			throw new GameTraceException( "Mismatch between read and write function of the tracer" );
		}
		
		return m;
	}
	
	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString( ) {
		return write( );
	}
	
	/**
	 * Trace parameter key/value pair
	 */
	private static class TraceKey {
		/** The key */
		protected KEYS key;
		
		/** The key value (as string) */
		protected String value;
		
		/**
		 * Creates a new trace key
		 * 
		 * @param key The key
		 * @param value The value
		 */
		protected TraceKey( KEYS key, String value ) {
			this.key = key;
			this.value = value;
		}
	}		

	/** The types of trace messages */
	@SuppressWarnings("javadoc")
	public enum TraceType {
		TraceInfo,
		
		GameStateForced,
		ClientStateForced,
		
		GameLoaded,
		GameStarted,
		GameEnded,
		PlayerAdded,
		PlayerRemoved,
		PortfolioAssigned,
		
		StartedPlanning,
		StartedExecution,
		RoundExecuted,
		RoundEnded,
		EndedExecution,
		StoppedExecution,
		PlanChanged,
		PlanSubmitted,
		SubmittedAll,
		PlayerResponded,
		RespondedAll,
		
		RequestedSuggestion,
	}
	
	/** Message keys to prevent typo errors in read/write */
	@SuppressWarnings("javadoc")
	public enum KEYS {
		AcceptResponse,
		ExecutionMode,
		GameFile,
		GamePeriod,
		GameTime,
		MaxPlayers,
		NumPlayers,
		PlanChange,
		PlanPreference,
		PlayerID,
		PortfolioID,
		StateOld,
		StateNew,
		TaskID,
		TasksCompleted,
		TasksDelayed,
		TasksPending,
		TasksStarted,		
		VersionNumber,
	}
}
