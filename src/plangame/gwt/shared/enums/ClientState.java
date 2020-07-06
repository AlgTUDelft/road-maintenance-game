/**
 * @file PlayerState.java
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
 * @date         20 aug. 2013
 * @project      NGI
 * @company      Almende B.V.
 */
package plangame.gwt.shared.enums;

/**
 * Possible states a client can be in
 *
 * @author Joris Scharpff
 */
public enum ClientState {
	/** Initialising */
	Initialising,		
	/** Waiting for a portfolio to be assigned */
	AwaitingPortfolio,		
	/** Waiting for game to start */
	WaitingToStart,
	/** Disconnected */
	Disconnected,
	/** Reconnecting/restoring game view */
	Reconnecting,
	/** Idle, waiting for planning or execution */
	Idle,
	/** In planning round */
	InPlanning,
	/** Submitted the plan */
	Submitted,
	/** In accepting phase */
	Accepting,
	/** Accepted the joint plan */
	Accepted,
	/** Declined the joint plan */
	Declined,
	/** Currently executing the game */
	Executing,
	/** The game has been finished */
	Finished;
}
