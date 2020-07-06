/**
 * @file GameState.java
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
package plangame.gwt.shared.enums;


/**
 * The current state of the game
 * 
 * @author Joris Scharpff
 */
public enum GameState {
	/** Initialising */
	Initialising,	
	/** The game is starting */
	Starting,
	/** In a round awaiting planning or execution */
	Idle,
	/** The planning phase */
	Planning,
	/** Accept/decline phase */
	Accept,
	/** The game is being executed */
	Executing,
	/** The game has finished */
	Finished;
}
