/**
 * @file ClientRequest.java
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
 * @date         9 mei 2013
 * @project      NGI
 * @company      Almende B.V.
 */
package plangame.gwt.shared.requests;

import java.io.Serializable;

import plangame.model.object.BasicID;

/**
 * Base class for all client requests
 *
 * @author Joris Scharpff
 */
@SuppressWarnings("serial")
public abstract class ClientRequest implements Serializable {
	/** The ID of the sending client (null if connect request) */
	protected BasicID clientID;
	
	/** Empty constructor for GWT RPC */
	@Deprecated protected ClientRequest( ) {}
	
	/**
	 * Creates a new client request
	 * 
	 * @param clientID The ID of the client sending the request
	 */
	public ClientRequest( BasicID clientID ) {
		this.clientID = clientID;
	}
	
	/**
	 * @return The client ID
	 */
	public BasicID getClientID( ) {
		return clientID;
	}
}
