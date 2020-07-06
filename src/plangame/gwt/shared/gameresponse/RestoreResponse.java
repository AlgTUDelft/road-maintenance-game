/**
 * @file RestoreResponse.java
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
 * @date         25 aug. 2013
 * @project      NGI
 * @company      Almende B.V.
 */
package plangame.gwt.shared.gameresponse;

import plangame.game.plans.JointPlan;
import plangame.gwt.shared.ExecutionInfo;
import plangame.gwt.shared.state.GameInfo;
import plangame.gwt.shared.state.GameServerInfo;
import plangame.gwt.shared.state.GameServerState;
import plangame.model.object.BasicID;

/**
 * Response to a request by the client to restore its view
 *
 * @author Joris Scharpff
 */
@SuppressWarnings("serial")
public class RestoreResponse extends GameResponse {
	/** The current server info */
	protected GameServerInfo serverinfo;
	
	/** The current server state */
	protected GameServerState serverstate;
	
	/** The game information */
	protected GameInfo gameinfo;
	
	/** The current (intermediate) joint plan */
	protected JointPlan jplan;
	
	/** The current execution info if in execution mode */
	protected ExecutionInfo execinfo;
	
	/** Empty constructor for GWT RPC */
	@Deprecated protected RestoreResponse( ) { }
	
	/**
	 * Creates a new restore response
	 * 
	 * @param gameID The ID of the game server
	 * @param serverinfo The game server info
	 * @param serverstate The current game server state
	 * @param gameinfo The game info
	 * @param jplan The current (intermediate) joint plan
	 * @param execinfo The current execution info
	 */
	public RestoreResponse( BasicID gameID, GameServerInfo serverinfo, GameServerState serverstate, GameInfo gameinfo, JointPlan jplan, ExecutionInfo execinfo ) {
		super( gameID );
		
		this.serverinfo = serverinfo;
		this.serverstate = serverstate;
		this.gameinfo = gameinfo;
		this.jplan = jplan;
		this.execinfo = execinfo;
	}
	
	/** @return The game server info */
	public GameServerInfo getServerInfo( ) { return serverinfo; }
	
	/**
	 * @return The current server state
	 */
	public GameServerState getServerState( ) { return serverstate; }
	
	/** @return The static game information */
	public GameInfo getGameInfo( ) { return gameinfo; }
	
	/**
	 * @return The current (intermediate) joint plan
	 */
	public JointPlan getJointPlan( ) { return jplan; }
	
	/**
	 * @return The currently execution info, null if not executing (or not GM)
	 */
	public ExecutionInfo getExecutionInfo( ) { return execinfo; }
}
