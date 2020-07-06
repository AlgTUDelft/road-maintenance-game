/**
 * @file StartGameEvent.java
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
 * @date         8 jan. 2013
 * @project      NGI
 * @company      Almende B.V.
 */
package plangame.gwt.shared.events;

import plangame.game.plans.JointPlan;
import plangame.gwt.shared.state.GameInfo;

/**
 * Notifies clients that the game is starting
 *
 * @author Joris Scharpff
 */
@SuppressWarnings("serial")
public class StartGameEvent extends Event {
	/** The game information */
	protected GameInfo gameinfo;
	
	/** The initialial joint plan */
	protected JointPlan jointplan;
	
	/** Empty constructor for GWT RPC */
	@Deprecated protected StartGameEvent( ) { }
	
	/**
	 * Creates a new start game event
	 * 
	 * @param gameinfo The game information
	 * @param jointplan The initial joint plan
	 */
	public StartGameEvent( GameInfo gameinfo, JointPlan jointplan ) {
		super( );
		
		this.gameinfo = gameinfo;
		this.jointplan = jointplan;
	}
	
	/**
	 * @return The game info
	 */
	public GameInfo getGameInfo( ) { return gameinfo; }
	
	/**
	 * @return The joint plan
	 */
	public JointPlan getJointPlan( ) {
		return jointplan;
	}
	
	/**
	 * @see plangame.gwt.shared.events.Event#getName()
	 */
	@Override
	public String getName( ) {
		return "Start game event";
	}
}
