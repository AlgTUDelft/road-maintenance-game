/**
 * @file SpeedProfiler.java
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
 * @date         30 jan. 2013
 * @project      NGI
 * @company      Almende B.V.
 */
package plangame.gwt.client.util;


/**
 * Simple code profiler
 *
 * @author Joris Scharpff
 */
public abstract class UIProfiler {
	/**
	 * Creates a new profiler
	 * 
	 * @param description The descriptive message
	 */
	public UIProfiler( String description ) {
		final long starttime = System.currentTimeMillis( );
		profile( );
		System.out.println( "[PROFILING] " + description + ": " + (System.currentTimeMillis( ) - starttime) + " msec" );
	}
	
	/**
	 * The code to profile
	 */
	public abstract void profile( );
}
