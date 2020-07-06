/**
 * @file GMCSS.java
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
 * @date         10 apr. 2013
 * @project      NGI
 * @company      Almende B.V.
 */
package plangame.gwt.client.resource.css;

import com.google.gwt.resources.client.CssResource;

/**
 * The GM CSS rules
 *
 * @author Joris Scharpff
 */
public interface GMCSS extends CssResource {
	/** @return The tab panel layout */
	String tabpanel( );
	
	/** @return The player manager widget */
	String players( );
	
	/** @return The planning widget */
	String plan( );
	
	/** @return The map widget */
	String map( );		
}