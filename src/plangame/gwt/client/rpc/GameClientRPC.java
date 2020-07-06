/**
 * @file GameClientRPC.java
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
 * @date         17 mrt. 2013
 * @project      NGI
 * @company      Almende B.V.
 */
package plangame.gwt.client.rpc;

import plangame.gwt.shared.exceptions.ClientNotConnectedException;
import plangame.gwt.shared.exceptions.ClientNotInGameException;
import plangame.gwt.shared.exceptions.GameServerException;
import plangame.gwt.shared.exceptions.NoServerException;
import plangame.gwt.shared.exceptions.NoSuchGameServerException;
import plangame.gwt.shared.gameresponse.JoinGameResponse;
import plangame.gwt.shared.gameresponse.RestoreResponse;
import plangame.gwt.shared.requests.JoinGameRequest;
import plangame.model.object.BasicID;

import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 *
 * @author Joris Scharpff
 */

@RemoteServiceRelativePath ("GameClientRPC" )
public interface GameClientRPC extends ClientRPC {
	/**
	 * Joins the specified game server
	 * 
	 * @param request The join game request
	 * @return The join result object
	 * @throws GameServerException if the join failed
	 * @throws NoSuchGameServerException 
	 * @throws ClientNotConnectedException 
	 * @throws NoServerException 
	 */
	public JoinGameResponse joinGame( JoinGameRequest request ) throws GameServerException, ClientNotConnectedException, NoSuchGameServerException, NoServerException;
	
	/**
	 * Requests the server for all relevant information to restore the current
	 * view.
	 * 
	 * @param clientID The client requesting the restore info
	 * @return The server state
	 * @throws NoServerException
	 * @throws NoSuchGameServerException 
	 * @throws ClientNotInGameException 
	 * @throws ClientNotConnectedException 
	 */
	public RestoreResponse restoreClient( BasicID clientID ) throws NoServerException, ClientNotConnectedException, ClientNotInGameException, NoSuchGameServerException;	
}
