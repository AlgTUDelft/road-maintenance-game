package plangame.gwt.client.rpc;

import java.util.List;

import plangame.gwt.shared.LogType;
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

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * All RPC methods shared by all the clients
 *
 * @author Joris Scharpff
 */
@RemoteServiceRelativePath ("ClientRPC" )
public interface ClientRPC extends RemoteService {
	/**
	 * Gets a list of active game servers
	 * 
	 * @return The list of game server info
	 * @throws NoServerException 
	 */
	public List<GameServerInfo> getServerList( ) throws NoServerException;
	
	/**
	 * Logs a client side message
	 * 
	 * @param clientID The ID of the client sending the message to log
	 * @param message The message to log
	 * @param loglevel The log level
	 * @return true iff logged successfully
	 * @throws NoServerException 
	 */
	public Boolean log( BasicID clientID, String message, LogType loglevel ) throws NoServerException;

	/**
	 * Listens to the game server for events
	 * 
	 * @param clientID The client that is listening
	 * @return The list of events awaiting processing
	 * @throws EventException if the client is not registered at the server's EM 
	 * @throws ClientNotConnectedException 
	 * @throws NoServerException 
	 */
	public List<Event> listen( BasicID clientID ) throws ClientNotConnectedException, EventException, NoServerException;

	/**
	 * Connects to the main server
	 * 
	 * @param request The (re-)connect request
	 * @return The connection response
	 * @throws NoServerException if the connection failed
	 * @throws InvalidObjectException if the name is invalid
	 * @throws SessionExpiredException if the client was reconnecting but its session no longer exists
	 * @throws GameServerException if it is a reconnect and the reconnect failed at the game server
	 */
	public ConnectResponse connect( ConnectRequest request ) throws NoServerException, InvalidObjectException, SessionExpiredException, GameServerException;

	/**
	 * Disconnects from the main server
	 * 
	 * @param clientID The client to disconnect
	 * @throws ClientNotConnectedException
	 * @throws NoServerException 
	 */
	public void disconnect( BasicID clientID ) throws ClientNotConnectedException, NoServerException;
	
	/**
	 * Signals the server that this client has closed its view, this could be
	 * caused by a page refresh.
	 * 
	 * @param clientID The ID of the client
	 * @throws ClientNotConnectedException 
	 * @throws NoServerException 
	 */
	public void closing( BasicID clientID ) throws ClientNotConnectedException, NoServerException;
}
