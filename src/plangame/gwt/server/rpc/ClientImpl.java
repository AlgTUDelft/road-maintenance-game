/**
 * @file ClientImpl.java
 * @brief Short description of file
 * 
 *        This file is created at Almende B.V. It is open-source software and
 *        part of the Common
 *        Hybrid Agent Platform (CHAP). A toolbox with a lot of open-source
 *        tools, ranging from
 *        thread pools and TCP/IP components to control architectures and
 *        learning algorithms.
 *        This software is published under the GNU Lesser General Public license
 *        (LGPL).
 * 
 *        Copyright © 2012 Joris Scharpff <joris@almende.com>
 * 
 * @author Joris Scharpff
 * @date 14 dec. 2012
 * @project NGI
 * @company Almende B.V.
 */
package plangame.gwt.server.rpc;

import java.util.List;

import plangame.gwt.client.rpc.ClientRPC;
import plangame.gwt.server.Server;
import plangame.gwt.server.resource.locale.Lang;
import plangame.gwt.shared.LogType;
import plangame.gwt.shared.clients.Client;
import plangame.gwt.shared.events.DisconnectEvent.DisconnectReason;
import plangame.gwt.shared.events.Event;
import plangame.gwt.shared.exceptions.ClientNotConnectedException;
import plangame.gwt.shared.exceptions.EventException;
import plangame.gwt.shared.exceptions.GameServerException;
import plangame.gwt.shared.exceptions.NoServerException;
import plangame.gwt.shared.exceptions.SessionExpiredException;
import plangame.gwt.shared.requests.ConnectRequest;
import plangame.gwt.shared.serverresponse.ConnectResponse;
import plangame.gwt.shared.state.GameServerInfo;
import plangame.model.exceptions.InvalidObjectException;
import plangame.model.object.BasicID;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * 
 * @author Joris Scharpff
 */
@SuppressWarnings ("serial" )
public class ClientImpl extends RemoteServiceServlet implements ClientRPC {
	/**
	 * @see plangame.gwt.client.rpc.ClientRPC#listen(java.lang.String)
	 */
	@Override
	public List<Event> listen( BasicID clientID ) throws NoServerException, ClientNotConnectedException, EventException {
		return getServer( ).listen( getClient( clientID ) );
	}
	
	/**
	 * @see plangame.gwt.client.rpc.ClientRPC#connect(plangame.gwt.shared.requests.ConnectRequest)
	 */
	@Override
	public ConnectResponse connect( ConnectRequest request ) throws NoServerException, InvalidObjectException, SessionExpiredException, GameServerException {
		// check if this is a connect or reconnect
		if( request.getClientID( ) == null ) {
			return getServer( ).connect( request );			
		} else {
			return getServer( ).reconnect( request );
		}
	}
	
	/**
	 * @see plangame.gwt.client.rpc.ClientRPC#disconnect(java.lang.String)
	 */
	@Override
	public void disconnect( BasicID clientID ) throws NoServerException, ClientNotConnectedException {
		getServer( ).disconnect( getClient( clientID ), DisconnectReason.ClientDisconnect );
	}

	/**
	 * @see plangame.gwt.client.rpc.ClientRPC#closing(plangame.model.object.BasicID)
	 */
	@Override
	public void closing( BasicID clientID ) throws NoServerException, ClientNotConnectedException {
		getServer( ).closing( getClient( clientID ) );
	}
	
	/**
	 * @see plangame.gwt.client.rpc.ClientRPC#getServerList()
	 */
	@Override
	public List<GameServerInfo> getServerList( ) throws NoServerException {
		return getServer( ).getGameServers( );
	}

	/**
	 * @return The main server instance
	 * @throws NoServerException if there is no server running
	 */
	public Server getServer( ) throws NoServerException {
		final Server server = Server.getInstance( );
		
		if( server == null ) throw new NoServerException( Lang.get( "NoServer" ) );
		return server;
	}

	/**
	 * Retrieves the client with the specified ID from the server
	 * 
	 * @param clientID The client ID
	 * @return The connected client
	 * @throws NoServerException
	 * @throws ClientNotConnectedException
	 */
	public Client getClient( BasicID clientID ) throws NoServerException, ClientNotConnectedException {
		final Client client = getServer( ).getClient( clientID );
		if( client == null ) {
			final String errormsg = Lang.get( "ClientNotConnected", clientID.toString( ) );
			getServer( ).log( LogType.Error, errormsg );
			throw new ClientNotConnectedException( errormsg );
		}

		return client;
	}

	/**
	 * @see plangame.gwt.client.rpc.ClientRPC#log(java.lang.String, java.lang.String, plangame.gwt.shared.LogType)
	 */
	@Override
	public Boolean log( BasicID clientID, String message, LogType loglevel ) throws NoServerException {
		return new Boolean( getServer( ).logClientMessage( clientID, message, loglevel ) );
	}
}
