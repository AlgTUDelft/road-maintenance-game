/**
 * @file Globals.java
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
 * @date         22 jan. 2013
 * @project      NGI
 * @company      Almende B.V.
 */
package plangame.gwt.shared;

/**
 * Global debug compilation options allowing for debug settings in the game
 *
 * @author Joris Scharpff
 */
public class DebugGlobals {
	/** @return Whether we are running debug mode. Setting the debug mode to false
	 * overrides all other compilation globals */
	public static boolean isDebug( ) { return false; }
	
	/** @return The filename of the game that is automatically loaded. Set to
	 * null to ignore this */
	public static String testGameFile( ) { return (isDebug( ) ? "games/roersteden/game.xml" : null); }
	
	/** @return The name of the server that is automatically created for the
	 * testGameFile( ) */
	public static String testGameServer( ) { return (isDebug( ) & autoConnect( ) ? "Testgame" : null); }
	
	/** @return Automatically connects clients to the test game */
	public static boolean autoConnect( ) { return isDebug( ) & (testGameFile( ) != null) & true; }
		
	/** @return Automatically assigns the first available portfolio to the client */
	public static boolean autoAssign( ) { return isDebug( ) & true; }
	
	/** @return Automatically develop an initial planning (randomly) */
	public static boolean autoInitialPlan( ) { return isDebug( ) & true; }
	
	/** @return Enable notification messages in console */
	public static boolean showNotifications( ) { return isDebug( ) & false; }
	
	/** @return Disable notification dialogs */
	public static boolean hideDialogs( ) { return isDebug( ) & true; }
	
	/** @return Enable speed profiling of user interface */
	public static boolean uiProfiling( ) { return isDebug( ) & true; }
	
	/** @return Print stack trace of warnings and errors */
	public static boolean stackTrace( ) { return isDebug( ) & true; }
	
	/** @return Show RPC exceptions, even if they are dealt with */
	public static boolean showRPCExceptions( ) { return isDebug( ) & false; }
	
	/** @return The log flags to print to console, 0 if not in debug */
	public static int printLog( ) { return LogType.All.toBits( ); }
	
	/** @return Clear the log directory on startup? */
	public static boolean clearLogs( ) { return isDebug( ) && true; }
	
	/** @return Clear the trace directory before starting? */
	public static boolean clearTraces( ) { return isDebug( ) && true; }
	
	/** @return Output cache performance */
	public static boolean cachePerformance( ) { return isDebug( ) && false; }
}
