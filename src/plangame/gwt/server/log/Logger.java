/**
 * @file Logger.java
 * @brief Short description of file
 *
 * This file is created at Almende B.V. It is open-source software and part of the Common
 * Hybrid Agent Platform (CHAP). A toolbox with a lot of open-source tools, ranging from
 * thread pools and TCP/IP components to control architectures and learning algorithms.
 * This software is published under the GNU Lesser General Public license (LGPL).
 *
 * Copyright © 2012 Joris Scharpff <joris@almende.com>
 *
 * @author       Joris Scharpff
 * @date         27 nov. 2012
 * @project      NGI
 * @company      Almende B.V.
 */
package plangame.gwt.server.log;

import java.io.FileNotFoundException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import plangame.gwt.shared.DebugGlobals;
import plangame.gwt.shared.LogType;

/**
 * Logger for the game
 *
 * @author Joris Scharpff
 */
public class Logger {
	/** The log writers */
	protected List<LogWriter> writers;
		
	/**
	 * Creates a new logger
	 */
	public Logger( ) {
		writers = new ArrayList<LogWriter>( );
		
		// DEBUG add sysout log writer
		if( DebugGlobals.printLog( ) > 0 )
			writers.add( new LogWriter( System.out, DebugGlobals.printLog( ) ) );
	}
	
	/**
	 * Creates a new default logger that logs all levels into one file
	 * 
	 * @param logfile The file to log to
	 */
	public Logger( String logfile ) {
		this( );
		
		addLog( logfile, LogType.All.toBits( ) );
	}
	
	/**
	 * Adds new log file with specified log levels bit flag
	 * 
	 * @param logfile The log filename
	 * @param logtypes The types bit flag
	 */
	public void addLog( String logfile, int logtypes ) {
		// no log found with this file name, create a new one
		try {
			writers.add( new LogWriter( logfile, logtypes ) );
		} catch( RuntimeException re ) {
			// failed to create logger
			System.err.println( re.getMessage( ) );
		}
	}
	
	/**
	 * Logs a message with the specified log level, checks if the level is in the
	 * flag of included log levels
	 * 
	 * @param sender The name of the sender
	 * @param message The message to log
	 * @param type The message type
	 */
	public void log( String sender, String message, LogType type ) {
		for( LogWriter w : writers ) 
			w.log( sender, message, type );
	}

	/**
	 * Private class used to represent one log writer
	 */
	private class LogWriter {
		/** The output stream */
		protected PrintWriter out;
		
		/** The logging types bit flag */
		protected int logtypes;
		
		/**
		 * Creates a new log writer that writes to the specified file name and uses
		 * the given logging level
		 * 
		 * @param logfile The log file
		 * @param logtypes The log types flag value
		 * @throws RuntimeException if the logger failed to initialise
		 */
		public LogWriter( String logfile, int logtypes ) throws RuntimeException {
			this.logtypes = logtypes; 
			
			try {
				out = new PrintWriter( logfile );
				writeHeader( );
			} catch( FileNotFoundException e ) {
				throw new RuntimeException( "Logger failed to initialise: " + e.getMessage( ) );
			} 
		}
		
		/**
		 * Creates a log writer that writes to a print stream
		 * 
		 * @param stream The print stream to write to
		 * @param logtypes The logging types bit flag
		 */
		public LogWriter( OutputStream stream, int logtypes ) {
			this.logtypes = logtypes;
			out = new PrintWriter( stream );
			writeHeader( );
		}

		/**
		 * Destroys the log writer, closes the log file
		 */
		@Override
		protected void finalize( ) throws Throwable {		
			out.close( );
			
			super.finalize( );
		}		
		
		/**
		 * Writes the log header
		 */
		private void writeHeader( ) {
			out.println( "Log started " + new Date( ).toString( ) );
			out.print( "Types of log messages:" );
			for( LogType type : LogType.values( ) )
				if( type.isset( logtypes ) )
					out.print( " " + type.toString( ) );
			out.println( );
		}
		
		/**
		 * Logs a message with the specified type
		 * 
		 * @param sender The name of the sender
		 * @param message The message
		 * @param type The log type
		 */
		public void log( String sender, String message, LogType type ) {
			// check if this level should be logged
			if( !type.isset( logtypes ) ) return;		

			final String tstamp = (new SimpleDateFormat( "HH:mm:ss" )).format( new Date( ) );
			
			out.println( "[" + tstamp + " " + sender + " (" + type.toString( ) + ")] " + message );
			out.flush( );
		}
	}
}
