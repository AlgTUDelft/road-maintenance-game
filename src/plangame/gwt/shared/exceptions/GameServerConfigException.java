/**
 * @file GameServerConfigExcption.java
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
package plangame.gwt.shared.exceptions;

/**
 * Exception that is thrown whenever the game server config is invalid
 *
 * @author Joris Scharpff
 */
@SuppressWarnings("serial")
public class GameServerConfigException extends Exception {
	/** Empty constructor for GWT RPC */
	@Deprecated protected GameServerConfigException( ) { }
	
	/** Config error types, this is so that they can be localised at the client */
	public enum GameServerConfigError {
		/** Invalid maximal number of players */
		InvalidMaxPlayers;
	}
	
	/** The config error that occurred */
	protected GameServerConfigError error;

	/** The string representation of the value that was tried to set */
	protected String invvalue;
	
	/**
	 * Creates a new {@link GameServerConfigException} with the specified error
	 * 
	 * @param error The config error
	 * @param invvalue The invalid value that was set
	 */
	public GameServerConfigException( GameServerConfigError error, String invvalue ) {
		super( );
		
		this.error = error;
		this.invvalue = invvalue;
	}
	
	/** @return The error */
	public GameServerConfigError getError( ) { return error; }
	
	/** @return The invalid value that was tried to set */
	public String getInvalidValue( ) { return invvalue; }
}
