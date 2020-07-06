/**
 * @file RequestSuggestionResult.java
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
 * @date         30 mei 2013
 * @project      NGI
 * @company      Almende B.V.
 */
package plangame.gwt.client.serviceprovider.dialogs;

import plangame.game.player.PlanPreference;
import plangame.model.tasks.Task;

/**
 * Container for the result of a request suggestion dialog
 *
 * @author Joris Scharpff
 */
public class RequestSuggestionResult {
	/** The task to request suggestion for */
	public final Task task;
	
	/** The plan preferences */
	public final PlanPreference prefs;
	
	/**
	 * Creates a new result for plan suggestions
	 * 
	 * @param prefs The player preferences
	 */
	public RequestSuggestionResult( PlanPreference prefs ) {
		this( null, prefs );
	}
	
	/**
	 * Creates a new result for task suggestions
	 * 
	 * @param task The task to get suggestion for
	 * @param prefs The planning preferences
	 */
	public RequestSuggestionResult( Task task, PlanPreference prefs ) {
		this.task = task;
		this.prefs = prefs;
	}
}
