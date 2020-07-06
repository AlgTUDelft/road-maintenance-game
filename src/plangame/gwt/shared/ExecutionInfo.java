/**
 * @file ExecutionInfo.java
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
 * @date         26 aug. 2013
 * @project      NGI
 * @company      Almende B.V.
 */
package plangame.gwt.shared;

import java.io.Serializable;

import plangame.game.plans.PlanStepResult;
import plangame.gwt.shared.enums.ExecutionMode;

/**
 * Contains all relevant information for plan execution
 *
 * @author Joris Scharpff
 */
@SuppressWarnings("serial")
public class ExecutionInfo implements Serializable {
	/** The execution mode we are currently running */
	protected ExecutionMode mode;
	
	/** The results of the last execution step, need to be tracked because
	 * otherwise the game might end up in a deadlock when the game is awaiting
	 * methods to be processed and there is no GM connected or aware of this.
	 */
	protected PlanStepResult pending;
	
	/** The sleep between two consecutive execution rounds */
	protected long sleep;
	
	/** Empty constructor for GWT RPC */
	@Deprecated protected ExecutionInfo( ) { }
	
	/**
	 * Creates a new execution info object
	 * 
	 * @param mode The execution mode
	 * @param sleep The sleep between rounds 
	 */
	public ExecutionInfo( ExecutionMode mode, long sleep ) {
		setMode( mode );
		setSleep( sleep );
		clearResults( );
	}
	
	/**
	 * Copies an existing execution info object but ignores the results
	 * 
	 * @param execinfo The execution info to copy
	 */
	public ExecutionInfo( ExecutionInfo execinfo ) {
		this( execinfo.getMode( ), execinfo.getSleep( ) );
	}
	
	/**
	 * Sets the current execution mode
	 * 
	 * @param mode The mode
	 */
	public void setMode( ExecutionMode mode ) {
		this.mode = mode;
	}
	
	/**
	 * @return The current execution mode
	 */
	public ExecutionMode getMode( ) { return mode; }
	
	/**
	 * @param mode The mode to check against
	 * @return True if the specified mode matches the execution mode
	 */
	public boolean isMode( ExecutionMode mode ) {
		return this.mode == mode;
	}
	
	/**
	 * Sets the sleep between consecutive rounds
	 * 
	 * @param sleep The sleep in milliseconds
	 */
	public void setSleep( long sleep ) {
		this.sleep = sleep;
	}
	
	/**
	 * @return The sleep between rounds in milliseconds
	 */
	public long getSleep( ) { return sleep; }
	
	/**
	 * Sets the current pending results
	 * 
	 * @param result The current pending results
	 */
	public void setResult( PlanStepResult result ) {
		this.pending = result;
	}
	
	/**
	 * Updates the pending results
	 * 
	 * @param result The pending results to update to
	 */
	public void updateResult( PlanStepResult result ) {
		this.pending.update( result );
	}
	
	/**
	 * Clears the current pending results
	 */
	public void clearResults( ) {
		this.pending = null;
	}

	/**
	 * @return The current game results
	 */
	public PlanStepResult getPendingResults( ) {
		return pending;
	}
}
