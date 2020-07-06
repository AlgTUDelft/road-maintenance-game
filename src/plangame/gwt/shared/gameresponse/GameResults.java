/**
 * @file GameResults.java
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
 * @date         24 sep. 2012
 * @project      NGI
 * @company      Almende B.V.
 */
package plangame.gwt.shared.gameresponse;

import java.util.List;

import plangame.game.plans.JointPlan;
import plangame.game.plans.PlanTask;
import plangame.model.object.BasicID;
import plangame.model.time.TimePoint;


/**
 * Container for the player results after execution, used to update client view
 * on the game results  
 * 
 * @author Joris Scharpff
 */
@SuppressWarnings ("serial" )
public class GameResults extends GameResponse {
	/** The current game time */
	protected TimePoint gametime;
	
	/** The joint plan after execution */
	protected JointPlan jointplan;
	
	/** List of completed plan tasks */
	protected List<PlanTask> completed;
	
	/** List of delayed methods */
	protected List<PlanTask> delayed;
	
	/** Empty constructor for GWT RPC */
	@Deprecated protected GameResults( ) { }
	
	/**
	 * Creates a new results object
	 * 
	 * @param gameID The sending game server ID
	 * @param gametime The current game time
	 * @param jointplan The joint plan after execution (including delays)
	 * @param scores The player scores
	 * @param completed The list of tasks that completed this step
	 * @param delayed The list of tasks that delayed
	 */
	public GameResults( BasicID gameID, TimePoint gametime, JointPlan jointplan, List<PlanTask> completed, List<PlanTask> delayed ) {
		super( gameID );
		
		this.gametime = gametime;
		this.jointplan = jointplan;
		this.completed = completed;
		this.delayed = delayed;
	}
	
	/** @return The current game time */
	public TimePoint getGameTime( ) { return gametime; }
	
	/** @return The joint plan */
	public JointPlan getJointPlan( ) { return jointplan; }
		
	/** @return The list of completed methods in the last execution round */
	public List<PlanTask> getCompleted( ) { return completed; }
	
	/** @return The list of delayed methods in the last execution round */
	public List<PlanTask> getDelayed( ) { return delayed; }

}
