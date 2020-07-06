/**
 * @file MethodDialogCSS.java
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
 * @date         27 aug. 2013
 * @project      NGI
 * @company      Almende B.V.
 */
package plangame.gwt.client.resource.css;

import com.google.gwt.resources.client.CssResource;

/**
 *
 * @author Joris Scharpff
 */
public interface MethodDialogCSS extends CssResource {
	/** @return The main panel */
	String mainpanel( );
	
	/** @return The button panel */
	String buttonpanel( );
	
	/** @return The listboxes */
	String datalist( );
	
	/** @return The task info container */
	String taskcontainer( );
	
	/** @return The task info side panel */
	String taskinfo( );
	
	/** @return The task name label */
	String taskname( );
	
	/** @return The task description label */
	String taskdesc( );
	
	/** @return The map */
	String map( );
	
	/** @return The method container */
	String methodcontainer( );
	
	/** @return The method info part */
	String methodinfo( );
	
	/** @return Method name label */
	String methodname( );
	
	/** @return The method description text */
	String methoddesc( );
	
	/** @return The grid title */
	String gridtitle( );
	
	/** @return Method info grid */
	String methodgrid( );
	
	/** @return Method grid if delayed label */
	@ClassName("methodgrid-ifdelayed")
	String methodgridifdelayed( );
	
	/** @return Method info grid label column */
	@ClassName("methodgrid-label")
	String methodgridlabel( );

	/** @return Method info grid value column */
	@ClassName("methodgrid-value")
	String methodgridvalue( );
	
	/** @return Method grid operator symbol column */
	@ClassName("methodgrid-operator")
	String methodgridop( );
	
	/** @return Method info grid total cell */
	@ClassName("methodgrid-total")
	String methodgridtotal( );
	
	/** @return The plan step panel */
	String planpanel( );
	
	/** @return The time graph panel */
	String timegraph( );
	
	/** @return The week number selector */
	String numweek( );
	
	/** @return The plot area */
	String plots( );
	
	/** @return The confirm step panel */
	String confirmpanel( );
	
	/** @return The confirm grid */
	String confirmgrid( );
	
	/** @return Confirm grid header */
	@ClassName("confirmgrid-header")
	String confirmgridheader( );
	
	/** @return Confirm grid label */
	@ClassName("confirmgrid-label")
	String confirmgridlabel( );
	
	/** @return Confirm grid value */
	@ClassName("confirmgrid-value")
	String confirmgridvalue( );
	
	/** @return Confirm grid total column */
	@ClassName("confirmgrid-totalcol")
	String confirmgridtotalcol( );
	
	/** @return Confirm grid total row */
	@ClassName("confirmgrid-totalrow")
	String confirmgridtotalrow( );
	
	/** @return The CSS for disabled labels */
	String disabled( );
}
