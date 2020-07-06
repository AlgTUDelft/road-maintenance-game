/**
 * @file ServerConfig.java
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
 * @date         29 mei 2013
 * @project      NGI
 * @company      Almende B.V.
 */
package plangame.gwt.server.config;

/**
 * Class that contains all server configurable options, includes defaults
 *
 * @author Joris Scharpff
 */
public class ServerConfig {
	/** Use session restore */
	protected boolean usesessions;
	
	/** Maximal time to wait for client to receive an event before reporting
	 * failure (in milliseconds)
	 */
	protected long clienteventwait;
	
	/** Maximal time to wait for event thread to end normally, after this time
	 * it will be forced to end (in milliseconds)
	 */
	protected long eventthreadtime;
	
	/**
	 * Creates a new serverconfig, sets defaults
	 */
	public ServerConfig( ) {
		usesessions = true;
		clienteventwait = 30000;
		eventthreadtime = 1000;
	}
	
	/**
	 * @return Whether we use sessions for client restores
	 */
	public boolean useSessions( ) { return usesessions; }
	
	/**
	 * @return The maximal time a client may take to respond to receiving an
	 * event that uses the fireAndWait mechanism. If the client failed to respond
	 * failure will be reported. The duration is in milliseconds.
	 */
	public long getClientEventTimeout( ) { return clienteventwait; }
	
	/**
	 * @return The maximal time in milliseconds that the server waits for the
	 * event manager thread to end normally. If the thread fails to do so, it
	 * will be terminated.
	 */
	public long getEventThreadTime( ) { return eventthreadtime; }
}
