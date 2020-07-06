/**
 * @file SBClient.java
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
 * @date         15 sep. 2013
 * @project      NGI
 * @company      Almende B.V.
 */
package plangame.gwt.shared.clients;

import plangame.model.object.BasicID;

/**
 * Client for score board view
 *
 * @author Joris Scharpff
 */
@SuppressWarnings("serial")
public class SBClient extends GameClient {

	/** Empty constructor for GWT RPC */
	@Deprecated protected SBClient( ) { }
	
	/**
	 * Creates a new SBClient
	 * 
	 * @param clientID The client ID
	 */
	public SBClient( BasicID clientID ) {
		super( clientID, ClientType.ScoreBoard );
	}
}
