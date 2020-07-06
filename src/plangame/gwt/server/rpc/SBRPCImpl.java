/**
 * @file SBRPCImpl.java
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
package plangame.gwt.server.rpc;

import plangame.gwt.client.scoreboard.SBRPC;
import plangame.gwt.shared.clients.SBClient;
import plangame.gwt.shared.exceptions.ClientNotConnectedException;
import plangame.gwt.shared.exceptions.NoServerException;
import plangame.model.object.BasicID;

/**
 * Score board RPC implementation
 *
 * @author Joris Scharpff
 */
@SuppressWarnings("serial")
public class SBRPCImpl extends GameClientImpl implements SBRPC {

	/**
	 * @throws ClientNotConnectedException 
	 * @see plangame.gwt.server.rpc.ClientImpl#getClient(plangame.model.object.BasicID)
	 */
	@Override
	public SBClient getClient( BasicID clientID ) throws NoServerException, ClientNotConnectedException {
		return (SBClient)super.getClient( clientID );
	}
}
