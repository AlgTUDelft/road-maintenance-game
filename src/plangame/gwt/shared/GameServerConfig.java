/**
 * @file GameServerConfig.java
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
 * @date         22 aug. 2013
 * @project      NGI
 * @company      Almende B.V.
 */
package plangame.gwt.shared;

import java.io.Serializable;

import plangame.gwt.shared.exceptions.GameServerConfigException;
import plangame.gwt.shared.exceptions.GameServerConfigException.GameServerConfigError;

/**
 * Configuration of the game server
 *
 * @author Joris Scharpff
 */
@SuppressWarnings("serial")
public class GameServerConfig implements Serializable {
	/** The maximum number of players that can join the game */
	protected int maxplayers;
	
	/** If true, planning is immediately initiated if one player declined */
	protected boolean singleDecline;
	
	/** If true, the gameplay will be traced */
	protected boolean trace;
	
	/** If true, task method names of other players will be hidden */
	protected boolean hideMethodNames;
	
	/** If true, the TTL distribution info will be shown on the map */
	protected boolean showTTLDistribution;
	
	/** If true, the players are allowed to ask task suggestions */
	protected boolean taskSuggestions;
	
	/** If true, the game will also use the quality objective */
	protected boolean quality;
	
	/** True to enable a confirmation step on planning tasks */
	protected boolean confirmStep;
	
	/** If true, the score board will be updated on every change. Otherwise it
	 * will only be updated on game transitions */
	protected boolean scoreUpdateLive;
	
	/**
	 * Creates a new default server configuration
	 */
	public GameServerConfig( ) {
		try {
			setMaxNumPlayers( -1 );
			singleDecline = true;
			trace = true;
			hideMethodNames = true;
			showTTLDistribution = true;
			taskSuggestions = false;
			quality = false;
			confirmStep = false;
			scoreUpdateLive = false;
		} catch( GameServerConfigException e ) {
			// never thrown due to manual construction with acceptable values
		}
	}
	
	/**
	 * Sets the maximum number of players, when num is set to -1, the maximum
	 * player number will be set to the number of available portfolios
	 * 
	 * @param num The number of players
	 * @throws GameServerConfigException if the number is not a positive integer
	 * or -1
	 */
	public void setMaxNumPlayers( int num ) throws GameServerConfigException {
		if( num < -1 || num == 0 ) throw new GameServerConfigException( GameServerConfigError.InvalidMaxPlayers, "" + num );
		
		this.maxplayers = num;
	}
	
	/**
	 * @return The maximum number of players that can joint this game
	 */
	public int getMaxNumPlayers( ) {
		return maxplayers;
	}
	
	/**
	 * @return True if the game should go to replan immediately when a player
	 * declines the new joint plan
	 */
	public boolean singleDecline( ) {
		return singleDecline;
	}
	
	/**
	 * @return True if the gameplay should be traced
	 */
	public boolean trace( ) {
		return trace;
	}
	
	/**
	 * @return True if the method names of other players should be hidden in the
	 * interface
	 */
	public boolean hideMethodNames( ) {
		return hideMethodNames;
	}
	
	/**
	 * @return True if the distribution of network TTL should be shown on the
	 * map widget whenever planned tasks are selected
	 */
	public boolean showTTLDistribution( ) {
		return showTTLDistribution;
	}
	
	/**
	 * @return True if the ServiceProviders are allowed to request suggestions
	 * for their tasks
	 */
	public boolean allowTaskSuggestions( ) {
		return taskSuggestions;
	}
	
	/**
	 * @return True if the game should also include the quality objective
	 */
	public boolean useQuality( ) {
		return quality;
	}
	
	/**
	 * @return True to enable a confirm step in the method dialog
	 */
	public boolean confirmStep( ) {
		return confirmStep;
	}
	
	/**
	 * @return True if the score board should be updated on every score change,
	 * otherwise only game state changes update the score.
	 */
	public boolean scoreUpdateLive( ) {
		return scoreUpdateLive;
	}
}
