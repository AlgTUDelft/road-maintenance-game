/**
 * @file ConnectRequest.java
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

import plangame.gwt.shared.clients.Client.ClientType;
import plangame.model.object.BasicID;

/**
 * Request to (re)connect to the server
 *
 * @author Joris Scharpff
 */
@SuppressWarnings("serial")
public class ConnectRequest extends ClientRequest {
	/** The type of client that is connecting */
	protected ClientType type;
	
	/** Empty constructor for GWT RPC */
	@Deprecated protected ConnectRequest( ) { }
	
	/**
	 * Creates a new connect request for the client of the specified type
	 * 
	 * @param type The client type
	 */
	public ConnectRequest( ClientType type ) {
		// empty ID because this is a connect request
		super( null );
		
		this.type = type;
	}
	
	/**
	 * Creates a reconnect request for the client
	 * 
	 * @param clientID The client ID
	 * @param type The type of the reconnecting client
	 */
	public ConnectRequest( BasicID clientID, ClientType type ) {
		super( clientID );
		
		this.type = type;
	}
	
	/**
	 * @return The client type of the connecting client
	 */
	public ClientType getClientType( ) {
		return type;
	}
}
