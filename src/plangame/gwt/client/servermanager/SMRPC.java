package plangame.gwt.client.servermanager;

import java.util.List;

import plangame.gwt.client.rpc.ClientRPC;
import plangame.gwt.shared.clients.Client;
import plangame.gwt.shared.exceptions.ClientNotConnectedException;
import plangame.gwt.shared.exceptions.GameServerException;
import plangame.gwt.shared.exceptions.InvalidClientTypeException;
import plangame.gwt.shared.exceptions.NoServerException;
import plangame.gwt.shared.exceptions.NoSuchGameServerException;
import plangame.gwt.shared.state.GameServerInfo;
import plangame.model.exceptions.InvalidObjectException;
import plangame.model.object.BasicID;

import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The clients side stub for the RPC service.
 */
@RemoteServiceRelativePath( "SMRPC" )
public interface SMRPC extends ClientRPC {
	/**
	 * Creates a game server
	 * 
	 * @param serverInfo The game server information
	 * @return The updated game server info
	 * @throws GameServerException if the game failed to load
	 * @throws InvalidObjectException if the game ID is invalid
	 * @throws NoServerException if the game cannot be created
	 */
	public GameServerInfo createGameServer( GameServerInfo serverInfo ) throws InvalidObjectException, GameServerException, NoServerException;
	
	/**
	 * Restarts the specified server, kicks all clients from the game
	 * 
	 * @param gameID The ID of the game server to restart
	 * @return The updated server info
	 * @throws GameServerException if the game server failed to restart
	 * @throws NoSuchGameServerException 
	 * @throws NoServerException 
	 */
	public GameServerInfo restartServer( BasicID gameID ) throws GameServerException, NoSuchGameServerException, NoServerException;
	
	/**
	 * Ends the specified server, kicks all clients from the game
	 * 
	 * @param gameID The ID of the game server to end
	 * @return The number of disconnected clients
	 * @throws GameServerException if the server failed to end
	 * @throws NoServerException
	 * @throws NoSuchGameServerException
	 */
	public int endServer( BasicID gameID ) throws GameServerException, NoSuchGameServerException, NoServerException;
	
	/**
	 * Retrieves the list of all currently connected clients
	 * 
	 * @return The list of connected clients
	 * @throws NoServerException
	 */
	public List<Client> getClientList( ) throws NoServerException;
	
	/**
	 * Re-assigns the client to the target client, making the target client
	 * replace its internal client object with the specified client. This can be
	 * used to assign a client to a view and thereby restoring a player in the
	 * game after an unexpected disconnect. The client will receive an event
	 * from the server that notifies it of the replace.
	 * 
	 * @param clientID The client that is reassigned
	 * @param targetID The target client to be replaced (i.e. this client will
	 * still exist afterwards but will now also be the SPClient of client's view)
	 * @throws NoServerException 
	 * @throws InvalidClientTypeException
	 * @throws ClientNotConnectedException
	 */
	public void reassign( BasicID clientID, BasicID targetID ) throws NoServerException, ClientNotConnectedException, InvalidClientTypeException;
	
	/**
	 * Disconnects the specified client from the server
	 * 
	 * @param clientID The ID of the client to disconnect
	 * @return True on success
	 * @throws NoServerException
	 * @throws ClientNotConnectedException
	 */
	public boolean kick( BasicID clientID ) throws NoServerException, ClientNotConnectedException;
	
	/**
	 * Disconnects all active clients from the server
	 * 
	 * @return The number of disconnected clients
	 * @throws NoServerException 
	 */
	public int disconnectAll( ) throws NoServerException;
}
