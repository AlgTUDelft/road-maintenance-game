package plangame.gwt.server.rpc;

import java.util.List;

import plangame.gwt.client.servermanager.SMRPC;
import plangame.gwt.server.resource.locale.Lang;
import plangame.gwt.shared.LogType;
import plangame.gwt.shared.clients.Client;
import plangame.gwt.shared.clients.SPClient;
import plangame.gwt.shared.events.DisconnectEvent.DisconnectReason;
import plangame.gwt.shared.exceptions.ClientNotConnectedException;
import plangame.gwt.shared.exceptions.GameServerException;
import plangame.gwt.shared.exceptions.InvalidClientTypeException;
import plangame.gwt.shared.exceptions.NoServerException;
import plangame.gwt.shared.exceptions.NoSuchGameServerException;
import plangame.gwt.shared.state.GameServerInfo;
import plangame.model.object.BasicID;

/**
 * The server side implementation of the RPC service for the server manager.
 */
@SuppressWarnings( "serial" )
public class SMRPCImpl extends ClientImpl implements SMRPC {
	/**
	 * @see plangame.gwt.client.servermanager.SMRPC#createGameServer(plangame.gwt.shared.state.GameServerInfo)
	 */
	@Override
	public GameServerInfo createGameServer( GameServerInfo serverInfo ) throws NoServerException, GameServerException {
		return getServer( ).createGameServer( serverInfo.getID( ), serverInfo.getName( ), serverInfo.getGameFile( ), serverInfo.getConfig( ) );
	}
	
	/**
	 * @see plangame.gwt.client.servermanager.SMRPC#restartServer(java.lang.String)
	 */
	@Override
	public GameServerInfo restartServer( BasicID gameID ) throws NoServerException, GameServerException, NoSuchGameServerException {
		return getServer( ).restartServer( getServer( ).getGameServer( gameID ) );
	}
	
	/**
	 * @see plangame.gwt.client.servermanager.SMRPC#endServer(plangame.model.object.BasicID)
	 */
	@Override
	public int endServer( BasicID gameID ) throws GameServerException, NoSuchGameServerException, NoServerException {
		return getServer( ).endServer( getServer( ).getGameServer( gameID ) );
	}
	
	/**
	 * @see plangame.gwt.client.servermanager.SMRPC#getClientList()
	 */
	@Override
	public List<Client> getClientList( ) throws NoServerException {
		// returns a list of all clients connected to the server
		return getServer( ).getClients( );
	}
	
	/**
	 * @see plangame.gwt.client.servermanager.SMRPC#reassign(plangame.model.object.BasicID, plangame.model.object.BasicID)
	 */
	@Override
	public void reassign( BasicID clientID, BasicID targetID ) throws NoServerException, ClientNotConnectedException, InvalidClientTypeException {
		// check both clients are connected and of the correct type
		try {
			final SPClient client = (SPClient) getClient( clientID );
			final SPClient target = (SPClient) getClient( targetID );
			
			// little check to prevent reassigning of the same client
			if( client.equals( target ) ) return;
			
			getServer( ).reassign( client, target );
		} catch( ClassCastException cce ) {
			final String errormsg = Lang.get( "InvalidClientType", clientID.toString( ) );
			getServer( ).log( LogType.Error, errormsg );
			throw new InvalidClientTypeException( errormsg );
		}
	}
	
	/**
	 * @see plangame.gwt.client.servermanager.SMRPC#kick(plangame.model.object.BasicID)
	 */
	@Override
	public boolean kick( BasicID clientID ) throws NoServerException, ClientNotConnectedException {
		return getServer( ).kick( getClient( clientID ), DisconnectReason.ServerDisconnect );
	}
	
	/**
	 * @see plangame.gwt.client.servermanager.SMRPC#disconnectAll()
	 */
	@Override
	public int disconnectAll( ) throws NoServerException {
		return getServer( ).kickAll( DisconnectReason.ServerDisconnect );
	}
}
