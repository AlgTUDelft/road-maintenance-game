/**
 * @file PortfolioItemHandle.java
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
 * @date         17 sep. 2012
 * @project      NGI
 * @company      Almende B.V.
 */
package plangame.gwt.client.widgets.portfolio;

import plangame.model.tasks.Task;


/**
 * @author Joris Scharpff
 */
public interface PortfolioItemHandler {
	/**
	 * Fired when the item is selected
	 * 
	 * @param task The new selection
	 */
	public void onSelect( Task task );
}
