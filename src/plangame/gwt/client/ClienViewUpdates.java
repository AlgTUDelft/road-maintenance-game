/**
 * @file ClienViewUpdates.java
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
 * @date         28 aug. 2013
 * @project      NGI
 * @company      Almende B.V.
 */
package plangame.gwt.client;

import plangame.gwt.shared.clients.Client;

/**
 * Updates that are presented to both view and UI
 *
 * @author Joris Scharpff
 */
public interface ClienViewUpdates {
	/**
	 * Called whenever the client object changes
	 * 
	 * @param oldclient The previous client
	 * @param newclient The new client
	 */
	public void onClientChange( Client oldclient, Client newclient );
}
