/**
 * @file PlanEvent.java
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
 * @date         19 sep. 2012
 * @project      NGI
 * @company      Almende B.V.
 */
package plangame.gwt.shared.events;


/**
 * @author Joris Scharpff
 */
@SuppressWarnings("serial")
public class PlanEvent extends Event {
	
	/** True if the players should submit plans */
	protected boolean plan;

	/** Empty constructor for GWT RPC */
	@Deprecated protected PlanEvent( ) { }
	
	/**
	 * Creates a new plan event
	 * 
	 * @param plan True if the players should submit a new plan
	 */
	public PlanEvent( boolean plan ) {
		super( );
		
		this.plan = plan;
	}
	
	/** @return Whether the players should submit a plan */
	public boolean submitPlan( ) { return plan; }

	/**
	 * @see plangame.gwt.shared.events.Event#getName()
	 */
	@Override
	public String getName( ) {
		return "Plan";
	}
}
