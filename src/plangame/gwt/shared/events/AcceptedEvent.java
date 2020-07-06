/**
 * @file AcceptedEvent.java
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

/**
 * Notifies the game manager that the plan has been accepted or declined
 * 
 * @author Joris Scharpff
 */
@SuppressWarnings("serial")
public class AcceptedEvent extends Event {
	/** True if the joint plan has been accepted */
	protected boolean accepted;
	
	/** Empty cconstructot for GWT RPC */
	@Deprecated protected AcceptedEvent( ) {}
	
	/**
	 * Creates a new AcceptedEvent
	 * 
	 * @param accepted True if the plan was accepted
	 */
	public AcceptedEvent( boolean accepted ) {
		super( );
		
		this.accepted = accepted;
	}
	
	/**
	 * @return Whether or not the joint plan was accepted
	 */
	public boolean isAccepted( ) {
		return accepted;
	}
	
	/**
	 * @see plangame.gwt.shared.events.Event#getName()
	 */
	@Override
	public String getName( ) {
		return "Plan accepted/declined event";
	}
}
