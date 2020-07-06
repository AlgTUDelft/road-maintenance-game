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

import plangame.gwt.client.rpc.GameClientRPC;
import plangame.gwt.server.GameServer;
import plangame.gwt.server.resource.locale.Lang;
import plangame.gwt.shared.LogType;
import plangame.gwt.shared.clients.GameClient;
import plangame.gwt.shared.exceptions.ClientNotConnectedException;
import plangame.gwt.shared.exceptions.ClientNotInGameException;
import plangame.gwt.shared.exceptions.GameServerException;
import plangame.gwt.shared.exceptions.NoServerException;
import plangame.gwt.shared.exceptions.NoSuchGameServerException;
import plangame.gwt.shared.gameresponse.JoinGameResponse;
import plangame.gwt.shared.gameresponse.RestoreResponse;
import plangame.gwt.shared.requests.JoinGameRequest;
import plangame.model.object.BasicID;

/**
 * Server side implementation of all game client RPC functions
 * 
 * @author Joris Scharpff
 */
@SuppressWarnings ("serial" )
public class GameClientImpl extends ClientImpl implements GameClientRPC {
	/**
	 * @see plangame.gwt.client.rpc.ClientRPC#joinGame(java.lang.String, java.lang.String)
	 */
	@Override
	public JoinGameResponse joinGame( JoinGameRequest request ) throws NoServerException, GameServerException, ClientNotConnectedException, NoSuchGameServerException {
		return getServer( ).joinServer( getGameServer( request.getGameID( ) ), getClient( request.getClientID( ) ), request );
	}
	
	/**
	 * @see plangame.gwt.client.rpc.GameClientRPC#restoreClient(plangame.model.object.BasicID)
	 */
	@Override
	public RestoreResponse restoreClient( BasicID clientID ) throws NoServerException, ClientNotConnectedException, ClientNotInGameException, NoSuchGameServerException {
		final GameClient client = getClient( clientID );
		return getGameServer( client ).restoreClient( client );
	}

	/**
	 * Typecasts the client to a game client 
	 * 
	 * @see plangame.gwt.server.rpc.ClientImpl#getClient(plangame.model.object.BasicID)
	 */
	@Override
	public GameClient getClient( BasicID clientID ) throws NoServerException, ClientNotConnectedException {
		return (GameClient)super.getClient( clientID );
	}
	
	/**
	 * Retrieves the game server for the client
	 * 
	 * @param client The client
	 * @return The game server to which the client is connected
	 * @throws NoServerException
	 * @throws ClientNotInGameException
	 * @throws NoSuchGameServerException 
	 * 
	 */
	protected GameServer getGameServer( GameClient client ) throws NoServerException, ClientNotInGameException, NoSuchGameServerException {
		if( client.getGameID( ) == null ) {
			final String emsg = Lang.get( "ClientNotInGame", client.getID( ).toString( ) );
			getServer( ).log( LogType.Error, emsg );
			throw new ClientNotInGameException( emsg );
		}

		// get the game server
		return getGameServer( client.getGameID( ) );
	}
	
	/**
	 * Retrieves the game server with the specified ID
	 * 
	 * @param gameID The ID of the game server
	 * @return The game server
	 * @throws NoServerException
	 * @throws NoSuchGameServerException
	 */
	protected GameServer getGameServer( BasicID gameID ) throws NoServerException, NoSuchGameServerException {
		return getServer( ).getGameServer( gameID );
	}
	
}
