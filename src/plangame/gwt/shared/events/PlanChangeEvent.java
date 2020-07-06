/**
 * @file PlanChangeEvent.java
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
 * @date         23 sep. 2013
 * @project      NGI
 * @company      Almende B.V.
 */
package plangame.gwt.shared.events;

import plangame.game.plans.PlanChange;
import plangame.gwt.shared.clients.SPClient;

/**
 * Notifies the score board of a plan change, this allow the SB to gradually
 * update its scores instead of all at once on a joint plan set
 *
 * @author Joris Scharpff
 */
@SuppressWarnings("serial")
public class PlanChangeEvent extends Event {
	/** The client performing the change */
	protected SPClient client;
	
	/** The plan change */
	protected PlanChange change;
	
	/** Empty constructor for GWT RPC */
	@Deprecated protected PlanChangeEvent( ) { }
	
	/**
	 * Creates a new PlanChangeEvent
	 * 
	 * @param client The client performing the change
	 * @param change The plan change
	 */
	public PlanChangeEvent( SPClient client, PlanChange change ) {
		super( );
		
		this.client = client;
		this.change = change;
	}
	
	/** @return The client */
	public SPClient getClient( ) { return client; }
	
	/** @return The plan change */
	public PlanChange getPlanChange( ) { return change; }
	
	/**
	 * @see plangame.gwt.shared.events.Event#getName()
	 */
	@Override public String getName( ) { return "Plan change event"; }
}
