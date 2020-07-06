/**
 * @file StartGameDialogCSS.java
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
 * @date         31 aug. 2013
 * @project      NGI
 * @company      Almende B.V.
 */
package plangame.gwt.client.resource.css;

import com.google.gwt.resources.client.CssResource;

/**
 * CSS for the start game dialog
 *
 * @author Joris Scharpff
 */
@SuppressWarnings("javadoc")
public interface StartGameDialogCSS extends CssResource {
	String mainpanel( );
	String titlepanel( );
	String welcometitle( );
	String colourlabel( );
	String colourdiv( );
	String infopanel( );
	String welcometext( );
	String maplabel( );
	String map( );
	String buttonpanel( );
	String startbutton( );
}
