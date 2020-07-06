/**
 * @file DelayDialogResult.java
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
package plangame.gwt.client.gamemanager.dialogs;

import java.util.List;

import plangame.game.plans.PlanTask;

/**
 * Result for a delay dialog
 *
 * @author Joris Scharpff
 */
public class DelayDialogResult {
	/** The planned tasks to be delayed */
	protected List<PlanTask> delayed;
	
	/** The planned tasks to complete */
	protected List<PlanTask> completed;
	
	/** Continue execution after processing */
	protected boolean contexec;
	
	/**
	 * Creates a new result container
	 * 
	 * @param delayed The list of delayed plan tasks
	 * @param completed The list of completed plan tasks
	 * @param contexec True to continue execution after processing
	 */
	public DelayDialogResult( List<PlanTask> delayed, List<PlanTask> completed, boolean contexec ) {
		this.delayed = delayed;
		this.completed = completed;
		this.contexec = contexec;
	}
	
	/** @return The list of delayed methods */
	public List<PlanTask> getDelayed( ) { return delayed; }
	
	/** @return The list of completed methods */
	public List<PlanTask> getCompleted( ) { return completed; }
	
	/** @return True if the execution should continue after processing */
	public boolean execContinue( ) { return contexec; }
}
