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

import plangame.gwt.shared.clients.SPClient;
import plangame.model.tasks.Portfolio;


/**
 * Event to notify client of assigned portfolio
 * 
 * @author Joris Scharpff
 */
@SuppressWarnings("serial")
public class AssignPortfolioEvent extends Event {
	
	/** The client that is assigned a new portfolio */
	protected SPClient client;
	
	/** The portfolio that was assigned */
	protected Portfolio portfolio;

	/** Empty constructor for GWT RPC */
	@Deprecated protected AssignPortfolioEvent( ) { }
	
	/**
	 * Creates a new assign portfolio event
	 * 
	 * @param client The client
	 * @param portfolio Its new portfolio
	 */
	public AssignPortfolioEvent( SPClient client, Portfolio portfolio ) {
		super( );
		
		this.client = client;
		this.portfolio = portfolio;
	}

	/** @return The client */
	public SPClient getClient( ) { return client; }
	
	/** @return The portfolio that is assigned */
	public Portfolio getPortfolio( ) { return portfolio; }
	
	/**
	 * @see plangame.gwt.shared.events.Event#getName()
	 */
	@Override
	public String getName( ) {
		return "Assign Portfolio Event";
	}
}
