/**
 * @file Player.java
 * @brief [brief description]
 *
 * This file is created at Almende B.V. It is open-source software and part of the Common
 * Hybrid Agent Platform (CHAP). A toolbox with a lot of open-source tools, ranging from
 * thread pools and TCP/IP components to control architectures and learning algorithms.
 * This software is published under the GNU Lesser General Public license (LGPL).
 *
 * Copyright � 2012 Joris Scharpff <joris@almende.com>
 *
 * @author       Joris Scharpff
 * @date         7 sep. 2012
 * @project      NGI
 * @company      Almende B.V.
 */
package plangame.gwt.shared.clients;

import plangame.model.object.BasicID;




/**
 * Asset manager client
 * 
 * @author Joris Scharpff
 */
@SuppressWarnings ("serial" )
public class GMClient extends GameClient {
	/** Empty constructor for GWT */
	@Deprecated protected GMClient( ) { }
	
	/**
	 * Creates a new GameManager Client
	 * 
	 * @param ID The client ID
	 */
	public GMClient( BasicID ID ) {
		super( ID, ClientType.GameManager );
	}
}
