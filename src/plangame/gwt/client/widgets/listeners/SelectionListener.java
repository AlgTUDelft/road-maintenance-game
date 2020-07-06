/**
 * @file SelectionListener.java
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
 * @date         2 sep. 2013
 * @project      NGI
 * @company      Almende B.V.
 */
package plangame.gwt.client.widgets.listeners;

import plangame.game.plans.PlanTask;

/**
 * Listener for selection change events
 *
 * @author Joris Scharpff
 */
public interface SelectionListener extends ViewListener {
	/**
	 * Called when the selection changes in the widget
	 * 
	 * @param source The source object changing the selection
	 * @param prevsel The previously selected method
	 * @param newsel The newly selected method
	 */
	public void onSelectionChange( Object source, PlanTask prevsel, PlanTask newsel );
}
