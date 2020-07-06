/**
 * @file TTLWidget.java
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
 * @date         27 sep. 2013
 * @project      NGI
 * @company      Almende B.V.
 */
package plangame.gwt.client.widgets.listeners;

/**
 * All widgets displaying/using TTL should implement this interface 
 *
 * @author Joris Scharpff
 */
public interface TTLWidget {
	/**
	 * Called when the TTL display format changes
	 * 
	 * @param relative True to display TTL relative
	 */
	public void setDisplayRelative( boolean relative );
}
