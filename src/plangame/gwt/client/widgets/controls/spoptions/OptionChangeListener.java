/**
 * @file OptionChangeListener.java
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
package plangame.gwt.client.widgets.controls.spoptions;

/**
 * Listener for option change event
 *
 * @author Joris Scharpff
 */
public interface OptionChangeListener {
	/**
	 * Called whenever the TTL display format is changed
	 * 
	 * @param relative True if relative display is now set
	 */
	public void onSetTTLRelative( boolean relative );
	
	/**
	 * Called when the TTL on map option changes
	 * 
	 * @param onmap True if the TTL should be displayed on the map widggt
	 */
	public void onSetTTLOnMap( boolean onmap );
}
