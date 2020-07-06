/**
 * @file EndGameDialogCSS.java
 * @brief Short description of file
 *
 * This file is created at Almende B.V. It is open-source software and part of the Common
 * Hybrid Agent Platform (CHAP). A toolbox with a lot of open-source tools, ranging from
 * thread pools and TCP/IP components to control architectures and learning algorithms.
 * This software is published under the GNU Lesser General Public license (LGPL).
 *
 * Copyright � 2013 Joris Scharpff <joris@almende.com>
 *
 * @author       Joris Scharpff
 * @date         2 okt. 2013
 * @project      NGI
 * @company      Almende B.V.
 */
package plangame.gwt.client.resource.css;

import com.google.gwt.resources.client.CssResource;

/**
 * CSS definitions for the end game dialog
 *
 * @author Joris Scharpff
 */
@SuppressWarnings("javadoc")
public interface EndGameDialogCSS extends CssResource {
	String mainpanel( );
	String titlepanel( );
	String resultstitle( );
	String tableheader( );
	String table( );
	String totalttl( );
	String totalttlvalue( );
	String ok( );
}
