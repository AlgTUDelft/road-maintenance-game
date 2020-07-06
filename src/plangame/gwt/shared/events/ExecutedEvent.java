/**
 * @file ExecuteEvent.java
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
 * @date         23 sep. 2012
 * @project      NGI
 * @company      Almende B.V.
 */
package plangame.gwt.shared.events;

import plangame.gwt.shared.gameresponse.GameResults;


/**
 * @author Joris Scharpff
 */
@SuppressWarnings("serial")
public class ExecutedEvent extends Event {
	/** The results of execution */
	protected GameResults results;
	
	/** Empty constructor for GWT RPW */
	@Deprecated protected ExecutedEvent( ) { }
	
	/**
	 * Creates a new execute event
	 * 
	 * @param results The results after execution
	 */
	public ExecutedEvent( GameResults results ) {
		super( );
		
		this.results = results;
	}
	
	/** @return The result container */
	public GameResults getResults( ) { return results; }

	/**
	 * @see plangame.gwt.shared.events.Event#getName()
	 */
	@Override
	public String getName( ) {
		return "Execute Results";
	}
}
