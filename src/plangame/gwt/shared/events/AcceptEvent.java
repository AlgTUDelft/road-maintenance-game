/**
 * @file AcceptEvent.java
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
 * @date         7 jan. 2013
 * @project      NGI
 * @company      Almende B.V.
 */
package plangame.gwt.shared.events;

import plangame.game.plans.JointPlan;

/**
 * Event that is send to all the clients to start the accept/decline round
 *
 * @author Joris Scharpff
 */
@SuppressWarnings("serial")
public class AcceptEvent extends Event {
	/** The joint plan to accept */
	protected JointPlan jplan;
	
	/** Empty constructor for GWT RPC */
	@Deprecated protected AcceptEvent( ) { }	
	
	/**
	 * Creates a new AcceptEvent with the joint plan
	 * 
	 * @param jplan The joint plan
	 */
	public AcceptEvent( JointPlan jplan ) {
		super( );
		
		this.jplan = jplan;
	}
	
	/**
	 * @return The joint plan
	 */
	public JointPlan getJointPlan( ) {
		return jplan;
	}

	/**
	 * @see plangame.gwt.shared.events.Event#getName()
	 */
	@Override
	public String getName( ) {
		return "Accept join plan event";
	}
}
