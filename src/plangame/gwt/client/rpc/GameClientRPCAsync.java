/**
 * @file GameClientRPCAsync.java
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

import plangame.gwt.shared.gameresponse.JoinGameResponse;
import plangame.gwt.shared.gameresponse.RestoreResponse;
import plangame.gwt.shared.requests.JoinGameRequest;
import plangame.model.object.BasicID;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Async counterpart of GameClientRPC
 *
 * @author Joris Scharpff
 */
@SuppressWarnings("javadoc")
public interface GameClientRPCAsync extends ClientRPCAsync {
	public void joinGame( JoinGameRequest request, AsyncCallback<JoinGameResponse> result );
	public void restoreClient( BasicID clientID, AsyncCallback<RestoreResponse> result );
}
