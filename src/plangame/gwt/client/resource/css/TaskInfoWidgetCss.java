/**
 * @file TaskInfoWidgetCss.java
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
 * @date         16 apr. 2013
 * @project      NGI
 * @company      Almende B.V.
 */
package plangame.gwt.client.resource.css;

import com.google.gwt.resources.client.CssResource;

/**
 * CSS for the task info panel
 *
 * @author Joris Scharpff
 */
public interface TaskInfoWidgetCss extends CssResource {
	/** @return The main panel */
	String infopanel( );
	
	/** @return The task card panel */
	String taskcard( );
	
	/** @return The task name */
	String taskname( );
	
	/** @return Long description text */
	String taskdesc( );
	
	/** @return The information table */
	String infogrid( );
	
	/** @return The method name */
	String methodname( );
	
	/** @return Key text label */
	String keylabel( );
	
	/** @return Value text label */
	String valuelabel( );
}
