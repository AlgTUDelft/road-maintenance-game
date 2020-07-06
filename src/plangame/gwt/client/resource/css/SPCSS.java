/**
 * @file SPCSS.java
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
 * The SP CSS rules
 *
 * @author Joris Scharpff
 */
public interface SPCSS extends CssResource {
	/** @return The main panel */
	String mainpanel( );
	
	/** @return Plan panel CSS */
	String planning( );
	
	/** @return The map widget CSS */
	String map( );
	
	/** @return The info table */
	String infotable( );
	
	/** @return The right hand size control panel */
	String controlpanel( );
	
	/** @return The control widget */
	String controller( );
	
	/** @return The help widget */
	String help( );
}