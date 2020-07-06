/**
 * @file LogLevel.java
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
 * @date         28 nov. 2012
 * @project      NGI
 * @company      Almende B.V.
 */
package plangame.gwt.shared;

/**
 * Logging type
 *
 * @author Joris Scharpff
 */
@SuppressWarnings("javadoc")
public enum LogType {
	Game(1),
	Result(2),
	
	Event(4),
	Info(8),
	Warning(16),
	Error(32),
	Verbose(64),
	
	Debug(128),
	
	// special enum values
	Gameplay( Game.value | Result.value ),
	System( Event.value | Info.value | Warning.value | Error.value | Verbose.value ),
	All( Debug.value * 2 - 1 ); // should be last one * 2 - 1 to set all bit flags
	
	/** Log level bit value */
	private int value;
	
	/**
	 * Creates a log type enum with specified bit value
	 * 
	 * @param value The bit flag value
	 */
	private LogType( int value ) {
		this.value = value;
	}
	
	/**
	 * @return Bit flag for the given type
	 */
	public int toBits( ) {
		return value;
	}
	
	/**
	 * Check if specified bit flag contains this enum value
	 * 
	 * @param bitflag The bit flag of log type
	 * @return True if the bit flag is true for this given log type
	 */
	public boolean isset( int bitflag ) {
		return (bitflag & value) == value;
	}
}