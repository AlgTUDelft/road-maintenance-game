/**
 * @file InvalidPlanException.java
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
 * @date         1 okt. 2013
 * @project      NGI
 * @company      Almende B.V.
 */
package plangame.gwt.shared.exceptions;

import java.util.ArrayList;
import java.util.List;

import plangame.game.plans.PlanError;

/**
 * Thrown whenever any game action is prohibited because the joint plan is invalid
 *
 * @author Joris Scharpff
 */
@SuppressWarnings("serial")
public class InvalidPlanException extends Exception {
	/** The list of plan errors making it invalid */
	protected List<PlanError> errors;
	
	/** Empty constructor for GWT RPC */
	@Deprecated protected InvalidPlanException( ) { }
	
	/**
	 * Creates a new invalid plan exception
	 * 
	 * @param msg The exception message
	 */
	public InvalidPlanException( String msg ) {
		super( msg );
		
		errors = new ArrayList<PlanError>( );
	}
	
	/**
	 * Creates an invalid plan exception
	 * 
	 * @param msg The error message
	 * @param errors The list of encountered plan errors
	 */
	public InvalidPlanException( String msg, List<PlanError> errors ) {
		this( msg );
		
		this.errors.addAll( errors );
	}
	
	/**
	 * @return The list of errors
	 */
	public List<PlanError> getErrors( ) {
		return errors;
	}
}
