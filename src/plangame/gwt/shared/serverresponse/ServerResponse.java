/**
 * @file ServerResponse.java
 * @brief Short description of file
 *
 * This file is created at Almende B.V. It is open-source software and part of the Common
 * Hybrid Agent Platform (CHAP). A toolbox with a lot of open-source tools, ranging from
 * thread pools and TCP/IP components to control architectures and learning algorithms.
 * This software is published under the GNU Lesser General Public license (LGPL).
 *
 * Copyright © 2012 Joris Scharpff <joris@almende.com>
 *
 * @author       Joris Scharpff
 * @date         10 dec. 2012
 * @project      NGI
 * @company      Almende B.V.
 */
package plangame.gwt.shared.serverresponse;

import java.io.Serializable;

import plangame.model.object.BasicID;

/**
 * Base class for all server responses
 *
 * @author Joris Scharpff
 */
@SuppressWarnings("serial")
public class ServerResponse implements Serializable {
	/** The ID of the server */
	protected BasicID serverID;
	
	/** Empty constructor for GWT */
	@Deprecated protected ServerResponse( ) {}
	
	/**
	 * Creates a new server response
	 * 
	 * @param serverID The ID of the sending server
	 */
	public ServerResponse( BasicID serverID ) {
		this.serverID = serverID;
	}
	
	/**
	 * @return The ID of the sending server
	 */
	public BasicID getServerID( ) {
		return serverID;
	}
}
