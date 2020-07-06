/**
 * @file PlanWidgetCSS.java
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
 * @date         9 apr. 2013
 * @project      NGI
 * @company      Almende B.V.
 */
package plangame.gwt.client.resource.css;

import com.google.gwt.resources.client.CssResource;

/**
 * Plan Widget CSS rules
 *
 * @author Joris Scharpff
 */
public interface PlanWidgetCSS extends CssResource {
	/** @return The task */
	String task( );
	
	/** @return Selected task */
	String taskselected( );

	/** @return Task info part */
	String taskinfo( );
	
	/** @return Task delay part */
	String taskdelay( );
		
	/** @return Task delay part pending */
	@ClassName("taskdelay-pending")
	String taskdelaypending( );
	
	/** @return Task delay part as planned */
	@ClassName("taskdelay-asplanned")
	String taskdelayasplanned( );
	
	/** @return Task delay part delayed */
	@ClassName("taskdelay-delayed")
	String taskdelaydelayed( );
	
	/** @return 

	/** @return The plan grid */
	String plangrid( );
	
	/** @return Player name labels */
	String playerlabel( );
	
	/** @return Week legend */
	String weeklegend( );
	
	/** @return Week number label */
	String weeklabel( );
	
	/** @return The current week marker */
	String weekmarker( );
	
	/** @return The week marker when the game is executing */
	@ClassName("weekmarker-executing") String weekmarkerexecuting( );
}
