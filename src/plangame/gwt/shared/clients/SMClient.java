/**
 * @file SMClient.java
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
 * @date         18 mrt. 2013
 * @project      NGI
 * @company      Almende B.V.
 */
package plangame.gwt.shared.clients;

import plangame.model.object.BasicID;

/**
 * ServerManager client
 *
 * @author Joris Scharpff
 */
@SuppressWarnings("serial")
public class SMClient extends Client {
	/** Empty constructor for GWT RPC */
	@Deprecated protected SMClient( ) { }
	
	/**
	 * Creates a new SMClient
	 * 
	 * @param clientID The ID of the client
	 */
	public SMClient( BasicID clientID ) {
		super( clientID, ClientType.ServerManager );
	}
	
	/**
	 * @see plangame.gwt.shared.clients.Client#update(plangame.gwt.shared.clients.Client)
	 */
	@Override
	public void update( Client client ) {
		// nothing much to do for now
	}

}
